package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/* *
 * @packing com.sky.service.impl
 * @author mtc
 * @date 22:57 11 21 22:57
 *
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        //TODO：套餐分类名称并未并没查到需要实现
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        // 把结果每个插入
        PageResult pageResult = new PageResult();

//        List records = pageResult.getRecords();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Override
    public void save(SetmealDTO setmealDTO) {
        // 1.添加数据到setmeal中
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        //setmeal_dish表中的数据添加
        //
        // setmeal_id 并没有添加
        // 2.添加数据时获得id

        // 3. 添加setmeal_dish表中数据
        Long id = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish dish : setmealDishes) {
            dish.setSetmealId(id);
            setmealDishMapper.insert(dish);
        }
    }

    @Override
    public SetmealVO selectById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        // 1.通过id查询setmeal表
        Setmeal setmeal = setmealMapper.selectById(id);
        BeanUtils.copyProperties(setmeal, setmealVO);

        // 2.通过id查询setmeal_dish表
        List<SetmealDish> setmealDish = setmealDishMapper.selectById(setmeal.getId());
        setmealVO.setSetmealDishes(setmealDish);
        return setmealVO;
    }

    @Override
    public List<Setmeal> userList(String categoryId) {
        // 查询
        List<Setmeal> setmealList = setmealMapper.userList(categoryId);
        return setmealList;
    }

    /**
     * 根据id查询套餐类菜品
     *
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> selectByIDWithDishItem(String id) {

        return setmealMapper.selectByIdWithDishItem(id);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void delete(List<Long> ids) {
        // TODO：

        // 1.删除setmeal中数据
        setmealMapper.deleteByIds(ids);
        // 2.删除setmeal_dish中数据
        setmealDishMapper.deleteByIds(ids);


    }

    /**
     * 套餐的起售停售
     * @param id
     * @param status
     */
    @Override
    public void StartOrStop(Long id, Integer status) {
        // 1.根据id来设置
        // 起售套餐时，判断套餐内是否有停售菜品，如果有提示“套餐内包含未起售菜品”
//起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if(status == StatusConstant.ENABLE){
            //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList != null && !dishList.isEmpty()){
                dishList.forEach(dish -> {
                    if(StatusConstant.DISABLE == dish.getStatus()){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);


    }

    /**
     * 修改套餐接口
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // 1.修改套餐表，执行update
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        Long id = setmeal.getId();
        // 2.删除套餐和菜品相关的关系，操作setmeal_dish表，执行delete
        setmealDishMapper.deleteBySetmealId(id);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish dish : setmealDishes) {
            dish.setSetmealId(id);
            setmealDishMapper.insert(dish);
        }
    }


}
