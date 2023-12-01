package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/* *
 * @packing com.sky.service
 * @author mtc
 * @date 14:37 12 01 14:37
 *
 */
public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
