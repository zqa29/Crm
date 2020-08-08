package per.zqa.crm.settings.dao;

import org.apache.ibatis.annotations.Param;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.settings.domain.UserLogin;

import java.util.List;
import java.util.Map;

/**
 * UserDao接口
 */

public interface UserDao {

    User login(Map<String,String> map);

    User selectOne(UserLogin ul);

    List<User> getUserList();

    User getUserById(String id);
}
