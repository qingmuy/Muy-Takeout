package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("user/shoppingCart")
@Slf4j
@Api(tags = "C端购物车相关接口")
public class ShoppongCartController {

    @Resource
    ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO 购物数据
     * @return 是否成功
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车，购物信息为：{}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车所有数据
     * @return 购物车所有数据
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> showShoppingCart() {
        List<ShoppingCart> shoppingCartList = shoppingCartService.showShoppingCart();
        return Result.success(shoppingCartList);
    }


    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result<String> clean() {
        shoppingCartService.clean();
        return Result.success();
    }
}
