package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Resource
    ShoppingCartMapper shoppingCartMapper;

    @Resource
    DishMapper dishMapper;

    @Resource
    SetmealMapper setmealMapper;

    /**
     * 购物车添加数据
     * @param shoppingCartDTO 购物数据
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 判断当前假如到购物车中的商品是否已经存在了
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        // 如果已经存在了，只需要将数量加一
        if (list != null && !list.isEmpty()) {
            ShoppingCart shoppingCart1 = list.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            this.updateById(shoppingCart1);
        }else {
            // 如果不存在，需要插入一条购物车数据

            // 判断本次添加的是菜品还是套餐
            if (shoppingCartDTO.getDishId() != null) {
                // 添加的数据是菜品

                Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            }else{
                // 添加的数据是套餐
                Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());

                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            this.save(shoppingCart);
        }

    }

    /**
     * 展示购物车所有商品
     * @return 购物车所有商品
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        return shoppingCartMapper.selectList(qw);
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartMapper.delete(qw);
    }
}
