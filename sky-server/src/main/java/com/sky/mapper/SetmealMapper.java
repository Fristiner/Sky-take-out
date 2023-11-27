package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
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
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */

    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增
     *
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
//    @Insert("insert into setmeal (category_id, name, price, description, image, create_time, update_time, create_user, update_user) VALUES " +
//            "(#{categoryId},#{name},#{price},#{description},#{image},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Setmeal setmeal);


    @Select("select * from setmeal where  id = #{id}")
    Setmeal selectById(Long id);
}
