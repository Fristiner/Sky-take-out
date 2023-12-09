package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* *
 * @packing com.sky.controller.admin
 * @author mtc
 * @date 22:56 11 21 22:56
 *
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 分页查询
        //
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
//    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    @PostMapping()
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐，{}", setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id来查询套餐
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id来查询套餐")
    public Result<SetmealVO> selectById(@PathVariable("id") Long id) {
        //
        SetmealVO setmealVO = setmealService.selectById(id);
        return Result.success(setmealVO);
    }
    // TODO：缓存套餐模块并未完成

    @DeleteMapping()
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        // TODO: 批量删除套餐
        setmealService.delete(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐的起售停售")
    public Result StartOrStop(@PathVariable(value = "status") Integer status,Long id){
        // 1. 1表示起售 0 表示停售
        // 根据id来处理
        setmealService.StartOrStop(id,status);
        return Result.success();
    }


    @PutMapping()
    @ApiOperation("修改套餐接口")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        // 修改套餐接口
        setmealService.update(setmealDTO);
        return Result.success();
    }



}
