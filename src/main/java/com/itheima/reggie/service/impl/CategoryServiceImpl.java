package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;







    /**
     * 删除业务
     * @param id
     */
    @Override
    public void remove(Long id) {
        //是否关联菜品
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(lambdaQueryWrapper);

        if (count>0){

           //抛出自定义菜品异常

            throw  new CustomException("当前分类关联菜品,不能删除");

        }

       //是否关联菜品
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(lambdaQueryWrapper1);
        if (count1>0){
            //抛出自定义套餐异常
            throw  new CustomException("当前分类关联套餐,不能删除");
        }

        //没有关联菜品或套餐,进行逻辑删除
        super.removeById(id);





    }
}
