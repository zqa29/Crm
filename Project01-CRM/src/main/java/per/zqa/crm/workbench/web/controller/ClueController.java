package per.zqa.crm.workbench.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import per.zqa.crm.settings.dao.UserDao;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.settings.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/workbench/clue")
public class ClueController {

    @Autowired
    private UserService userService;

    @RequestMapping("/getUserList.do")
    @ResponseBody
    public List<User> getUserList(){
        List<User> userList = userService.getUserList();
        return userList;
    }
}
