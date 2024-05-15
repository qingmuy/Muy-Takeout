package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     *
     * @param queryDTO 分页查询数据
     * @return 分页查询结果
     */
    @Override
    public PageResult queryByPage(SetmealPageQueryDTO queryDTO) {
        Page<Setmeal> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        Page<Setmeal> setmealPage = setmealMapper.selectPage(page, null);
        return new PageResult(setmealPage.getTotal(), setmealPage.getRecords());
    }

    /**
     * 批量删除套餐
     * @param lst 套餐列表
     */
    @Override
    public void deleteByIds(ArrayList<Integer> lst) {
        // 查看套餐列表中是否有在售的套餐
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(Setmeal::getStatus, 1)
                .in(Setmeal::getId, lst);
        Long l = setmealMapper.selectCount(qw);

        // 处理包含列表中有在售的情况
        if (l > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

        // 删除套餐
        LambdaQueryWrapper<Setmeal> qw1 = new LambdaQueryWrapper<>();
        qw1.in(Setmeal::getId, lst);
        setmealMapper.delete(qw1);
    }

    @Override
    public SetmealVO queryById(Long id) {
        // 查询出套餐数据
        Setmeal setmeal = setmealMapper.selectById(id);

        // 查询出对应的套餐关系
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId, id);

        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(qw);

        // 复制对象传递值
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Override
    @Transactional
    public void updateDate(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setUpdateUser(BaseContext.getCurrentId());

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealMapper.updateById(setmeal);

        // 插入菜品和套餐关系：分解为删除和插入
        // 删除原先的数据
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId, setmealDTO.getId());
        setmealDishMapper.delete(qw);

        // 重新插入数据
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDTO.getId());
            setmealDishMapper.insert(setmealDish);
        }
    }
}
