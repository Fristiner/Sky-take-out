package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */

@Component
@Slf4j
public class OrderTask {
    //


    @Autowired
    private OrdersMapper ordersMapper;
//    @Scheduled(cron = "0/5 * * * * ? ")

    /**
     * 处理超时订单
     */
    // 每分钟触发一次
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder() {
        log.info("定时处理超时订单：{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        // select * from orders where status = ? and orders_time < (当前时间 - 15分钟)
        List<Orders> orderList = ordersMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if (orderList != null && orderList.size() > 0) {
            for (Orders orders : orderList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                ordersMapper.update(orders);
            }
        }
    }

    //处理一直处理派送中的方法


    // 每天凌晨一点触发一次

    /**
     * 处理一直处于派送中的状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("定时处理处于派送中的订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> orderList = ordersMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if (orderList != null && orderList.size() > 0) {
            for (Orders orders : orderList) {
                orders.setStatus(Orders.COMPLETED);
                ordersMapper.update(orders);
            }
        }
    }


}
