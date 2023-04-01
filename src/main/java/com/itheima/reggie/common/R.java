package com.itheima.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class R<T> {

    private Integer code ;    //状态码 1代表成功 ,0代表失败
    private  String msg;     // 错误信息
    private T   data;        //  数据

    private Map   map=new HashMap();  ////动态数据


   public static <T> R<T>  success(T object){
        R<T>  r=new R<>();
        r.data=object;
        r.code=1;
        r.msg="成功";

        return r;
   }


   public static <T> R<T> error(String msg){
       R<T>  r=new R<>();
       r.code=0;
       r.msg=msg;
       return r;

   }



    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }










}
