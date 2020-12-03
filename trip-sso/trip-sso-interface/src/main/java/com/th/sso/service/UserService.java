package com.th.sso.service;

import com.th.entity.TbUser;
import com.th.utils.ItripResult;

import java.util.Map;

public interface UserService {

    //增加一个用户时检查用户数据的完整性
    //type:值为1,2,3时分别代表userName,phone,email
    ItripResult checkData(String param, Integer type);

    //完成用户注册
    ItripResult register(TbUser user);

    //用户登录
    ItripResult userLogin(String userName, String password);

    //根据token取redis中用户对象
    ItripResult getUserByToken(String token);

    //完成用户激活
    ItripResult jihuo(String code);

    //通过
    TbUser getUserByCode(String code);



}
