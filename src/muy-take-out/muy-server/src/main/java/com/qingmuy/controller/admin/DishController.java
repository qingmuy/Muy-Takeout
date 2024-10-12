package com.qingmuy.controller.admin;

import com.qingmuy.dto.DishDTO;
import com.qingmuy.dto.DishPageQueryDTO;
import com.qingmuy.entity.Dish;
import com.qingmuy.result.PageResult;
import com.qingmuy.result.Result;
import com.qingmuy.service.DishService;
import com.qingmuy.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Resource
    DishService dishService;

    @Resource
    RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO 要新增的菜品的信息
     * @return 成功增添信息
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);

        dishService.saveWithFlavor(dishDTO);

        // 清理Redis中的缓存
        cleanCache("dish_" + dishDTO.getCategoryId());

        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 菜品分页查询条件
     * @return 分页查询的结果
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     *
     * @param ids 菜品id数组
     * @return 是否成功删除
     */
    @DeleteMapping
    @ApiOperation("菜品的批量删除")
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteByids(ids);

        // 清理Redis缓存
        cleanCache("dish_*");

        return Result.success();
    }


    /**
     * 根据菜品id查询菜品的详细信息
     *
     * @param id 菜品的id
     * @return 菜品的详细id
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO res = dishService.getVOById(id);
        return Result.success(res);
    }

    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        dishService.updatee(dishDTO);

        // 清空Redis中的缓存
        cleanCache("dish_" + dishDTO.getCategoryId());

        return Result.success();
    }

    /**
     * 修改菜品状态
     *
     * @param status 修改后状态
     * @param id     菜品id
     * @return 修改成功
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品状态")
    public Result<String> changeStatus(@PathVariable Integer status, Long id) {
        dishService.changeStatus(status, id);

        // 清空Redis中的缓存
        cleanCache("dish_" + dishService.getById(id).getCategoryId());

        return Result.success();
    }

    /**
     * 根据分类Id查询菜品
     * @param categoryId 分类ID
     * @return 菜品列表
     */
    @GetMapping("/list")
    @ApiOperation("根据分类Id查询菜品")
    public Result<List<Dish>> queryDishByCategoryId(Long categoryId) {
        List<Dish> dishes = dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     * 清理Redis缓存
     * @param pattern
     */
    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
