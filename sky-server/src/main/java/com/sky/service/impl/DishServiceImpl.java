package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    private SetmealDishMapper setmealDishMapper;


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

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        // 1.可以一次删除一个 也可以批量删除
        // 2. 起售的菜品不能删除
        // 3. 被套餐关联的菜品不能删除
        //  4. 删除菜品后，关联的口为数据也要删除
        // 判断当前菜品是否能够删除-- 是否存在启售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish != null && dish.getStatus() == StatusConstant.ENABLE) {
                // 当前菜品在
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 判断当前菜品是否能够删除 -- 是否被套餐关联了
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            // 当前菜品被套餐关联不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 删除菜品表中的菜品数据
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            // 删除菜品关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        // 根据菜品id集合批量删除菜品数据
        //sql delete from dish where id in (?,?,?)
        dishMapper.deleteByIds(ids);
        // 根据菜品id集合批量删除菜品口味数据
        //sql delete from dish_flavor where dish_id in (?,?,?)
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public DishVO selectDishWithFlavor(Long id) {
        // 1.查询菜品根据id
        Dish dish = dishMapper.getById(id);
        //2.根据id查询falvor
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectFlavorWithId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 根据id修改菜品信息和口味信息
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //根据id修改菜品信息

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        // 先把传过来的数据全部删掉
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        // 再重新插入实现修改
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 起售停售功能
     *
     * @param id
     * @param status
     */
    @Override
    public void StartOrStop(Long id, Integer status) {
        //1.根据id来查找
        Dish dishMapperById = dishMapper.getById(id);

        dishMapperById.setStatus(status);
        //2.update
        dishMapper.update(dishMapperById);
    }

    @Override
    public List<Dish> selectList(Long categoryId) {
        // 根据categoryId来进行查询
        return dishMapper.selectByCategoryId(categoryId);
    }


}
