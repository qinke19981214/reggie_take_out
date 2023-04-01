package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {


    //添加套餐

    public void  addSetmealService(SetmealDto setmealDto);


    //删除套餐
    public void  deletedSetmealService(List<Long> ids);

    //停售套餐
    public void  stopSpellSetmeal(int status,List<Long> ids);

    //回显套餐
    public SetmealDto echoSetmeal(Long id);

    //修改套餐

    public void updateSetmeal(SetmealDto setmealDto);


    //展示套餐在客户页面

    public SetmealDto  list(Long categoryId,int status);





}
