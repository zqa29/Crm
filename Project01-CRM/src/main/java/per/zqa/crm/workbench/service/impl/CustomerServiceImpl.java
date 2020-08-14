package per.zqa.crm.workbench.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.workbench.dao.CustomerDao;
import per.zqa.crm.workbench.service.CustomerService;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Override
    public List<String> getCustomerName(String name) throws AjaxRequestException {
        List<String> nameList = customerDao.getCustomerName(name);
        if (nameList == null) {
            // 查询失败
            throw new AjaxRequestException("查询失败");
        }
        return nameList;
    }
}
