package com.qingmuy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingmuy.dto.DishDTO;
import com.qingmuy.dto.DishPageQueryDTO;
import com.qingmuy.entity.Dish;
import com.qingmuy.result.PageResult;
import com.qingmuy.vo.DishVO;

import java.util.List;

public interface DishService extends IService<Dish> {

    /**
     * 新增菜品和对应的口味
     * @param dishDTO 菜品的信息
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO 分页查询条件
     * @return 分页结果
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     * @param ids 菜品Id
     */
    void deleteByids(List<Long> ids);

    /**
     * 根据Id查询菜品信息
     * @param id 菜品的id
     * @return 菜品的返回对象
     */
    DishVO getVOById(Long id);

    /**
     * 修改菜品信息
     * @param dishDTO 菜品的信息
     */
    void updatee(DishDTO dishDTO);

    /**
     * 修改菜品状态
     * @param status 目标状态
     * @param id 菜品id
     */
    void changeStatus(Integer status, Long id);

    /**
     * 根据分类Id查询菜品
     * @param categoryId 分类id
     * @return 菜品列表
     */
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
