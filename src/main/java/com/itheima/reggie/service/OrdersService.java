package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {



    public void submitOrdersService(Orders orders);


    public Page<OrdersDto> pageOrdersService(int page, int pageSize);


    public Page<OrdersDto>   mangePage(int page, int pageSize, Long number,  String beginTime, String endTime);





}
