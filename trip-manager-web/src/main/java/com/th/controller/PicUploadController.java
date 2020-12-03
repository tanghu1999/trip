package com.th.controller;

import com.th.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("file")
public class PicUploadController {

    /*
    * @Value("#{}") 表示SpEl表达式通常用来获取bean的属性，或者调用bean的某个方法。当然还有可以表示常量
    *  用 @Value("${xxxx}")注解从配置文件读取值的用法
    * */
    //读取配置文件,从配置res.properties文件中读取imageServer 的值
    @Value("${imageServer}")
    private String imageServer;

    //单个文件上传
    @RequestMapping("uploadOne")
    @ResponseBody
    public Map uploadOne(@RequestParam("file") MultipartFile file) throws Exception{
        //创建一个FastDFS的客户端
        FastDFSClient fastDFSClient = new FastDFSClient("classpath:resource/fdfs_client.conf");
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".")+1); //截取jpg png
        //执行上传处理，返回图片的路径
        String path = imageServer+fastDFSClient.uploadFile(file.getBytes(), ext);
        Map map = new HashMap();
        map.put("path",path);

        return map;
    }

    //多文件上传
    @RequestMapping("uploadMultiple")
    @ResponseBody
    public Map uploadMultiple(HttpServletRequest request) throws Exception{
        //创建一个通用的多部分解析器
        Map map = new HashMap();
        //创建一个FastDFS的客户端
        FastDFSClient fastDFSClient = new FastDFSClient("classpath:resource/fdfs_client.conf");
        //存储上传成功后文件的路径
        List<String> list = new ArrayList<>();
        //创建文件视图解析器对象
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //如果请求是多个文件
        if(commonsMultipartResolver.isMultipart(request)){
            //转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            //得到文件的名字
            Iterator<String> iter = multiRequest.getFileNames();
            while(iter.hasNext()){
                //通过文件名得到上传文件
                List<MultipartFile> multipartFiles = multiRequest.getFiles(iter.next());
                for(MultipartFile multipartFile:multipartFiles){
                    String fileName=multipartFile.getOriginalFilename();
                    String ext = fileName.substring(fileName.lastIndexOf(".")+1);
                    String path = imageServer+fastDFSClient.uploadFile(multipartFile.getBytes(),ext);
                    list.add(path);
                }
            }
        }

        map.put("code",0);
        map.put("data",list);
        return map;

    }










}
