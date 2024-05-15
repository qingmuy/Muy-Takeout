package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetMealService {

    @Resource
    SetmealMapper setmealMapper;

    @Resource
    SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void addmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 套餐表中插入信息
        setmealMapper.addnewmeal(setmeal);

        // 套餐菜品关系表中插入数据
        for (SetmealDish setmealDish : setmealDTO.getSetmealDishes()) {
            setmealDishMapper.insert(setmealDish);
        }
    }

    /**
     * 分页查询套餐数据
     * @param queryDTO 分页查询数据
     * @return 分页查询结果
     */
    @Override
    public PageResult queryByPage(SetmealPageQueryDTO queryDTO) {
        Page<Setmeal> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        Page<Setmeal> setmealPage = setmealMapper.selectPage(page, null);
        return new PageResult(setmealPage.getTotal(), setmealPage.getRecords());
    }
}
