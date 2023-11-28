package com.sky.controller.user;

/* *
 * @packing com.sky.controller.user
 * @author mtc
 * @date 19:59 11 28 19:59
 *
 */

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetmealController")
@Slf4j
@RequestMapping("/user/setmeal")
@Api(tags = "套餐浏览接口")
public class setmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    public Result<List<Setmeal>> userList(String categoryId) {
        List<Setmeal> setmealList = setmealService.userList(categoryId);
        return Result.success(setmealList);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> select(@PathVariable String id) {
        //
        List<DishItemVO> list = setmealService.selectByIDWithDishItem(id);

        return Result.success(list);

    }

}
