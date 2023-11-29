package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/* *
 * @packing com.sky.controller.user
 * @author mtc
 * @date 13:47 11 28 13:47
 *
 */
@RestController("userCategoryController")
@Slf4j
@RequestMapping("/user/category")
@Api(tags = "用户端分类接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation("条件查询接口")
    public Result<List<Category>> list(Integer type) {
        List<Category> list = categoryService.userList(type);
        return Result.success(list);
    }
}
