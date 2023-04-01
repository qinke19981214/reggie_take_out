package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    DishFlavorService dishFlavorService;


    /*
    *
    * 添加菜品
    * */
    @Transactional  //事务
    @Override
    public void saveWithFlavor(DishDto dto) {

     //保存到菜品表
     this.save(dto);
     //获取菜品id
     Long id = dto.getId();
     //把菜品id附到菜品口味表
     List<DishFlavor> flavors = dto.getFlavors();
        flavors=  flavors.stream().map((item->{
        item.setDishId(id);

        return  item;
     })).collect(Collectors.toList());

     //添加到菜品口味表中
        dishFlavorService.saveBatch(flavors);



    }

    /**
     * 通过id回显
     * @param id
     * @return
     */


    @Override
    public DishDto getByIDWithFlavor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);

        DishDto dishDto=new DishDto();

        //拷贝
        BeanUtils.copyProperties(dish,dishDto);

        //查询口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
         //设置口味
        dishDto.setFlavors(list);

        return dishDto;
    }

    @Transactional
    @Override
    public void updateWithFlavor(DishDto dto) {
        //修改菜品表
        this.updateById(dto);
        //删除原有口味表

        LambdaQueryWrapper <DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dto.getId());

        dishFlavorService.remove(lambdaQueryWrapper);

        //添加口味
        List<DishFlavor> flavors = dto.getFlavors();
       //注入菜品id
        flavors=   flavors.stream().map((item->{
            item.setDishId(dto.getId());

            return item;


        })).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);



    }

    @Override
    public void stopSelling(int status,String id) {

        List<Dish> list =new ArrayList<>();
        int index = id.indexOf(",");
          //表明只有id
        if (index==-1){
            Long ids =Long.parseLong(id);
            Dish dish=new Dish();
            dish.setId(ids);
            dish.setStatus(status);
            list.add(dish);
          this.updateById(dish);
        }else {
            String[] split = id.split(",");
            for (String s : split) {
                Long ids =Long.parseLong(s);
                Dish dish=new Dish();
                dish.setId(ids);
                dish.setStatus(status);
                list.add(dish);

            }
        }


       this.updateBatchById(list);




    }
     @Transactional
    @Override
    public void deletedDish(String id) {

        List<Long> list =new ArrayList<>();
        int index = id.indexOf(",");
        if (index==-1){
            Long ids =Long.parseLong(id);
            list.add(ids);
        }else {

            String[] split = id.split(",");
            for (String s : split) {
                Long ids =Long.parseLong(s);
                list.add(ids); }
        }

      this.removeByIds(list);



    }
}
