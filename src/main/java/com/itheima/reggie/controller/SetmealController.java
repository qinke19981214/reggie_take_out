package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @Autowired
    CategoryService  categoryService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String>  addSetmeal(@RequestBody SetmealDto setmealDto){

        setmealService.addSetmealService(setmealDto);
        return R.success("添加套餐成功");


    }


    /**
     * 套餐分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        Page<Setmeal> setmealPage =new Page<>(page,pageSize);
        Page<SetmealDto>  dtoPage =new Page<>();


        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //套餐查询
        setmealService.page(setmealPage,lambdaQueryWrapper);

        //拷贝
        BeanUtils.copyProperties(setmealPage,dtoPage,"records");

        //获取records
        List<Setmeal> records = setmealPage.getRecords();

      List<SetmealDto>  list=   records.stream().map((item->{
            SetmealDto  setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //获取套餐id
            Long categoryId = item.getCategoryId();
            //获取套餐
            Category byId = categoryService.getById(categoryId);
            //设置套餐名
            setmealDto.setCategoryName(byId.getName());

            return setmealDto;


        })).collect(Collectors.toList());


        dtoPage.setRecords(list);

        return R.success(dtoPage);


    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        setmealService.deletedSetmealService(ids);

        return R.success("删除成功");
    }



   @PostMapping("/status/{status}")
   public R<String> status(@PathVariable int status,@RequestParam List<Long> ids){

      setmealService.stopSpellSetmeal(status,ids);

      return R.success("套餐停售成功");


    }

    /**
     *
     * 套餐回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> update(@PathVariable Long  id){
        log.info("菜单的ID{}",id);
        SetmealDto setmealDto = setmealService.echoSetmeal(id);
        return R.success(setmealDto);
    }







    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("修改套餐的信息{}",setmealDto);

       setmealService.updateSetmeal(setmealDto);

      return R.success("修改套餐信息成功");
    }



    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }







}
