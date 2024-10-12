package com.qingmuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingmuy.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    List<ShoppingCart> list(ShoppingCart shoppingCart);
}
