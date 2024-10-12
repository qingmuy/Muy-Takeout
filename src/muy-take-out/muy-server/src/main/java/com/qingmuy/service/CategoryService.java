package com.qingmuy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingmuy.dto.CategoryDTO;
import com.qingmuy.dto.CategoryPageQueryDTO;
import com.qingmuy.entity.Category;
import com.qingmuy.result.PageResult;
import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 新增分类
     * @param categoryDTO 分类信息
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询
     * @param categoryPageQueryDTO 分页查询数据
     * @return 分页数据
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id 分类ID
     */
    void deleteById(Long id);

    /**
     * 修改分类
     * @param categoryDTO 修改后的分类数据
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用、禁用分类
     * @param status 是否启/禁用分类
     * @param id 分类ID
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类
     * @param type 分类的类型
     * @return  分类的列表
     */
    List<Category> list(Integer type);
}
