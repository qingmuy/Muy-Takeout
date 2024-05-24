package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加购物车
     * @param shoppingCartDTO 购物数据
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 展示购物车的所有商品
     * @return 购物车内的所有商品
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     */
    void clean();
}
