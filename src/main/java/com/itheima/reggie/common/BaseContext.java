package com.itheima.reggie.common;


/*
*
*基于ThreadLocal工具类,用于获取和设置用户id
6* */
public class BaseContext {

    private  static ThreadLocal<Long> threadLocal=new ThreadLocal<>();



    //设置登入用户id
    public  static  void setCurrentId(Long id){
        threadLocal.set(id);
    }


    //取出登入用户id
    public static Long   getCurrentId(){

        return threadLocal.get();
    }

}
