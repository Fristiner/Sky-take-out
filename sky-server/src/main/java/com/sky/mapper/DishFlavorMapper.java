package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/* *
 * @packing com.sky.mapper
 * @author mtc
 * @date 17:47 11 21 17:47
 *
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入多个数据
     *
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);
}
