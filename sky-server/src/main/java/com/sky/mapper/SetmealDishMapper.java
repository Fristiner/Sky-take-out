package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/* *
 * @packing com.sky.mapper
 * @author mtc
 * @date 20:44 11 21 20:44
 *
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id来查套餐id
     *
     * @param dishIds
     * @return
     */
    // select setmeal id from setmeal dish where dish_id in (1,2,3)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
