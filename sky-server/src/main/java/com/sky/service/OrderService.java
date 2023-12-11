package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/* *
 * @packing com.sky.service
 * @author mtc
 * @date 14:37 12 01 14:37
 *
 */
public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);


    OrderVO orderDetail(Long id);


    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    void repetition(Long id);


    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);


    void confirm(OrdersConfirmDTO ordersConfirmDTO);


    void delivery(Long id);


    void complete(Long id);


    void rejection(OrdersRejectionDTO ordersRejectionDTO);


    void cancel(Long id) throws Exception;


    void adminCancelOrders(OrdersCancelDTO ordersCancelDTO);


    OrderStatisticsVO statistics();
}
