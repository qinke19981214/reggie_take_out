package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

     @Autowired
    CategoryService categoryService;


    /**
     * 添加菜系和套餐类型
     * @param category
     * @return
     */
   @PostMapping
    public R<String>  save(@RequestBody Category category){

     log.info("category {}",category);
     categoryService.save(category);

     return R.success("添加成功");

   }

    /**
     *
     * 分页
     * @param page
     * @param pageSize
     * @return
     */

  @GetMapping("/page")
  public R<Page> page(int page,int pageSize){

   Page<Category> categoryPage=new Page<>(page,pageSize);
   LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
   lambdaQueryWrapper.orderByAsc(Category::getSort);
   categoryService.page(categoryPage,lambdaQueryWrapper);

   return R.success(categoryPage);



  }

    /**
     * 通过id进行逻辑删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String>  deleted(Long ids){
       log.info("删除的id {}:",ids);
       categoryService.remove(ids);
       return R.success("分类信息删除成功");

    }


    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String>  update(@RequestBody Category category){
        log.info("修改分类信息, {}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");

    }



   @GetMapping("/list")
   public R<List<Category>> list( Category category){

     //设置查询条件
     LambdaQueryWrapper<Category>  lambdaQueryWrapper=new LambdaQueryWrapper<>();
     lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
     //根据顺序排序和修改时间
       lambdaQueryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
       //执行查询
       List<Category> categoryList = categoryService.list(lambdaQueryWrapper);


       return R.success(categoryList);


   }








}
