package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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


    /**
     * 查询通过id来查
     *
     * @param id
     * @return
     */
    @Select("select * from setmeal where  id = #{id}")
    Setmeal selectById(Long id);

    @Select("select * from setmeal where category_id = #{categoryId} and status = '1' ")
    List<Setmeal> userList(String categoryId);


    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> selectByIdWithDishItem(String id);

//    @Delete("delete from setmeal where id in (1,2,3)")

    void deleteByIds(List<Long> ids);

    /**
     * 设置套餐的起售停售
     *
     * @param id
     * @param status
     */

    @Update("update setmeal set status = #{status} where id = #{id};")
    void StartOrStop(String id, Integer status);


    void update(Setmeal setmeal);

    @Select("select count(*) from setmeal where status = #{status}")
    Integer selectByStatus(Integer status);
}
