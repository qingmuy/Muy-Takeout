package com.qingmuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingmuy.annotation.AutoFill;
import com.qingmuy.dto.DishPageQueryDTO;
import com.qingmuy.entity.Dish;
import com.qingmuy.enumeration.OperationType;
import com.qingmuy.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId 传入的分类ID
     * @return 该分类下菜品数量
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品
     * @param dish 菜品信息
     */
    @AutoFill(value = OperationType.INSERT)
    void inserte(Dish dish);

    /**
     * 分页查询菜品信息
     * @param dishPageQueryDTO 菜品的分页查询条件
     * @return 分页查询结果
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 批量删除菜品口味信息
     * @param ids 菜品id列表
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
