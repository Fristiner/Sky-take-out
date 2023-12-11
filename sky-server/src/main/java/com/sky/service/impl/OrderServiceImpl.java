package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.OrderStatusException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/* *
 * @packing com.sky.service.impl
 * @author mtc
 * @date 14:37 12 01 14:37
 *
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Value("sky.baidu.ak")
    private String ak;
    @Autowired
    private OrdersMapper orderMapper;
    @Autowired
    private OrdersDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //用户
        // 1.处理各种业务异常

        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //1.1地址是否存在


        //1.2购物车是否存在
        Long userId = BaseContext.getCurrentId();
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        if (list == null || list.isEmpty()) {
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 完成用户下单功能开发
        // 2.向订单表中添加一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
//        orders.setAddress(addressBook.getDetail());
        orders.setUserId(userId);

        orderMapper.insert(orders);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        // 3.向订单明细表中添加多条数据
        for (ShoppingCart cart1 : list) {
            OrderDetail orderDetail = new OrderDetail();// 订单明细
            BeanUtils.copyProperties(cart1, orderDetail);
            // 当前当前订单明细关联的主键值
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        // 4.清空购物车
        shoppingCartMapper.deleteByUserId(userId);
        // 5.封装VO对象返回
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        // 生成空的JSON，跳过微信支付流程
        JSONObject jsonObject = new JSONObject();
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

//        paySuccess(ordersPaymentDTO.getOrderNumber());

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();


        orderMapper.update(orders);
//        // 通过webSocket向客户端浏览器推送消息 type orderId content
//        Map map = new HashMap<>();
//        map.put("type",1);
//        map.put("orderId",ordersDB.getId());
//        map.put("content","订单号"+outTradeNo);
//
//        String json = JSON.toJSONString(map);
//        webSocketServer

        // websocket Server
        // 通过websocket向客户端浏览器推送数据 type orderId content

        Map map = new HashMap<>();
        map.put("type", 1); //1.表示来单提醒 2.表示客户催单
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号" + outTradeNo);
        // 将map转化为json
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO orderDetail(Long id) {
        // 1.根据id查询order表
        OrderVO orderVO = new OrderVO();
        Orders orders = orderMapper.selectById(id);

        AddressBook addressBookMapperById = addressBookMapper.getById(orders.getAddressBookId());
        orders.setAddress(addressBookMapperById.getProvinceName() + addressBookMapperById.getCityName()
                + addressBookMapperById.getDistrictName() + addressBookMapperById.getDetail());

        BeanUtils.copyProperties(orders, orderVO);
        // 2.更具orderId查询 order_detail
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 查询历史订单记录
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 1.获取用户id
        Long UserId = BaseContext.getCurrentId();
        // 2.根据用户id查询数据
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        ordersPageQueryDTO.setUserId(UserId);
        Page<OrderVO> page = orderMapper.pageQueryOrders(ordersPageQueryDTO);
        long total = page.getTotal();
        List<OrderVO> result = page.getResult();
        for (OrderVO order : result) {
            // 根据id查询
            List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(order.getId());
            order.setOrderDetailList(orderDetailList);
        }

        return new PageResult(total, result);
    }

    /**
     * 再来一单，根据订单id
     *
     * @param id
     */
    @Override
    public void repetition(Long id) {
        // 根据订单id
        // 1.查询订单数据


        Long UserId = BaseContext.getCurrentId();

        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(id);

        // 将订单详情对象转化为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(UserId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 2.生成新的订单

        for (ShoppingCart shoppingCart : shoppingCartList) {
            shoppingCartMapper.insert(shoppingCart);
        }
    }

//    @Autowired
//    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public void cancel(Long id) throws Exception {

        // status更改
        // 1.检验订单是否存在
        Orders ordersDB = orderMapper.selectById(id);

        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            // 取消接口
            // 退款接口 并未实现
//            weChatPayUtil.refund(
//                    ordersDB.getNumber(), // 商户订单号
//                    ordersDB.getNumber(), // 商户退款订单号
//                    new BigDecimal(0.01), // 退款金额
//                    new BigDecimal(0.01) // 原订单金额
//            );
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason("用户取消");
        orderMapper.update(orders);
        // 2.订单状态
        //
        // 1.pay_status更改
    }


    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        // phone status page number endTime beginTime number
        // 1.获取userId
        // 1.获取用户id
//        Long UserId = BaseContext.getCurrentId();
        // 2.根据用户id查询数据
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
//        ordersPageQueryDTO.setUserId(UserId);

        Page<Orders> page = orderMapper.conditionSearch(ordersPageQueryDTO);
        // TODO：订单菜品和地址并未查询

        // 根据order_id查询order_detail表 获得dish_id 或者 setmeal_id 然后返回数据
        long total = page.getTotal();
//        List<OrderVO> result = page.getResult();
//        for (OrderVO order : result) {
//            // 根据id查询
//            List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(order.getId());
//            order.setOrderDetailList(orderDetailList);
//        }
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(), orderVOList);

    }


    private List<OrderVO> getOrderVOList(Page<Orders> page) {
// 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // 将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        //
        // 1.更新status 根据id
        orderMapper.updateDeliveryStatus(ordersConfirmDTO.getId());
    }

    /**
     * 派送订单
     *
     * @param id
     */
    @Override
    public void delivery(Long id) {
        //
        // 1.修改状态为4
        // 确保订单状态为3才可以修改
        Orders orders = orderMapper.selectById(id);

        if (orders != null && orders.getStatus().equals(Orders.CONFIRMED)) {
            // 修改订单状态

            orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

            orderMapper.update(orders);
        }


//        Integer status = Orders.DELIVERY_IN_PROGRESS
//        orderMapper.updateStatus(Orders.DELIVERY_IN_PROGRESS, id);

    }

    @Override
    public void complete(Long id) {
        // 更新status 状态

        Orders orders = orderMapper.selectById(id);

        // 判断是否是否是4
        if (orders.getStatus() != Orders.DELIVERY_IN_PROGRESS) {
            throw new OrderStatusException("传入的订单状态不为派送中");
        }

//        Integer status = orders.getStatus();
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
        // 更新delivery_time
    }

    /**
     * 拒绝订单
     *
     * @param ordersRejectionDTO
     */
    @Override
    @Transactional
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 拒绝订单   并且退款
        //

        //1.拒绝原因
        // 2.订单状态

        Orders orders = orderMapper.selectById(ordersRejectionDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());

        orderMapper.update(orders);

        // 3.退款
//        RejectPayment(orders.getId());


    }


    /**
     * 退款根据订单id
     */
    public void RejectPayment(Long id) {

    }


    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     */
    @Override
    public void adminCancelOrders(OrdersCancelDTO ordersCancelDTO) {
        // 取消订单
        // 1.订单已经支付 商家
        // 2. 修改状态
        // 3.设置取消原因
        Orders orders = orderMapper.selectById(ordersCancelDTO.getId());
        // 完成之后无法取消订单
//        if (orders.getStatus().equals(Orders.COMPLETED)) {
//            throw new OrderStatusException("订单已经完成无法取消");
//        }


        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);


        // 4. 退款
//        RejectPayment(orders.getId());
    }

    @Override
    public OrderStatisticsVO statistics() {
        // 获得订单数量信息
        //待接单数量
//        private Integer toBeConfirmed;

        //待派送数量
//        private Integer confirmed;

        //派送中数量
//        private Integer deliveryInProgress;
        OrderStatisticsVO orderStatisticsVO = orderMapper.selectStatistics();
        return orderStatisticsVO;
    }

}
