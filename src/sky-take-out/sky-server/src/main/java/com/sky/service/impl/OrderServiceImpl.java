package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    UserMapper userMapper;

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
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
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
     * @param outTradeNo
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
}
