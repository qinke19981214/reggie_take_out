package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/*
* 菜品管理
* */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    DishService  dishService;
    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    CategoryService  categoryService;
    @Autowired
    private RedisTemplate redisTemplate;


   @PostMapping
    public R<String>  addDish(@RequestBody DishDto dishDto){

       log.info("详细信息 {}",dishDto);
       dishService.saveWithFlavor(dishDto);
       //清理某个分类下面的菜品缓存数据
       String key="dish_"+dishDto.getCategoryId()+"_1";
       redisTemplate.delete(key);

       return R.success("添加菜品成功");


   }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

   @GetMapping("/page")
   public R<Page> page(int page,int pageSize,String name){

       Page<Dish> dishPage=new Page<>(page,pageSize);
       Page<DishDto> dtoPage=new Page<>();

       //条件
       LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper();
       dishLambdaQueryWrapper.like(StringUtils.isNotBlank(name),Dish::getName,name);
       dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
       //查询菜品
       dishService.page(dishPage,dishLambdaQueryWrapper);

       //拷贝除了records这个字段
       BeanUtils.copyProperties(dishPage,dtoPage,"records");
      //获取菜品集合
       List<Dish> records = dishPage.getRecords();

       List< DishDto>  list=    records.stream().map((item->{
           DishDto dto=new DishDto();
           //拷贝
           BeanUtils.copyProperties(item,dto);
           //获取菜品id
           Long categoryId = item.getCategoryId();
           //通过菜品id查找菜品分类
           Category cate = categoryService.getById(categoryId);
           dto.setCategoryName(cate.getName());


           return dto;
       })).collect(Collectors.toList());


       //records这个字段
       dtoPage.setRecords(list);


       return R.success(dtoPage);
   }


    /**
     * 根据id回显
     * @param id
     * @return
     */
   @GetMapping("{id}")
   public R<DishDto>   update(@PathVariable Long  id){

       DishDto dishDto = dishService.getByIDWithFlavor(id);

        return R.success(dishDto);


   }


    /**
     * 修改
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String>  update(@RequestBody DishDto dishDto){

        log.info("详细信息 {}",dishDto);
        dishService.updateWithFlavor(dishDto);
        //清理某个分类下面的菜品缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);





        return R.success("修改菜品成功");


    }

    /**
     * 停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status,String ids){
        dishService.stopSelling(status,ids);
        return R.success("停售成功");


    }


    /**
     * 通过id删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String>  delete(String  ids){

        dishService.deletedDish(ids);

        return R.success("删除成功");




    }


    /**
     * 通过菜类型id查找下的菜
     * @param dish
     * @return
     */


 /*   @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){

     LambdaQueryWrapper<Dish> lambdaQueryWrapper =new LambdaQueryWrapper<>();
     lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
     lambdaQueryWrapper.eq(Dish::getStatus,1);
     lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        return R.success(list);
    }*/



    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;

        //动态构造key
       String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //从redis获取缓存数据
        dishDtoList= (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList!=null){
            return R.success(dishDtoList);
        }


        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
         //如果不存在,需要查询数据库,将查询到菜品数据缓存到Redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

















}
