package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {


    @Autowired
    AddressBookService addressBookService;


    /**
     * 添加收货地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String>  save(@RequestBody AddressBook  addressBook){
        //设置当前登入的ID
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("添加收货地址成功");

    }


    /**
     * 显示所有的地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>>   list(){
        Long useId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,useId);
        List<AddressBook> listAddressBook = addressBookService.list(lambdaQueryWrapper);
        return R.success(listAddressBook);
    }


    @PutMapping("/default")
    public R<AddressBook> defaultAddressBook(@RequestBody AddressBook  addressBook){
        log.info("默认地址位{}",addressBook.getId());
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault,0);
        //先把所有地址修改成非默认地址
        addressBookService.update(wrapper);
        //在把要求的地址修改成默认值


        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }


    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }






}
