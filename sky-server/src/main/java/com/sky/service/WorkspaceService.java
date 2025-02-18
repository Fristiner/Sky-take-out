package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkspaceService {


    SetmealOverViewVO overviewSetmeals();


    DishOverViewVO overviewDishes();

    OrderOverViewVO overviewOrders();


    BusinessDataVO businessData(LocalDateTime begin, LocalDateTime end);
}
