package per.zqa.crm.settings.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.zqa.crm.exception.LoginException;
import per.zqa.crm.settings.dao.UserDao;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.settings.service.UserService;
import per.zqa.crm.utils.DateTimeUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {

//        // 采用对象属性来传入属性
//        UserLogin ul = new UserLogin(loginAct, loginPwd);

        // 采用Map来传入数据
        Map<String,String> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);

        // 调用dao层
        User user = userDao.login(map);

        // 判断是否查询到相应用户
        if (user == null) {
            // 没有则抛出异常
            throw new LoginException("账号密码错误");
        }

        // 获取用户失效时间
        String expireTime = user.getExpireTime();
        if (expireTime != null) {
            // 判断失效时间是否比当前时间晚
            if (expireTime.compareTo(DateTimeUtil.getSysTime()) < 0) {
                // 小于0则已经时间已经到达，抛出失效异常
                throw new LoginException("账号已失效");
            }
        }

        // 获取帐户锁定状态
        String lockState = user.getLockState();
        if (lockState != null) {
            // 判断账户是否已锁定
            if ("0".equals(lockState)) {
                throw new LoginException("账户已锁定");
            }
        }

        // 获取账户的允许的ip地址
        String allowIps = user.getAllowIps();
        if (allowIps != null) {
            if (!allowIps.contains(ip)) {
                // 不包含ip地址则抛出异常
                throw new LoginException("ip地址受限");
            }
        }

        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> userList = userDao.getUserList();
        return userList;
    }

    @Override
    public User getUserById(String id) {
        User user = userDao.getUserById(id);
        return user;
    }

}
