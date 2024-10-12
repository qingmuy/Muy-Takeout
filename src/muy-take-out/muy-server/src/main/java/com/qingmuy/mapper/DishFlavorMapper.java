package com.qingmuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingmuy.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {

    /**
     * 针对不同的口味插入相关的菜品对象
     * @param flavors 口味标签
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据ID列表删除对应的口味
     * @param ids 菜品id列表
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据ID列表删除对应的口味
     * @param id 菜品的id
     */
    void deleteByDishId(Long id);

    /**
     * 根据菜品的id查询口味
     * @param id 菜品id
     * @return 口味列表
     */
    @Select("select * from sky_take_out.dish_flavor where dish_id = #{id}")
    List<DishFlavor> selectByDishID(Long id);


}
