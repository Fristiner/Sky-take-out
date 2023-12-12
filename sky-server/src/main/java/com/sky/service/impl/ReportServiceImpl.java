package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j

public class ReportServiceImpl implements ReportService {


    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        // 查询数据  返回dataList    turnoverList
        // dataList

        List<LocalDate> dataList = getDataList(begin, end);
        // 查询每个日期的营业额
        List<Double> turnOverList = new ArrayList<>();

        for (LocalDate date : dataList) {
            // 查询date 日期对应的营业额数据，营业额指的是查询已经完成的日期
            // 当前的000 结束时间23 59 59
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();

            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnOver = ordersMapper.sumByMap(map);
            turnOver = turnOver == null ? 0.0 : turnOver;
            turnOverList.add(turnOver);
            // select sum(amount) from orders where order_time > ? and order_time < ? and status = 5;
        }

        // 当前集合用于存放begin到end范围内每天的日期
        return TurnoverReportVO.builder()
                .dateList(getDataListString(begin, end))
                // 查询营业额数据
                .turnoverList(StringUtils.join(turnOverList, ",")).build();
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {

        String dataListString = getDataListString(begin, end);

        List<LocalDate> dataList = getDataList(begin, end);

        // 查询dataList

        // 新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        // select count(id) from user where create_time<？ and create_time > ?
        // 总用户数量
        List<Integer> totalUserList = new ArrayList<>();


        for (LocalDate date : dataList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("end", endTime);
            // 总用户数量
            Integer totalUser = userMapper.countByMap(map);
            map.put("begin", beginTime);

            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }

        return UserReportVO
                .builder()
                .dateList(dataListString)
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
//        private String dateList;

        //每日订单数，以逗号分隔，例如：260,210,215
//        private String orderCountList;

        //每日有效订单数，以逗号分隔，例如：20,21,10
//        private String validOrderCountList;

        //订单总数
//        private Integer totalOrderCount;

        //有效订单数
//        private Integer validOrderCount;

        //订单完成率
//        private Double orderCompletionRate;
        String dataListString = getDataListString(begin, end);

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validCountList = new ArrayList<>();

        for (LocalDate date : getDataList(begin, end)) {
            // 每日订单
            // select count(id) from orders where order_time
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // and status = com
//            ordersMapper
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validCountList.add(validOrderCount);
            // 每日有效订单
        }

        // 订单总数
        // select count(*) orders ;
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();


        // 有效订单数
        Integer validOrderCount = validCountList.stream().reduce(Integer::sum).get();


        // select count(*) orders where id =


        // 订单完成率 = 有效订单/订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = (validOrderCount.doubleValue() / totalOrderCount);
        }

        return OrderReportVO
                .builder()
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .dateList(dataListString)
                .validOrderCountList(StringUtils.join(validCountList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .build();
    }


    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return ordersMapper.countByMap(map);
    }

    /**
     * 日期计算，计算指定日期的后一天对应的日期
     *
     * @return String
     */
    public String getDataListString(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        return StringUtils.join(dateList, ",");
    }

    public List<LocalDate> getDataList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }


}
