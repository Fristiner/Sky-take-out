package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/* *
 * @packing com.sky.service
 * @author mtc
 * @date 17:33 11 21 17:33
 *
 */

public interface DishService {


    /**
     * 新增菜品和口为数据
     *
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);


    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    void deleteBatch(List<Long> ids);


    DishVO selectDishWithFlavor(Long id);


    void updateWithFlavor(DishDTO dishDTO);


    void StartOrStop(Long id, Integer status);

    List<Dish> selectList(Long categoryId);
}
