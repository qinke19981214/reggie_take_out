package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    CategoryService categoryService;




    @Transactional
    @Override
    public void addSetmealService(SetmealDto setmealDto) {
       //添加套餐
        this.save(setmealDto);
       //获取套餐对应的产品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes= setmealDishes.stream().map((item->{

           item.setSetmealId(setmealDto.getId());

           return item;

        })).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);






    }
     @Transactional
    @Override
    public void deletedSetmealService(List<Long> ids) {
        //先判断是否停售了
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        //关联条数
        int count = this.count(lambdaQueryWrapper);
        if (count>0){

            throw new CustomException("菜品还在销售中,目前无法删除");
        }

        //可以删除,套餐
        this.removeByIds(ids);
        //删除套餐关联的表
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper1);




    }

    @Override
    public void stopSpellSetmeal(int status, List<Long> ids) {

        LambdaQueryWrapper<Setmeal>  lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        //获取套餐集合
        List<Setmeal> list = this.list(lambdaQueryWrapper);
        //套餐集合中套餐状态重新赋值
        list =  list.stream().map((item->{
             item.setStatus(status);
             return item;

         })).collect(Collectors.toList());

         //改变数据套餐表状态
         this.updateBatchById(list);


    }

    @Override
    public SetmealDto echoSetmeal(Long id) {

        //通过id查找套餐
        Setmeal setmeal = this.getById(id);
        //通过套餐id查找关联表
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        //获取套餐名
        Category category = categoryService.getById(setmeal.getCategoryId());
        //拷贝

         SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(list);
        setmealDto.setCategoryName(category.getName());
        return setmealDto;
    }

    @Transactional
    @Override
    public void updateSetmeal(SetmealDto setmealDto) {
     //修改套餐的信息
    this.updateById(setmealDto) ;

     //修改套餐关联的表

     //1先删除
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);

    //2重新添加 ,并为SetmealDish的套餐ID赋值
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes =  setmealDishes.stream().map((item->{
           item.setSetmealId(setmealDto.getId());

           return item;
        })).collect(Collectors.toList());


        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public SetmealDto list(Long categoryId, int status) {
         //查找套餐
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setmeal::getCategoryId,categoryId);
        lambdaQueryWrapper.eq(Setmeal::getStatus,status);
        Setmeal setmeal = this.getOne(lambdaQueryWrapper);

        SetmealDto  setmealDto =new SetmealDto();

         //拷贝
        BeanUtils.copyProperties(setmeal,setmealDto);
        //查询套餐关联表
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper1);

         //注入
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }


}
