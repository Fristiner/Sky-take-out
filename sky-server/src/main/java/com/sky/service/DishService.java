package com.sky.service;

import com.sky.dto.DishDTO;

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
}
