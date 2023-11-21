package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/* *
 * @packing com.sky.mapper
 * @author mtc
 * @date 20:29 11 18 20:29
 *
 */

@Mapper
public interface SetmealMapper {

    /**
     * 根据id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

}
