package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/* *
 * @packing com.sky.mapper
 * @author mtc
 * @date 17:47 11 21 17:47
 *
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入多个数据
     *
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id来删除对应口味
     *
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据ids批量删除对应口味
     *
     * @param ids
     */
    void deleteByDishIds(List<Long> ids);

    /**
     * 根据dishId查询口味
     *
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> selectFlavorWithId(Long dishId);
}
