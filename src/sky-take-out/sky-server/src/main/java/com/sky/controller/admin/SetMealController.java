package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetMealController {

    @Resource
    SetMealService setMealService;

    /**
     * 新增套餐
     * @param setmealDTO 新增的套餐信息
     * @return 是否成功添加信息
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result<String> addmeal(@RequestBody SetmealDTO setmealDTO) {
        setMealService.addmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param queryDTO 分页插叙的条件
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> queryByPage(SetmealPageQueryDTO queryDTO) {
        PageResult pageResult = setMealService.queryByPage(queryDTO);
        return Result.success(pageResult);
    }

}
