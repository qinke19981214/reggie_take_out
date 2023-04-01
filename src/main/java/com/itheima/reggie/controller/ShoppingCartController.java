package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {


    @Autowired
    ShoppingCartService shoppingCartService;



    /**
     * 添加商品到购物车
     * @param shoppingCart
     * @return
     */

    @PostMapping("/add")
    public R<ShoppingCart>  addShoppingCart(@RequestBody ShoppingCart shoppingCart ){

        ShoppingCart shoppingCart1 = shoppingCartService.addShoppingCartService(shoppingCart);


        return  R.success(shoppingCart1);


    }


    /**
     * 查询购物车
      * @return
     */
    @GetMapping("list")
    public R<List<ShoppingCart>> list(){

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);

        return R.success(list);


    }

    /**
     * 减少商品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart  shoppingCart){

        ShoppingCart shoppingCart1 = shoppingCartService.subShoppingCartService(shoppingCart);
        return R.success(shoppingCart1);
     }


    /**
     * 清空购物车
     */
      @DeleteMapping("/clean")
      public R<String>  delete(){

      Long currentId = BaseContext.getCurrentId();
       LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
       lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
       shoppingCartService.remove(lambdaQueryWrapper);

       return R.success("清空购物成功");


    }





}
