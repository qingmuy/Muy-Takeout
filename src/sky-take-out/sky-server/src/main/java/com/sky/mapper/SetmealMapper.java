package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {

    /**
     * 根据分类id查询套餐的数量
     * @param id 传入的分类ID
     * @return 该分类下套餐的数量
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增菜品
     * @param setmeal 菜品信息
     */
    @AutoFill(OperationType.INSERT)
    void addnewmeal(Setmeal setmeal);

    /**
     * 修改套餐的状态：起售/停售
     * @param setmeal 套餐信息
     */
    @AutoFill(OperationType.UPDATE)
    void updateByIdButAutoFill(Setmeal setmeal);

    /**
     * 动态条件查询套餐
     * @param setmeal 套餐信息
     * @return 套餐的详细信息
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId 套餐ID
     * @return 菜品的详细信息
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
