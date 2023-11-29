package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

/* *
 * @packing com.sky.service
 * @author mtc
 * @date 20:28 11 18 20:28
 *
 */
public interface CategoryService {


    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);


    void save(CategoryDTO categoryDTO);

    /**
     * 启用 禁用分类
     *
     * @param status
     * @param id
     */
    void StartOrStop(Integer status, Long id);

    void deleteById(Long id);


    void Update(CategoryDTO categoryDTO);


    List<Category> selectByType(Integer type);

    List<Category> userList(Integer type);
}
