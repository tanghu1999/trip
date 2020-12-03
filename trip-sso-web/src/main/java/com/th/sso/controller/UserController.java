package com.th.sso.controller;

import com.aliyuncs.exceptions.ClientException;
import com.th.entity.TbUser;
import com.th.sso.service.UserService;
import com.th.utils.CookieUtils;
import com.th.utils.ItripResult;
import com.th.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    //跳转到注册页面
    @RequestMapping("/user/showRegister")
    public String showRegister(){
        return "register";
    }

    //跳转到登录页面
    @RequestMapping("/user/showLogin")
    public String showLogin(@RequestParam(name = "url",defaultValue = "") String url,
                            Model model) {
        System.out.println(url);
        model.addAttribute("redirect",url);

        return "login";
    }


    //判断用户是否已经存在
    @RequestMapping(value="/user/check/{param}/{type}",method = RequestMethod.GET)
    @ResponseBody
    public ItripResult checkData(@PathVariable("param") String param,
                                 @PathVariable("type") Integer type){
        return userService.checkData(param,type);

    }

    //发送短信
    @RequestMapping("/user/sendMessage")
    @ResponseBody
    public Map sendMessage(){
        try {
            //3个参数,第一个是模板CODE, 第二个是你的电话号码, 第三个必须是验证码,必须是数字的字符串
            int random = (int)(Math.random()*1000000);
            String code = Integer.valueOf(random).toString();
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, "18827142552", code);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        Map map = new HashMap();
        map.put("msg","验证码发送成功");

        return map;
    }

    //用户注册
    @RequestMapping(value="/user/register",method=RequestMethod.POST)
    @ResponseBody
    public ItripResult register(TbUser tbUser){
        return userService.register(tbUser);
    }

    //激活码
    @RequestMapping("/user/jihuo/{code}")
    public String jihuo(@PathVariable("code") String code){
        userService.jihuo(code);

        return "redirect:/user/showLogin";
    }

    //用户登录
    @RequestMapping("/user/login")
    @ResponseBody
    public ItripResult login(String username, String password,
                             HttpServletRequest request, HttpServletResponse response){
        ItripResult result = userService.userLogin(username, password);
        if(result.getStatus()==200){ //登录成功
            //将用户的token写到cookie中
            CookieUtils.setCookie(request,response,"USERTOKENID",result.getData().toString(),1800,true);
        }

        return result;
    }

    //用户退出
    @RequestMapping("/user/logout")
    public void logout(HttpServletRequest request,HttpServletResponse response) throws Exception {
        //得到所有的Cookie对象
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName();
            if("USERTOKENID".equals(cookieName)){
                //让name为USERTOKENID的cookie立即失效,必须设置路径
                cookie.setMaxAge(0);
                cookie.setPath("/");
                //将修改后的cookie重新加进去
                response.addCookie(cookie);
                break;
            }

        }
        response.sendRedirect("http://localhost:8081");

    }

    //根据cookie中的token查询出用户信息
    @RequestMapping("/user/token/{token}")
    @ResponseBody
    public Object getUserByToken(@PathVariable("token") String token,String callback){

        ItripResult result = userService.getUserByToken(token);
        //处理json跨域请求
        if(callback!=null && callback.trim().length()>0){
            //请求参数中有callback，表示一个jsonp的请求(跨域请求)
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            //设置回调方法
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        //非json跨域请求
        return result;
    }








}
