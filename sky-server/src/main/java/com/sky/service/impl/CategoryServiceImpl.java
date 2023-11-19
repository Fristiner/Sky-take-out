package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/* *
 * @packing com.sky.service.impl
 * @author mtc
 * @date 20:28 11 18 20:28
 *
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    // // select * from employee limit 0,10
    //        // 开始分页查询
    //        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
    //        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
    //        long total = page.getTotal();
    //        List<Employee> records = page.getResult();
    //
    //        PageResult pageResult = new PageResult(total, records);
    //
    //        return pageResult;

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQueryCategory(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> records = page.getResult();
        PageResult pageResult = new PageResult(total, records);
        return pageResult;
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     */

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        Long currentId = BaseContext.getCurrentId();
//        category.setCreateUser(currentId);
//        category.setUpdateUser(currentId);
        categoryMapper.insert(category);
    }

    /**
     * 启用 禁用分类
     *
     * @param status
     * @param id
     */

    @Override
    public void StartOrStop(Integer status, Long id) {
        // 1. 通过id选择出来对应的数据
        Category category = categoryMapper.selectById(id);
        // 2.
        category.setStatus(status);
        // 更新时间
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        // 更新用户id
        categoryMapper.update(category);
    }

    @Override
    public void deleteById(Long id) {
        // TODO：实现删除功能
        //  如果当前分类下面有菜品不能删除
        //  如果当前分类下面有套餐，不能删除
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    @Override
    public void Update(CategoryDTO categoryDTO) {
        //1.根据id查找处相关数据
        Category category = new Category();
        // 2.导入数据
//        // 需要判断导入的数据不为null
//        if (categoryDTO.getName() != null) {
//            category.setName(categoryDTO.getName());
//        }
//        if (category.getSort() != null) {
//            category.setSort(categoryDTO.getSort());
//        }
//        if (category.getType() != null) {
//            category.setType(categoryDTO.getType());
//        }
//
        BeanUtils.copyProperties(categoryDTO, category);
//        category.setUpdateUser(BaseContext.getCurrentId());
//        category.setUpdateTime(LocalDateTime.now());
        //3.更新数据
        categoryMapper.update(category);

        // TODO：实现对数据添加没成功的判断
        // org.apache.ibatis.reflection.ReflectionException:
        // There is no getter for property named 'create_time' in 'class com.sky.entity.Category'
    }

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    @Override
    public List<Category> selectByType(Integer type) {
        //
        List<Category> categories = categoryMapper.selectByType(type);

        return categories;
    }
}
