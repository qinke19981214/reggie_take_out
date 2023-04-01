package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {


    public void saveWithFlavor(DishDto dto);

    public DishDto getByIDWithFlavor(Long id);

    //根据DishDto修改菜品表和口味表
    public void   updateWithFlavor(DishDto dto);


    //根据id停售菜品

    public void stopSelling(int status,String id);

    //根据id进行批量删除

    public void   deletedDish(String id);



}
