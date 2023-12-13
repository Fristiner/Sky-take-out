package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j

public class ReportServiceImpl implements ReportService {


    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrdersDetailMapper ordersDetailMapper;
    @Autowired
    private WorkspaceService workspaceService;

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

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesDTO = ordersMapper.getSalesTop10(beginTime, endTime);

        List<String> nameList = salesDTO.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());

        List<Integer> numberList = salesDTO.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());


        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    /**
     * 导出运营数据报表
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 1.查询数据库获取营业数据 -- 查询最近30天运营数据
        // 2.营业额 订单完成率  新增用户数 有效订单  平均客单价
        LocalDate dataBegin = LocalDate.now().minusDays(30);
        LocalDate dataEnd = LocalDate.now().minusDays(1);
        LocalDateTime begin = LocalDateTime.of(dataBegin, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(dataEnd, LocalTime.MAX);
        BusinessDataVO businessDataVO = workspaceService.businessData(begin, end);
        // 获取vo对象
        // 基于模板文件创建新的文件
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("template/muban.xlsx");
        if (stream == null) {
            return;
        }

        try {
            XSSFWorkbook excel = new XSSFWorkbook(stream);

            // 填充数据--时间
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dataBegin + "至" + dataEnd);

            //获得第四行
            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());

            // 获得第五行
            XSSFRow row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());
            // 添加明细数据

            for (int i = 0; i < 30; i++) {
                LocalDate date = dataBegin.plusDays(i);
                LocalDateTime newBegin = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime newEnd = LocalDateTime.of(date, LocalTime.MAX);
                BusinessDataVO dataVO = workspaceService.businessData(newBegin, newEnd);
                // 第一次第八行
                // 应该判断一下如果返回为null则怎么样
                XSSFRow sheetRow = sheet.getRow(7 + i);
                sheetRow.getCell(1).setCellValue(String.valueOf(date));
                sheetRow.getCell(2).setCellValue(dataVO.getTurnover());
                sheetRow.getCell(3).setCellValue(dataVO.getValidOrderCount());
                sheetRow.getCell(4).setCellValue(dataVO.getOrderCompletionRate());
                sheetRow.getCell(5).setCellValue(dataVO.getUnitPrice());
                sheetRow.getCell(6).setCellValue(dataVO.getNewUsers());
            }

            ServletOutputStream responseOutputStream = response.getOutputStream();

            excel.write(responseOutputStream);

            // 关闭资源
            responseOutputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 2.查询数据库写入Excel文件中


        // 3.通过输出流 将excel文件下载到客户端浏览器中


    }


    //商品名称列表，以逗号分隔，例如：鱼香肉丝,宫保鸡丁,水煮鱼
//        private String nameList;

    //销量列表，以逗号分隔，例如：260,215,200
//        private String numberList;
//        select od.name,sum(od.number) aa from order_detail  od ,orders o where od.order_id = o.id
//        and o.status = 5 group by od.name order by aa desc limit 0,10;


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
