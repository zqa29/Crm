package per.zqa.crm.settings.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import per.zqa.crm.exception.LoginException;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.settings.service.UserService;
import per.zqa.crm.utils.HandleFlag;
import per.zqa.crm.utils.MD5Util;
import per.zqa.crm.utils.UUIDUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.http.HttpResponse;
import java.util.Map;

@Controller
@RequestMapping("/settings/user")
public class UserController  {

    @Autowired
    private UserService userService;

    /*
        用户登录
            验证用户名和密码
            验证成功继续往下验证其他的字段信息
            从返回的User对象获取到
                expireTime 失效时间
                lockState  锁定状态
                allowIPs   浏览器端ip地址是否有效
     */
    @RequestMapping("/login.do")
    @ResponseBody
    public Map<String,Object> login(String loginAct, String loginPwd, String loginCheck,
                                    HttpServletRequest request, HttpServletResponse response) throws LoginException {

        // 获取访问的ip地址
        String ip = request.getRemoteAddr();
        // 将得到的用户、密码（转码后）和ip地址调用userDao的login方法
        User user = userService.login(loginAct, MD5Util.getMD5(loginPwd), ip);
        // 没有异常则将对象放入Session域中
        request.getSession().setAttribute("user", user);

        // 十天免登录操作
        // 判断是否已经是否选中
        if ("selected".equals(loginCheck)) {

            // 创建Cookie对象
            Cookie cookie01 = new Cookie("loginAct", loginAct);
            Cookie cookie02 = new Cookie("loginPwd", loginPwd);

            // 设置Cookie的失效时间
            cookie01.setMaxAge(60 * 60 *24 *10);
            cookie02.setMaxAge(60 * 60 *24 *10);

            // 设置Cookie的作用地址
            cookie01.setPath("/");
            cookie02.setPath("/");

            // 把Cookie添加到相应的作用域中
            response.addCookie(cookie01);
            response.addCookie(cookie02);
        }

        return HandleFlag.successTrue();
    }

    // 实现十天内免登录,不需要@ResponseBody因为是页面跳转不是字符串返回
    @RequestMapping("/toLogin.do")
    public String toLogin(HttpServletRequest request){
        // 首先获取Cookie对象数组
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            // 定义变量
            String loginAct = null;
            String loginPwd = null;

            // 遍历数组获取用户帐号和密码
            for (Cookie cookie: cookies) {
                String cookieName = cookie.getName();
                // 获取用户名
                if ("loginAct".equals(cookieName)) {
                    loginAct = cookie.getValue();
                }
                // 获取用户密码
                if ("loginPwd".equals(cookieName)) {
                    loginPwd = cookie.getValue();
                }
            }

            // 如果帐号密码都不为空则说明Cookie里没有对应的用户信息
            if (loginAct != null && loginPwd != null) {
                // 获取访问的ip地址
                String ip = request.getRemoteAddr();
                try {
                    // 将得到的用户、密码（转码后）和ip地址调用userDao的login方法
                    User user = userService.login(loginAct, MD5Util.getMD5(loginPwd), ip);
                    // 没有异常则将对象放入Session域中
                    request.getSession().setAttribute("user", user);

                    // 免登录验证成功后重定向到欢迎页面
                    return "redirect:/workbench/activity/toIndex.do";

                } catch (LoginException e) {
                    e.printStackTrace();
                    // 验证失败则跳转到登录页面，重新进行登录
                    return "login";
                }
            }

        }

        // 帐号密码为空也要返回登录界面
        return "login";
    }

    @RequestMapping("/logout.do")
    public String logout(HttpSession session, HttpServletResponse response) {
        // session域销毁
        session.invalidate();

        // 创建新的Cookie对象来覆盖旧的Cookie
        Cookie cookie01 = new Cookie("loginAct", null);
        Cookie cookie02 = new Cookie("loginPwd", null);
        cookie01.setPath("/");
        cookie02.setPath("/");
        // 修改Cookie的失效时间为0
        cookie01.setMaxAge(0);
        cookie02.setMaxAge(0);
        // 将Cookie添加到作用域
        response.addCookie(cookie01);
        response.addCookie(cookie02);

        return "login";
    }

}
