package com.th.order.interceptor;

import com.th.entity.TbUser;
import com.th.utils.CookieUtils;
import com.th.utils.JedisClient;
import com.th.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Value("${USERTOKEN}")
    private String USERTOKEN;

    @Autowired
    private JedisClient jedisClient;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //得到拦截前请求的路径(http://localhost:8086/order/order-cart.html)
        String url = httpServletRequest.getRequestURL().toString();

        //1.从cookie中取token
        String USERTOKENID = CookieUtils.getCookieValue(httpServletRequest, "USERTOKENID", true);
        if (USERTOKENID == null || USERTOKENID.trim().length() == 0) {
            httpServletResponse.sendRedirect("http://localhost:8084/user/showLogin?url=" + url);
            return false;
        }
        //2.从redis中取用户
        String jsonUser = jedisClient.get(USERTOKEN + USERTOKENID);
        if (jsonUser == null || jsonUser.trim().length() == 0) {
            httpServletResponse.sendRedirect("http://localhost:8084/user/showLogin?url=" + url);
            return false;
        }
        //3.在cookie和redis中都得到了用户信息
        TbUser tbUser = JsonUtils.jsonToPojo(jsonUser, TbUser.class);
        httpServletRequest.setAttribute("user", tbUser);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
