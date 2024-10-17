package com.qingmuy.controller.user;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.qingmuy.constant.StatusConstant;
import com.qingmuy.entity.Category;
import com.qingmuy.entity.Dish;
import com.qingmuy.mapper.CategoryMapper;
import com.qingmuy.result.Result;
import com.qingmuy.service.DishService;
import com.qingmuy.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {

    @Resource
    CategoryMapper categoryMapper;

    @Resource
    DishService dishService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedissonClient redissonClient;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {

        // 构造redis中的key，规则：dish_分类id
        String key = "dish:category" + categoryId;

        // 查询redis中是否存在菜品数据
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        // 判断取出的值是否为防止缓存穿透的特殊值
        if (entries.containsKey("NULL") && entries.get("NULL").equals("NULL")) {
            return Result.success();
        }

        List<DishVO> list = new ArrayList<>();
        for (Object json:entries.values()){
            DishVO dishVO = JSONUtil.toBean((String) json, DishVO.class);
            list.add(dishVO);
        }

        if (list != null && !list.isEmpty()) {
            // 如果存在直接返回缓存中的结果即可，无需再查询数据库
            stringRedisTemplate.expire(key, 5, TimeUnit.MINUTES);
            return Result.success(list);
        }

        // 判断该分类id是否合法，解决缓存穿透问题
        // 1 查询当前分类id是否存在
        Category category = categoryMapper.selectById(categoryId);
        // 2 不存在则直接向Redis中存储空字段
        if (category == null) {
            stringRedisTemplate.opsForHash().put(key, "NULL", "NULL");
            stringRedisTemplate.expire(key, 5, TimeUnit.MINUTES);
            return Result.success();
        }

        // 上锁，防止缓存击穿
        // RedisLockUtil redisLockUtil = new RedisLockUtil(key, stringRedisTemplate);
        RLock lock = redissonClient.getLock("lock:" + key);
        // 默认等待30s
        boolean isLock = lock.tryLock();
        if (!isLock) {
            return Result.error("系统内部异常");
        }

        try {
            Dish dish = new Dish();
            dish.setCategoryId(categoryId);
            dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

            // 如果不存在，查询数据，经查询到的数据放入redis中
            list = dishService.listWithFlavor(dish);
            for (DishVO dishVO : list) {
                String dishJson = JSONUtil.toJsonStr(dishVO);
                stringRedisTemplate.opsForHash().put("dish:category" + dishVO.getCategoryId(), dishVO.getId().toString(), dishJson);
            }
            stringRedisTemplate.expire("dish:category" + categoryId, 300 + RandomUtil.randomInt(60), TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }
        return Result.success(list);
    }

}
