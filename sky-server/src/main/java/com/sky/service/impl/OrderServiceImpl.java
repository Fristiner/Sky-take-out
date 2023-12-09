package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/* *
 * @packing com.sky.service.impl
 * @author mtc
 * @date 14:37 12 01 14:37
 *
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

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
        //TODO：再来一单

        // 2.生成新的订单


    }

    @Override
    public void cancel(Long id) {
        // TODO：取消订单
        // status更改
        //
        // 1.pay_status更改
    }


}
