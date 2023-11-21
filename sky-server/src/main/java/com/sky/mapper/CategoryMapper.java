package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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
public interface CategoryMapper {

    Page<Category> pageQueryCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) VALUES " +
            "(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Category category);

    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    @Select("select * from category where id = #{id}")
    Category selectById(Long id);


    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);


    List<Category> selectByType(Integer type);
}
