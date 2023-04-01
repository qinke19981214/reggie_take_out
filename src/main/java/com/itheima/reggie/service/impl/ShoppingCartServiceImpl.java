package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {



    @Override
    public ShoppingCart addShoppingCartService(ShoppingCart shoppingCart) {
        //设置用户id
       shoppingCart.setUserId(BaseContext.getCurrentId());

       //判断是否菜品还是套餐
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());


        if (dishId!=null){
            //是菜品
           lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else {
         //是套餐
          lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询是否以前有相同商品
        ShoppingCart shoppingCart1 = this.getOne(lambdaQueryWrapper);

        if (shoppingCart1!=null){
           //有相同的商品
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCart1.setCreateTime(LocalDateTime.now());

           this.updateById(shoppingCart1);

           return shoppingCart1;

        }else {
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCart.setNumber(1);
            this.save(shoppingCart);

            return shoppingCart;
        }
    }

    @Override
    public ShoppingCart subShoppingCartService(ShoppingCart shoppingCart) {

       //判断是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper  =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        if (dishId!=null){
           //是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
          //是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }


        ShoppingCart shoppingCart1 = this.getOne(lambdaQueryWrapper);
        //修改数量
        if (shoppingCart1!=null) {
            shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);


            if (shoppingCart1.getNumber() == 0) {
                this.removeById(shoppingCart1);

                return shoppingCart1;
            }

            this.updateById(shoppingCart1);
        }

        return shoppingCart1;


    }
}
