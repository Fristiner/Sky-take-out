package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public SetmealOverViewVO overviewSetmeals() {
        // 已启售数量
//        private Integer sold;

        Integer sold = setmealMapper.selectByStatus(StatusConstant.ENABLE);
        // 已停售数量
//        private Integer discontinued;
        Integer discontinued = setmealMapper.selectByStatus(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    @Override
    public DishOverViewVO overviewDishes() {
        // 已启售数量
//        private Integer sold;

        // 已停售数量
//        private Integer discontinued;
        Integer sold = dishMapper.selectCountByStatus(StatusConstant.ENABLE);
        Integer discontinued = dishMapper.selectCountByStatus(StatusConstant.DISABLE);

        return DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold).build();

    }

    @Override
    public OrderOverViewVO overviewOrders() {
        //待接单数量
//        private Integer waitingOrders;
        Integer waitingOrders = ordersMapper.selectCountByStatus(Orders.TO_BE_CONFIRMED);

        //待派送数量
//        private Integer deliveredOrders;
        Integer deliveredOrders = ordersMapper.selectCountByStatus(Orders.CONFIRMED);
        //已完成数量
//        private Integer completedOrders;
        Integer completedOrders = ordersMapper.selectCountByStatus(Orders.COMPLETED);

        //已取消数量
//        private Integer cancelledOrders;
        Integer cancelledOrders = ordersMapper.selectCountByStatus(Orders.CANCELLED);
        //全部订单
//        private Integer allOrders;
        Integer allOrders = ordersMapper.selectCountByStatus(null);

        return OrderOverViewVO.builder()
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }

    @Override
    public BusinessDataVO businessData() {
//        private Double turnover;//营业额
        // 今天总数目
        LocalDateTime timeToNow = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.of(LocalDate.from(timeToNow), LocalTime.MAX);
        LocalDateTime begin = LocalDateTime.of(LocalDate.from(timeToNow), LocalTime.MIN);
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", null);
        // 总订单数目
        Integer totalOrders = ordersMapper.countByMap(map);

        map.put("status", Orders.COMPLETED);
        // status =
        Double turnover = ordersMapper.sumByMap(map);
//        private Integer validOrderCount;//有效订单数 已经完成的订单
        Integer validOrderCount = ordersMapper.countByMap(map);
//        private Double orderCompletionRate;//订单完成率
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrders;

//        private Double unitPrice;//平均客单价
        Double unitPrice = turnover / validOrderCount;
        // 新增用户就是 user表
//        private Integer newUsers;//新增用户
        Integer newUsers = userMapper.countByMap(map);

        return BusinessDataVO.builder()
                .newUsers(newUsers)
                .orderCompletionRate(orderCompletionRate)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .validOrderCount(validOrderCount).build();

    }
}
