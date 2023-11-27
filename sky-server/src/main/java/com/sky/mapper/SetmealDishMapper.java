package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 新增多条数据
     *
     * @param setmealDishes
     */


    @Insert("insert into setmeal_dish (setmeal_id, dish_id, name, price, copies) VALUES " +
            "(#{setmealId},#{dishId},#{name},#{price},#{copies})")
    void insert(SetmealDish setmealDishes);

    @Select("select * from setmeal_dish where setmeal_id = #{setmealId};")
    List<SetmealDish> selectById(Long setmealId);
}
