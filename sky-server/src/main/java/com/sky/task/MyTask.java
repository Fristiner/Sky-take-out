//package com.sky.task;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//@Slf4j
//public class MyTask {
//
//
//    // 每分钟检查一次是否存在超时订单，如果存在则修改订单状态为“已取消”
//
//    // 每天凌晨一点检查一次是否存在“派送中”的订单，如果存在则修改订单状态为“已经完成”
//
//
//    /**
//     * 每两秒执行一次
//     */
//    @Scheduled(cron = "0/2 * * * * ? ")
//    public void executeTask() {
//        log.info("定时任务开启执行：{}", new Date());
//
//    }
//}
