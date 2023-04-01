package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {


    //添加商品到购物车
    public ShoppingCart  addShoppingCartService(ShoppingCart shoppingCart);

    //减少商品

    public ShoppingCart subShoppingCartService(ShoppingCart shoppingCart);




}
