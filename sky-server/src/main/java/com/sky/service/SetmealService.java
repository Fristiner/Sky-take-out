package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/* *
 * @packing com.sky.service
 * @author mtc
 * @date 22:56 11 21 22:56
 *
 */
public interface SetmealService {
    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);


    SetmealVO selectById(Long id);


    List<Setmeal> userList(String categoryId);


    List<DishItemVO> selectByIDWithDishItem(String id);
}
