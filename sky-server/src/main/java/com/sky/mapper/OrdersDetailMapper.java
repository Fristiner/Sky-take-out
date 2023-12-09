package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/* *
 * @packing com.sky.mapper
 * @author mtc
 * @date 14:40 12 01 14:40
 *
 */
@Mapper
public interface OrdersDetailMapper {
    /**
     * 批量插入订单明细数据
     *
     * @param orderDetailList
     */
//    @Insert("insert into order_detail (name, image, order_id, dish_id, setmeal_id, dish_flavor, amount) VALUES ")
    void insertBatch(List<OrderDetail> orderDetailList);


    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> selectByOrderId(Long id);


}
