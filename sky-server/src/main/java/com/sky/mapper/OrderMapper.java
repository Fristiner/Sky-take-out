package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/* *
 * @packing com.sky.mapper
 * @author mtc
 * @date 14:39 12 01 14:39
 *
 */
@Mapper
public interface OrderMapper {

    //    @Insert("insert into orders (number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status) VALUES ")
    void insert(Orders orders);
}
