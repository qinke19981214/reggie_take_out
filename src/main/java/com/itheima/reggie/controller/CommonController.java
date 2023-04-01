package com.itheima.reggie.controller;


import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {


    //存放文件的目录

    @Value("${reggie.path}")
    private String    direct;


    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info("上传图片信息 {}",file);
        //获取图片原始名信息
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.indexOf("."));   //类似.jpg

        //使用UUID随机生成文件名,防止文件名重复

        String newFileNme = UUID.randomUUID().toString() + substring;

        File  file1=new File(direct);

        //判断目录是否存在
        if (!file1.exists()){
            file1.mkdirs();
        }


         try{
              //将临时文件转存在指定位置
             file.transferTo(new File(direct+newFileNme));

         }catch (IOException e){
             e.printStackTrace();
         }



       return R.success(newFileNme);

    }


     @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
         FileInputStream  fileInputStream=null;
         ServletOutputStream outputStream=null;

        //获取输入流
         try {
               fileInputStream=new FileInputStream(direct+name);


          //获取输出流,把图片写回到浏览器,在浏览器展示
              outputStream = response.getOutputStream();

             byte[] bytes=new byte[1024];
             int len;
             while ((len=fileInputStream.read(bytes))!=-1){

                 outputStream.write(bytes,0,len);
                 outputStream.flush();
             }

         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }finally {
             try {
                 fileInputStream.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
             try {
                 outputStream.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }


     }





}
