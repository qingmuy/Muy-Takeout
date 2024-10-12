package com.qingmuy.controller.user;

import cn.hutool.json.JSONUtil;
import com.qingmuy.constant.StatusConstant;
import com.qingmuy.entity.Dish;
import com.qingmuy.result.Result;
import com.qingmuy.service.DishService;
import com.qingmuy.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
    DishService dishService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

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
        List<DishVO> list = new ArrayList<>();
        for (Object json:entries.values()){
            DishVO dishVO = JSONUtil.toBean((String) json, DishVO.class);
            list.add(dishVO);
        }

        if (list != null && !list.isEmpty()) {
            // 如果存在直接返回缓存中的结果即可，无需再查询数据库
            stringRedisTemplate.expire("dish:category" + categoryId, 5, TimeUnit.MINUTES);
            return Result.success(list);
        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        // 如果不存在，查询数据，经查询到的数据放入redis中
        list = dishService.listWithFlavor(dish);
        for (DishVO dishVO : list) {
            String dishJson = JSONUtil.toJsonStr(dishVO);
            stringRedisTemplate.opsForHash().put("dish:category" + dishVO.getCategoryId(), dishVO.getId().toString(), dishJson);
        }
        stringRedisTemplate.expire("dish:category" + categoryId, 5, TimeUnit.MINUTES);
        return Result.success(list);
    }

}
