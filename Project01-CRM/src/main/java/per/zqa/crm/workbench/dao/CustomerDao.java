package per.zqa.crm.workbench.dao;

import org.apache.ibatis.annotations.Param;
import per.zqa.crm.workbench.domain.Clue;
import per.zqa.crm.workbench.domain.Customer;

public interface CustomerDao {


    Customer getCustomerByName(String company);

    int saveCustomerByClueAndCustomer(@Param("cus") Customer customer, @Param("clu") Clue clue);
}
