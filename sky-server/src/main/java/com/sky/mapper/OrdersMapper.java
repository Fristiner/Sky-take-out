package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/* *
 * @packing com.sky.mapper
 * @author mtc
 * @date 14:39 12 01 14:39
 *
 */
@Mapper
public interface OrdersMapper {

    //    @Insert("insert into orders (number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status) VALUES ")
    void insert(Orders orders);


    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);


    @Select("select  * from orders where id = #{id}")
    Orders selectById(Long id);


    Page<OrderVO> pageQueryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    Page<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    @Update("update orders set status = 3 where id = #{id}")
    void updateDeliveryStatus(Long id);


    @Update("update orders set  status =#{status}  where id = #{id};")
    void updateStatus(@Param(value = "status") Integer deliveryInProgress, Long id);


    @Select("SELECT\n" +
            "    SUM(IF(status = 2, 1, 0))  AS to_be_confirmed,\n" +
            "    SUM(IF(status = 3, 1, 0)) AS confirmed,\n" +
            "    SUM(IF(status = 4, 1, 0)) AS delivery_in_progress\n" +
            "FROM orders;")
    OrderStatisticsVO selectStatistics();


    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);


    /**
     * 根据动态条件统计营业额数据
     *
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /*
        根据status 查询数目
     */
    Integer countByMap(Map map);


    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);

    //    @Select("select count(*) from orders where status = #{status}")
    Integer selectCountByStatus(Integer status);
}
