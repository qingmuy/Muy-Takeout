package com.qingmuy.canal;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.qingmuy.entity.Dish;
import com.qingmuy.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Created withIntelliJ IDEA.
 *
 * @author: qingmuy
 * @date:2024/10/8
 * @time:18:01
 * @description : do some thing
 */
// 指定要监听的表
@CanalTable("dish")
@Component
@Slf4j
public class DishHandler implements EntryHandler<Dish> {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 监听数据库插入新数据
     * @param dish 新增菜品数据
     */
    @Override
    public void insert(Dish dish) {
        log.info("监听到新增菜品：{}", dish);
        // 删去不必要字段
        DishVO dishVO = new DishVO();
        BeanUtil.copyProperties(dish, dishVO);
        String dishJson = JSONUtil.toJsonStr(dishVO);
        stringRedisTemplate.opsForHash().put("dish:category" + dish.getCategoryId(), dish.getId().toString(), dishJson);
        stringRedisTemplate.expire("dish:category" + dish.getCategoryId(), 5, TimeUnit.MINUTES);
    }

    /**
     * 监听菜品数据的更新
     * @param before 更新前数据
     * @param after 更新后数据
     */
    @Override
    public void update(Dish before, Dish after) {
        log.info("监听到菜品数据更新：{}", after);
        log.info("监听到菜品数据更新前数据为：{}", before);
        Long categoryId = null;
        if (before.getCategoryId() == null) {
            categoryId = after.getCategoryId();
        } else {
            categoryId = before.getCategoryId();
        }
        // 从Redis中删除数据 重新写入
        stringRedisTemplate.opsForHash().delete("dish:category" + categoryId, after.getId().toString());
        // 删去不必要字段
        DishVO dishVO = new DishVO();
        BeanUtil.copyProperties(after, dishVO);
        String dishJson = JSONUtil.toJsonStr(dishVO);
        stringRedisTemplate.opsForHash().put("dish:category" + after.getCategoryId(), after.getId().toString(), dishJson);
        stringRedisTemplate.expire("dish:category" + after.getCategoryId(), 5, TimeUnit.MINUTES);
    }

    /**
     * 监听数据库删除数据
     * @param dish 菜品数据
     */
    @Override
    public void delete(Dish dish) {
        stringRedisTemplate.opsForHash().delete("dish:category" + dish.getCategoryId(), dish.getId().toString());
    }
}
