package com.sky.controller.admin;

/* *
 * @packing com.sky.controller.admin
 * @author mtc
 * @date 20:28 11 18 20:28
 *
 */

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "套餐管理相关接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("套餐分页查询,参数为:{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {

        log.info("新增分类,{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 启用、禁用分类
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用,禁用分类")
    public Result StartOrStop(@PathVariable("status") Integer status, Long id) {
        log.info("启用,禁用分类,{},{}", status, id);
        categoryService.StartOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping()
    @ApiOperation("根据id删除分类")
    public Result deleteById(Long id) {
        log.info("删除{}的数据", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类");
        // TODO：去完成这部分
        categoryService.Update(categoryDTO);
        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> selectByType(Integer type) {
        // 1.对数据进行校验
        if (type != 0 || type != 1) {
            return Result.error("传输的类型错误");
        }
        // 2.开始处理

        List<Category> list = categoryService.selectByType(type);
        return Result.success(list);
    }

}
