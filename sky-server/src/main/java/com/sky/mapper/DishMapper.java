package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/* *
 * @packing com.sky.mapper
 * @author mtc
 * @date 20:29 11 18 20:29
 *
 */
@Mapper
public interface DishMapper {
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);


    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 根据id来查
     *
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据ids批量删除对应数据
     *
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectByCategoryId(Long categoryId);

    @Select("select * from dish where category_id = #{catagoryId} and status = '1'")
    List<Dish> userList(String categoryId);

    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long id);


    @Select("select count(*) from dish where status = #{status}")
    Integer selectCountByStatus(Integer status);
}
