package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    OrdersService ordersService;




   @PostMapping("/submit")
   public R<String> submit(@RequestBody Orders orders){

     ordersService.submitOrdersService(orders);
     return R.success("支付成功");
   }


   @GetMapping("/userPage")
   public R< Page<OrdersDto>> userPage(int page, int pageSize){
     log.info("页数{},显示条数",page,pageSize);
       Page<OrdersDto> ordersDtoPage = ordersService.pageOrdersService(page, pageSize);
       return R.success(ordersDtoPage);

   }



   @GetMapping("/page")

    public R<Page<OrdersDto>>   page(int page, int pageSize, Long number,  String beginTime, String endTime){
    log.info("页数和显示的条数{}{}",page,pageSize);
    log.info("订单号{}",number);
    log.info("开始时间{}",beginTime);
    log.info("结束时间{}",endTime);

       Page<OrdersDto> ordersDtoPage = ordersService.mangePage(page, pageSize, number, beginTime, endTime);

      return R.success(ordersDtoPage) ;
   }














}
