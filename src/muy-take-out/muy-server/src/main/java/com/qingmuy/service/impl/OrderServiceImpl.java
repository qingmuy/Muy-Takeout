package com.qingmuy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingmuy.constant.MessageConstant;
import com.qingmuy.context.BaseContext;
import com.qingmuy.dto.*;
import com.qingmuy.entity.AddressBook;
import com.qingmuy.entity.OrderDetail;
import com.qingmuy.entity.Orders;
import com.qingmuy.entity.ShoppingCart;
import com.qingmuy.exception.AddressBookBusinessException;
import com.qingmuy.exception.OrderBusinessException;
import com.qingmuy.exception.ShoppingCartBusinessException;
import com.qingmuy.mapper.OrderDetailMapper;
import com.qingmuy.mapper.OrdersMapper;
import com.qingmuy.mapper.ShoppingCartMapper;
import com.qingmuy.mapper.UserMapper;
import com.qingmuy.result.PageResult;
import com.qingmuy.service.AddressBookService;
import com.qingmuy.service.OrderService;
import com.qingmuy.vo.OrderPaymentVO;
import com.qingmuy.vo.OrderStatisticsVO;
import com.qingmuy.vo.OrderSubmitVO;
import com.qingmuy.vo.OrderVO;
import com.qingmuy.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    @Resource
    AddressBookService addressBookService;

    @Resource
    ShoppingCartMapper shoppingCartMapper;

    @Resource
    OrdersMapper ordersMapper;

    @Resource
    OrderDetailMapper orderDetailMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    WebSocketServer webSocketServer;

    /*@Resource
    WeChatPayUtil weChatPayUtil;*/

    /**
     * 提交订单/创建订单
     * @param ordersSubmitDTO 订单信息
     * @return 订单信息
     */
    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {

        // 处理地址簿、购物数据为空的情况
        AddressBook byId = addressBookService.getById(ordersSubmitDTO.getAddressBookId());
        if (byId == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(qw);

        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 向订单表中插入数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(byId.getPhone());
        orders.setConsignee(byId.getConsignee());
        orders.setUserId(BaseContext.getCurrentId());
        orders.setUserName(userMapper.selectById(BaseContext.getCurrentId()).getName());

        // 完善订单地址数据
        AddressBook addressBook = addressBookService.getById(ordersSubmitDTO.getAddressBookId());
        String address = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        orders.setAddress(address);

        ordersMapper.insert(orders);

        // 向订单详细表中插入数据
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();

        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }

        ordersMapper.insertBatch(orderDetailList);

        // 清空当前用户的购物车数据
        shoppingCartMapper.delete(qw);

        // 封装VO返回结果
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO 订单支付信息
     * @return 支付信息
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        // Long userId = BaseContext.getCurrentId();
        // User user = userMapper.selectById(userId);

        //调用微信支付接口，生成预支付交易单
        /*JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo 订单号
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.update(orders);

        //通过webcoket向客户端推送消息
        HashMap<Object, Object> hashMap = new HashMap<>();

        hashMap.put("type", 1); //1表示来单提醒，2表示用户催单
        hashMap.put("orderId", ordersDB.getId());
        hashMap.put("content", "订单号" + outTradeNo);

        String jsonString = JSON.toJSONString(hashMap);
        webSocketServer.sendToAllClient(jsonString);
    }

    /**
     * 订单查询
     * @param ordersPageQueryDTO 查询条件
     * @return 订单分页结果
     */
    @Override
    public PageResult search(OrdersPageQueryDTO ordersPageQueryDTO) {
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();

        // 拼接查询条件
        if (ordersPageQueryDTO.getStatus() != null) {
            qw.eq(Orders::getStatus, ordersPageQueryDTO.getStatus());
        }
        if (ordersPageQueryDTO.getPhone() != null) {
            qw.eq(Orders::getStatus, ordersPageQueryDTO.getStatus());
        }
        if (ordersPageQueryDTO.getNumber() != null) {
            qw.eq(Orders::getNumber, ordersPageQueryDTO.getNumber());
        }
        if (ordersPageQueryDTO.getBeginTime() != null && ordersPageQueryDTO.getEndTime() != null) {
            qw.between(Orders::getOrderTime, ordersPageQueryDTO.getBeginTime(), ordersPageQueryDTO.getEndTime());
        }

        Page<Orders> page = new Page<>(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersPage = ordersMapper.selectPage(page, qw);

        return new PageResult(ordersPage.getTotal(), ordersPage.getRecords());
    }

    /**
     * 各个状态的订单数量统计
     * @return 订单数量
     */
    @Override
    public OrderStatisticsVO countOrdersByStatus() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();

        LambdaQueryWrapper<Orders> qwc = new LambdaQueryWrapper<>();
        // 统计待配送/已接单订单的数量
        qwc.eq(Orders::getStatus, Orders.CONFIRMED);
        Long comfirmed = ordersMapper.selectCount(qwc);

        LambdaQueryWrapper<Orders> qwd = new LambdaQueryWrapper<>();
        // 统计派送中订单的数量
        qwd.eq(Orders::getStatus, Orders.DELIVERY_IN_PROGRESS);
        Long deliveryInProgress = ordersMapper.selectCount(qwd);

        LambdaQueryWrapper<Orders> qwt = new LambdaQueryWrapper<>();
        // 统计待接单订单的数量
        qwt.eq(Orders::getStatus, Orders.TO_BE_CONFIRMED);
        Long toBeConfirmed = ordersMapper.selectCount(qwt);

        orderStatisticsVO.setConfirmed(Math.toIntExact(comfirmed));
        orderStatisticsVO.setDeliveryInProgress(Math.toIntExact(deliveryInProgress));
        orderStatisticsVO.setToBeConfirmed(Math.toIntExact(toBeConfirmed));

        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param id 订单id
     */
    @Override
    public void confirm(Long id) {
        Orders orders = ordersMapper.selectById(id);
        orders.setStatus(Orders.CONFIRMED);

        ordersMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO 拒单的信息
     */
    @Override
    public void reject(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = ordersMapper.selectById(ordersRejectionDTO.getId());

        // 设置订单的状态
        orders.setStatus(Orders.CANCELLED);
        orders.setPayStatus(Orders.REFUND);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());

        this.updateById(orders);
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    @Override
    public OrderVO queryOrderDetail(Long id) {
        // 订单数据
        Orders order = this.getById(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        LambdaQueryWrapper<OrderDetail> qw = new LambdaQueryWrapper<>();
        qw.eq(OrderDetail::getOrderId, id);
        List<OrderDetail> orders = orderDetailMapper.selectList(qw);

        orderVO.setOrderDetailList(orders);

        return orderVO;
    }

    /**
     * 取消订单
     * @param ordersCancelDTO 订单信息
     */
    @Override
    public void cancal(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = ordersMapper.selectById(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        ordersMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id 订单id
     */
    @Override
    public void deliver(Long id) {
        Orders order = this.getById(id);

        order.setStatus(Orders.DELIVERY_IN_PROGRESS);
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(45));

        ordersMapper.update(order);
    }

    @Override
    public void complete(Long id) {
        Orders order = this.getById(id);

        order.setStatus(Orders.COMPLETED);
        order.setDeliveryTime(LocalDateTime.now());

        this.updateById(order);
    }

    @Override
    public PageResult queryHistoryOrders(Integer page, Integer pageSize, Integer status) {
        Page<Orders> page1 = new Page<>(page, pageSize);    // 分页查询 条件
        Page<Orders> orderPage;     // 分页查询结果

        // 判断是否为有条件查询
        if (status != null) {
            LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
            qw.eq(Orders::getStatus, status);

            orderPage = ordersMapper.selectPage(page1, qw);
        } else {
            orderPage = ordersMapper.selectPage(page1, null);
        }

        // 将分页查询结果 Orders类封装为OrderVO类 改为列表
        ArrayList<OrderVO> arrayList = new ArrayList<>();
        for (Orders order : orderPage.getRecords()) {
            LambdaQueryWrapper<OrderDetail> qw = new LambdaQueryWrapper<>();
            qw.eq(OrderDetail::getOrderId, order.getId());

            List<OrderDetail> orderDetails = orderDetailMapper.selectList(qw);
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setOrderDetailList(orderDetails);

            arrayList.add(orderVO);
        }

        // 改为OrderVO分页结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(orderPage.getTotal());
        pageResult.setRecords(arrayList);

        return pageResult;
    }

    /**
     * 再来一单
     * @param id 订单id
     */
    @Transactional
    @Override
    public void again(Long id) {
        // 复刻当前订单数据
        Orders target = new Orders();

        Orders source = this.getById(id);
        BeanUtils.copyProperties(source, target);

        log.info("订单号：{}", System.currentTimeMillis());
        target.setNumber(String.valueOf(System.currentTimeMillis()));
        target.setOrderTime(LocalDateTime.now());
        target.setCancelTime(null);
        target.setPayMethod(0);
        target.setPayStatus(Orders.UN_PAID);
        target.setCancelTime(null);
        target.setCancelReason(null);
        target.setRejectionReason(null);
        target.setEstimatedDeliveryTime(null);
        target.setDeliveryTime(null);

        // 完成订单信息的插入
        ordersMapper.insert(target);

        // 查询原订单的菜品信息
        LambdaQueryWrapper<OrderDetail> qw = new LambdaQueryWrapper<>();
        qw.eq(OrderDetail::getOrderId, id);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(qw);

        for (OrderDetail orderDetail : orderDetails) {
            OrderDetail orderDetailTarget = new OrderDetail();

            // 复制信息
            BeanUtils.copyProperties(orderDetail, orderDetailTarget);

            orderDetailTarget.setId(null);
            orderDetailTarget.setOrderId(target.getId());

            orderDetailMapper.insert(orderDetailTarget);
        }
    }

    @Override
    public void reminder(Long id) {
        // 获取当前订单
        Orders orders = ordersMapper.selectById(id);

        // 校验订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("type", 2); //1表示来单提醒，2表示客户催单
        hashMap.put("orderId", id);
        hashMap.put("content", "订单号" + orders.getNumber());

        webSocketServer.sendToAllClient(JSON.toJSONString(hashMap));
    }
}
