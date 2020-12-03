package com.th.cart.controller;

import com.th.entity.TbItem;
import com.th.entity.TbUser;
import com.th.service.ItemService;
import com.th.sso.service.UserService;
import com.th.utils.CookieUtils;
import com.th.utils.ItripResult;
import com.th.utils.JedisClient;
import com.th.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private JedisClient jedisPool;

    @Value("${CART}")
    private String CART;

    //判断用户是否登录
    public TbUser isLogin(HttpServletRequest request) {
        //判断用户是否登录
        TbUser user=null;
        //用户登录后浏览器的Cookie中会有用户相关的信息
        //只要用户登录了，用户信息就会保存在浏览器的Cookie中(Cookie名为"USERTOKENID")，也会写入Redis中
        String userTokenId = CookieUtils.getCookieValue(request, "USERTOKENID", true);
        if (userTokenId != null && userTokenId.trim().length() > 0) {
            //通过Cookie名称从Redis中得到用户信息
            ItripResult result = userService.getUserByToken(userTokenId);
             user = (TbUser) result.getData();
        }
        return user;

    }


    //得到购物车对象
    public List<TbItem> getCartList(HttpServletRequest request,Long itemId,Integer num){
        //创建一个购物车对象
        List<TbItem> list = null;
        //Cookie中购物车对象
        String cartCookie = null;
        //1.判断用户是否登录
        TbUser user = isLogin(request);
        //用户登录的情况下
        if(user!=null){
            //第一次登录的情况
            String redisCart = jedisPool.get(CART+":"+user.getCode());
            if(redisCart==null){
                list = new ArrayList<>();
                TbItem tbItem = null;
                tbItem = itemService.findById(itemId);
                String[] images = tbItem.getImage().split(",");
                tbItem.setImage(images[0]);
                tbItem.setNum(num);
                list.add(tbItem);
                String jsonString = JsonUtils.objectToJson(list);
                //2.把购物车对象写入到Redis中,键为用户的激活码(code唯一),值为购物车对象(list集合)
                jedisPool.set(CART+":"+user.getCode(),jsonString);
            }
            //3.返回购物车对象
            list = JsonUtils.jsonToList(redisCart, TbItem.class);
            return list;
        }else{ //没有登录的情况
            //1.从cookie中得到一个购物车
             cartCookie = CookieUtils.getCookieValue(request,"CART",true);
            //2.判断cookie中是否有购物车
            if(cartCookie!=null && cartCookie.trim().length()>0) {
                list = JsonUtils.jsonToList(cartCookie, TbItem.class);
                return list;
            }else{
                //没有则创建购物车对象
                list = new ArrayList<>();
                return list;
            }
        }


    }


    //将商品增加到购物车中，需要商品id(itemId)和商品数量(num)
    @RequestMapping("/cart/add/{itemId}")
    public String addCart(@PathVariable("itemId") Long itemId,
                          @RequestParam(value="num",defaultValue = "1") Integer num,
                          HttpServletRequest request, HttpServletResponse response){
        //得到购物车对象判断以前是否购买过这个商品
        List<TbItem> list = getCartList(request,itemId,num);
        //循环list，list中是否存在这个商品
        TbItem tbItem = null;
        boolean isExists = false;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getId().equals(itemId)){
                //如果存在该商品，增加对应的数量
                tbItem = list.get(i);
                tbItem.setNum(tbItem.getNum()+num);
                isExists=true;
                break;
            }
        }
        //不存在该商品，把该商品添加到购物车
        if(isExists==false){
           tbItem = itemService.findById(itemId);
            String[] images = tbItem.getImage().split(",");
            tbItem.setImage(images[0]);
            tbItem.setNum(num);
            list.add(tbItem);
        }
        //把购物车对象转换为json格式字符串
        String jsonString = JsonUtils.objectToJson(list);
        //把购物车对象写入Cookie中，并设置生命周期为1天
        CookieUtils.setCookie(request,response,"CART",jsonString,86400,true);
        //判断用户是否登录
        TbUser user = isLogin(request);
        if(user!=null){
            //说明用户登录了，把购物车对象写到Redis中,键为用户的激活码(code唯一),值为购物车对象(list集合)
            jedisPool.set(CART+":"+user.getCode(),jsonString);
        }

        return "cartSuccess";

    }

    //得到购物车并跳转到购物车页面
    @RequestMapping("/cart/cart")
    public String cart(Model model,HttpServletRequest request,
                       @RequestParam(value = "itemId",required = false) Long itemId,
                       @RequestParam(value="num",required = false,defaultValue = "1") Integer num){
        //从cookie中得到购物车
        List<TbItem> list = getCartList(request,itemId,num);
        model.addAttribute("cartList",list);

        return "cart";
    }

    //更新购物车中的数据
    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public ItripResult updateCartItemNum(@PathVariable("itemId") Long itemId,
                                         @PathVariable("num") Integer num,
                                         HttpServletRequest request, HttpServletResponse response){
        //1.得到购物车
        List<TbItem> list = getCartList(request,itemId,num);
        //2.在cart中找到这个商品，修改购买数量
        TbItem tbItem = null;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getId().equals(itemId)){
                tbItem = list.get(i);
                tbItem.setNum(num);
                break;
            }
        }
        //3.重新写回到cookie中
        String jsonString = JsonUtils.objectToJson(list);
        CookieUtils.setCookie(request,response,"CART",jsonString,86400,true);
        TbUser user = isLogin(request);
        //4.如果用户登录了，更新该用户在Redis中的购物车对象
        if(user!=null){
            jedisPool.set(CART+":"+user.getCode(),jsonString);
        }

        return ItripResult.ok();
    }

    //删除购物车中的一件商品
    @RequestMapping("/cart/delete/{itemId}")
    public String deleteCartItem(@PathVariable("itemId") Long itemId,
                                 @RequestParam(value = "num",required = false) Integer num,
                                 HttpServletRequest request,HttpServletResponse response){
        //1.得到cart
        List<TbItem> list = getCartList(request,itemId,num);

        for(int i=0;i<list.size();i++){
            TbItem tbItem = list.get(i);
            if(tbItem.getId().equals(itemId)){
                list.remove(tbItem);
            }
        }
        String jsonString = JsonUtils.objectToJson(list);
        CookieUtils.setCookie(request,response,"CART",jsonString,86400,true);

        TbUser user = isLogin(request);
        //如果用户登录了，更新该用户在Redis中的购物车对象
        if(user!=null){
            jedisPool.set(CART+":"+user.getCode(),jsonString);
        }
        return "redirect:/cart/cart.html";
    }

    //删除购物车中的多件商品
    @RequestMapping("/cart/deletes/{ids}")
    @ResponseBody
    public String deletesCartItem(@PathVariable("ids") Long[] ids,
                                 HttpServletRequest request,HttpServletResponse response,
                                  @RequestParam(value = "itemId",required = false) Long itemId,
                                  @RequestParam(value="num",required = false,defaultValue = "1") Integer num){
        //1.得到cart
        List<TbItem> list = getCartList(request,itemId,num);
        //2.遍历集合，将数组中的商品编号与集合中对象的商品编号进行比较，一样则删除
        for(int x=0;x<list.size();x++){
            TbItem tbItem =list.get(x);
            for(int y=0;y<ids.length;y++){
                if(ids[y].equals(tbItem.getId())){
                    list.remove(tbItem);
                    x--; //删除后重新从第一个商品开始比较
                }
            }

        }
        System.out.println(list);
        String jsonString = JsonUtils.objectToJson(list);
        //更新Cookie中的购物车对象
        CookieUtils.setCookie(request,response,"CART",jsonString,86400,true);
        //判断用户是否登录
        TbUser user = isLogin(request);
        //如果用户登录了，更新该用户在Redis中的购物车对象
        if(user!=null){
            jedisPool.set(CART+":"+user.getCode(),jsonString);
        }

        return "success";
    }








}
