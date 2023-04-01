package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    AddressBookService addressBookService;
    @Autowired
    UserService userService;
    @Autowired
    OrderDetailService orderDetailService;




    @Override
    public void submitOrdersService(Orders orders) {
        //获取用户ID
        Long useId = BaseContext.getCurrentId();

        //获取此用户购物车信息
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,useId);

        List<ShoppingCart> listShoppingCart = shoppingCartService.list(lambdaQueryWrapper);

        //查询用户数据
        User user = userService.getById(useId);

        if (listShoppingCart==null||listShoppingCart.size()==0){
            throw new CustomException("购物车为空，不能下单");
        }
        //查询下单地址
        AddressBook address = addressBookService.getById(orders.getAddressBookId());

        if(address == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }
         //创建订单表
        long orderId = IdWorker.getId();//订单号



        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = listShoppingCart.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(useId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(address.getConsignee());
        orders.setPhone(address.getPhone());
        orders.setAddress((address.getProvinceName() == null ? "" : address.getProvinceName())
                + (address.getCityName() == null ? "" : address.getCityName())
                + (address.getDistrictName() == null ? "" : address.getDistrictName())
                + (address.getDetail() == null ? "" : address.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);
        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(lambdaQueryWrapper);





    }

    @Override
    public Page<OrdersDto> pageOrdersService(int page, int pageSize) {
         //订单分页
        Page<Orders> ordersPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
         this.page(ordersPage,lambdaQueryWrapper);
          //拷贝
         Page<OrdersDto>   page1=new Page<>();
        BeanUtils.copyProperties(ordersPage,page1,"records");
        //获取订单集合
        List<Orders> records = ordersPage.getRecords();

      List<OrdersDto>  list=    records.stream().map((item->{
            OrdersDto dto =new OrdersDto();
            //拷贝
            BeanUtils.copyProperties(item,dto) ;
            //获取订单详情
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(OrderDetail::getOrderId,item.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper1);
            dto.setOrderDetails(orderDetailList);

            return dto;
      })).collect(Collectors.toList());
        //设置records属性
        page1.setRecords(list);


        return page1;
    }

    @Override
    public Page<OrdersDto> mangePage(int page, int pageSize, Long number, String beginTime, String endTime) {
        //分页
      Page<Orders> ordersDtoPage =new Page<>(page,pageSize);
      LambdaQueryWrapper<Orders>
              lambdaQueryWrapper=new LambdaQueryWrapper<>();
      //订单号
      lambdaQueryWrapper.eq(number!=null,Orders::getNumber,number);
      //根据时间查询
      lambdaQueryWrapper.ge( StringUtils.isNotBlank(beginTime),Orders::getOrderTime,beginTime);
      lambdaQueryWrapper.le(StringUtils.isNotBlank(endTime), Orders::getOrderTime,endTime);
      this.page(ordersDtoPage, lambdaQueryWrapper);
      //拷贝
      Page<OrdersDto>  dtoPage=new Page<>();
      BeanUtils.copyProperties(ordersDtoPage,dtoPage,"records");

        List<Orders> records = ordersDtoPage.getRecords();

          List<OrdersDto> list=  records.stream().map((item->{
            //通过下单用户id,获取用户
              User user = userService.getById(item.getUserId());
              OrdersDto ordersDto=new OrdersDto();
              BeanUtils.copyProperties(item,ordersDto);
              ordersDto.setUserName(user.getName());
              //查询订单详情
              LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
              lambdaQueryWrapper1.eq(OrderDetail::getOrderId,item.getId());
              List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper1);
              ordersDto.setOrderDetails(orderDetailList);

              return ordersDto;
          })).collect(Collectors.toList());

           dtoPage.setRecords(list);



        return dtoPage;
    }
}
