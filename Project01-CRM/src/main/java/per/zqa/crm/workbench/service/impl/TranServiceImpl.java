package per.zqa.crm.workbench.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.exception.TraditionRequestException;
import per.zqa.crm.settings.dao.UserDao;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.utils.DateTimeUtil;
import per.zqa.crm.utils.UUIDUtil;
import per.zqa.crm.vo.PageVo;
import per.zqa.crm.workbench.dao.*;
import per.zqa.crm.workbench.domain.*;
import per.zqa.crm.workbench.service.TranService;
import per.zqa.crm.workbench.vo.TranVo;

import java.util.List;
import java.util.Map;

@Service
public class TranServiceImpl implements TranService {

    @Autowired
    private TranDao tranDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ContactsDao contactsDao;

    @Override
    public void saveTran(String customerName, Tran tran) throws TraditionRequestException {
        // 补全tran对象
        tran.setId(UUIDUtil.getUUID());
        tran.setCreateTime(DateTimeUtil.getSysTime());

        // 调用customerDao对客户名称进行精确查询
        Customer customer = customerDao.getCustomerByName(customerName);
        if (customer == null) {
            // 没有，新建客户
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(tran.getCreateTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setOwner(tran.getOwner());
            // 添加客户
            int i01 = customerDao.saveCustomer(customer);
            if (i01 != 1) {
                throw new TraditionRequestException("添加客户失败！");
            }
        }

        // 获取客户id
        String customerId = customer.getId();
        tran.setContactsId(customerId);
        // 调用tranDao
        int i02 = tranDao.saveTran(tran);
        if (i02 != 1) {
            throw new TraditionRequestException("添加交易失败");
        }

        // 添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setStage(tran.getStage());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setCreateTime(tran.getCreateTime());
        // 调用dao层
        int i03 = tranHistoryDao.saveHistory(tranHistory);
        if (i03 != 1) {
            throw new TraditionRequestException("添加交易历史失败");
        }

    }

    @Override
    public Tran tranDetail(String id) throws TraditionRequestException {
        Tran tran = tranDao.tranDetail(id);
        if (tran == null) {
            throw new TraditionRequestException("查询列表失败");
        }

        // 获取所有者名称
        User user = userDao.getUserById(tran.getOwner());
        if (user != null) {
            tran.setOwner(user.getName());
        }

        // 获取市场活动源
        Activity activity = activityDao.getActivityById(tran.getActivityId());
        if (activity != null) {
            tran.setActivityId(activity.getName());
        }

        // 获取联系人名称
        Contacts contacts = contactsDao.getContactById(tran.getContactsId());
        if (contacts != null) {
            tran.setContactsId(contacts.getFullname());
        }

        // 获取客户名称
        Customer customer = customerDao.getCustomerById(tran.getCustomerId());
        if (customer != null) {
            tran.setCustomerId(customer.getName());
        }

        return tran;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {
        List<TranHistory> thList = tranHistoryDao.getHistoryListByTranId(tranId);
        return thList;
    }

    @Override
    public TranVo updateStage(String id, String stage, String money, String expectedDate, String editBy) throws AjaxRequestException {
        // 1.增加调用tranDao更新阶段
        String editTime = DateTimeUtil.getSysTime();
        int i1 = tranDao.updateStage(id, stage, money, expectedDate, editBy, editTime);
        if (i1 != 1) {
            throw new AjaxRequestException("更新阶段失败！");
        }

        // 2.生成修改（交易）记录
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setStage(stage);
        tranHistory.setCreateBy(editBy);
        tranHistory.setCreateTime(editTime);
        tranHistory.setTranId(id);
        tranHistory.setMoney(money);
        tranHistory.setExpectedDate(expectedDate);
        int i2 = tranHistoryDao.saveHistory(tranHistory);
        if (i2 != 1) {
            throw new AjaxRequestException("生成交易历史失败");
        }

        // 3.将参数封装成TranVo对象
        TranVo tranVo = new TranVo();
        tranVo.setEditBy(editBy);
        tranVo.setEditTime(editTime);
        tranVo.setStage(stage);

        return tranVo;
    }

    @Override
    public PageVo<Map<String, Object>> getCharts() throws AjaxRequestException {
        PageVo<Map<String, Object>> pageVo = new PageVo<>();

        // 调用dao方法获取交易记录的总条数
        int total = tranDao.getTotal();

        // 取得dataList
        List<Map<String, Object>> dataList = tranDao.getCharts();

        if (total == 0 || dataList == null) {
            throw new AjaxRequestException("查询数据失败");
        }

        // 查询成功，属性赋值
        pageVo.setTotal(total);
        pageVo.setDataList(dataList);

        return pageVo;
    }
}
