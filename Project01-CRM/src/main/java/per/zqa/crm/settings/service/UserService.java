package per.zqa.crm.settings.service;

import per.zqa.crm.exception.LoginException;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.settings.domain.UserLogin;

import java.util.List;

/**
 * User业务层
 */

public interface UserService {

    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();


    User getUserById(String id);
}
