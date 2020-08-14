package per.zqa.crm.workbench.service;

import per.zqa.crm.exception.AjaxRequestException;

import java.util.List;

public interface CustomerService {

    List<String> getCustomerName(String name) throws AjaxRequestException;
}
