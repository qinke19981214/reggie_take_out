package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    EmployeeService  employeeService;




    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request , @RequestBody Employee employee){
        //把网页提交的密码进行md5处理
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        //用户名比较
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(employee.getUsername()),Employee::getUsername,employee.getUsername());

        Employee emp = employeeService.getOne(lambdaQueryWrapper);


         if (emp==null){
             return  R.error("登入失败");
         }

         //比较密码

        if (!emp.getPassword().equals(password)){
            return  R.error("登入失败");

        }

        //检查状态
        if (emp.getStatus()==0){

            return  R.error("该用户已经禁用");
        }


        //把用户id保存在session中
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }



   //退出登入
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        request.getSession().removeAttribute("employee");

        return R.success("退出成功");


    }

    /**
     * 新增员工
     * @param employee
     * @return
     */

    @PostMapping
    public R<String>  save(HttpServletRequest request    ,  @RequestBody Employee employee){

        //设置初始密码,进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间和修改时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获取当前登入用户id
       //Long  empId = (Long) request.getSession().getAttribute("employee");

       //设置创建人和修改人

       // employee.setCreateUser(empId);
      // employee.setUpdateUser(empId);


       employeeService.save(employee);


        log.info("新增员工,员工信息: {}",employee);



        return R.success("新增员工成功");
    }


    /**
     *
     * 分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public R<Page>   page(int page, int pageSize, String name){

        //开启分页
        Page<Employee> employeePage =new Page<>(page,pageSize);
        //过滤条件
        LambdaQueryWrapper<Employee>  lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);
        //排序
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
         //执行查询
        employeeService.page(employeePage,lambdaQueryWrapper);

        return R.success(employeePage);





    }


    /**
     *
     * 更新操作
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
       //更新人
        //Long  empId=(Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(empId);
        //更新时间
       // employee.setUpdateTime(LocalDateTime.now());

        //执行更新
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");

    }


    /**
     *
     * 根据id查询
     * @param id
     * @return
     */


    @GetMapping("/{id}")
    public R<Employee> findByEmployeeId(@PathVariable("id") Long id){
        Employee employee = employeeService.getById(id);

        if (employee!=null){

            return R.success(employee);
        }
        return R.error("没有查询到对应员工的信息");




    }






}
