package com.qingmuy.controller.admin;

import com.qingmuy.dto.SetmealDTO;
import com.qingmuy.dto.SetmealPageQueryDTO;
import com.qingmuy.result.PageResult;
import com.qingmuy.result.Result;
import com.qingmuy.service.SetMealService;
import com.qingmuy.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;

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
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
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

    /**
     * 批量删除套餐
     * @param ids 套餐id列表
     * @return 是否成功删除
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> delete(String ids) {
        String[] split = ids.split(",");
        ArrayList<Integer> lst = new ArrayList<>();
        for (String num : split) {
            lst.add(Integer.parseInt(num));
        }
        setMealService.deleteByIds(lst);
        return Result.success();
    }

    /**
     * 根据id查询套餐信息
     * @param id 套餐id
     * @return 套餐的信息
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐信息")
    public Result<SetmealVO> queryById(@PathVariable Long id) {
        SetmealVO SetmealVO = setMealService.queryById(id);
        return Result.success(SetmealVO);
    }

    /**
     * 修改套餐数据
     * @param setmealDTO 套餐的信息
     * @return 是否修改成功
     */
    @PutMapping
    @ApiOperation("修改套餐数据")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        setMealService.updateDate(setmealDTO);
        return Result.success();
    }

    /**
     * 修改套餐起售/停售状态
     * @param status 状态
     * @param id 套餐id
     * @return 成功修改
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改套餐状态")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> changeStatus(@PathVariable Integer status, Long id) {
        setMealService.changeStatus(id, status);
        return Result.success();
    }

}
