package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/* *
 * @packing com.sky.service.impl
 * @author mtc
 * @date 17:33 11 21 17:33
 *
 */

@Service
@Slf4j

public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;


    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    /**
     * 新增菜品和口味数据
     *
     * @param dishDTO
     */
    // 需要保障数据的一致性
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        // 向菜品表添加1条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        // 获取insert生成的主键值

        Long dishId = dish.getId();

        // 向口味表中插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }


    }
}
