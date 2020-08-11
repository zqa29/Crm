package per.zqa.crm.workbench.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.zqa.crm.utils.DateTimeUtil;
import per.zqa.crm.utils.UUIDUtil;
import per.zqa.crm.workbench.dao.*;
import per.zqa.crm.workbench.domain.*;
import per.zqa.crm.workbench.service.ClueService;

import java.util.List;

@Service
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueDao clueDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private ContactsDao contactsDao;
    @Autowired
    private ClueRemarkDao clueRemarkDao;
    @Autowired
    private CustomerRemarkDao customerRemarkDao;
    @Autowired
    private ContactsRemarkDao contactsRemarkDao;
    @Autowired
    private ClueActivityRelationDao clueActivityRelationDao;
    @Autowired
    private ContactsActivityRelationDao contactsActivityRelationDao;
    @Autowired
    private TranDao tranDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;

    @Override
    public boolean saveClue(Clue clue) {
        // 调用方法
        int i = clueDao.saveClue(clue);
        if (i != 1) {
            // 保存失败
            return false;
        }
        return true;
    }

    @Override
    public Clue getDetail(String id) {
        // 调取dao层方法
        Clue detail = clueDao.getDetail(id);
        return detail;
    }

    @Override
    public boolean deleteRelationById(String id) {
        int i = clueDao.deleteRelationById(id);
        if (i != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean boundRelation(String uuid, String cId, String aId) {
        int i = clueDao.boundRelation(uuid, cId, aId);
        if (i != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean saveCustomerByConvert(String clueId, String createBy, Tran tran) {
        boolean flag = true;

        // 1.通过线索获取线索对象
        Clue clue = clueDao.getClueById(clueId);

        // 2.通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司名称精确匹配）
        String company = clue.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        // 判断客户是否存在
        if (customer == null) {
            // 不存在，新建客户
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setCreateBy(createBy);
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setName(company);
            // 其他信息全在线索里
            int i = customerDao.saveCustomerByClueAndCustomer(customer, clue);
            if (1 != i) {
                // 创建失败
                flag = false;
            }
        }

        // 3.通过线索对象提取联系人信息，保存联系人(不需要判断，可以同时填多个)
        String contactsId = UUIDUtil.getUUID();
        String customerId = customer.getId();
        int i2 = contactsDao.saveContactsByClueAndId(contactsId, customerId, clue);
        if (i2 != 1) {
            // 创建失败
            flag = false;
        }

        // 4.线索备注转换到客户备注以及联系人备注
        // 查询出于该线索有关的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getClueRemarkListByClueId(clueId);
        // 遍历集合取出clueRemark
        for (ClueRemark clueRemark: clueRemarkList) {
            // 只需取出备注信息即可
            String noteContent = clueRemark.getNoteContent();

            // 创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(DateTimeUtil.getSysTime());
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int i3 = customerRemarkDao.saveCustomerRemark(customerRemark);
            if (i3 != 1) {
                flag = false;
            }

            // 创建客户备注对象，添加客户备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(DateTimeUtil.getSysTime());
            contactsRemark.setContactsId(contactsId);
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int i4 = contactsRemarkDao.saveContactsRemark(contactsRemark);
            if (i4 != 1) {
                flag = false;
            }
        }

        // 5.线索和市场活动的关系 转换 到联系人和市场活动的关系
        // 查询出与该条线索关联的市场活动
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        // 遍历出相关联的市场活动的id
        for (ClueActivityRelation car: clueActivityRelationList) {
            String activityId = car.getActivityId();
            // 创建对象，属性赋值
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contactsId); // 第三步生成，直接赋值
            // 调用dao层
            int i5 = contactsActivityRelationDao.saveContactsActivity(contactsActivityRelation);
            if (i5 != 1) {
                flag = false;
            }

        }

        // 6.如果有创建交易需求，创建一条交易
        if (tran != null) {
            // 补全tran
            tran.setId(UUIDUtil.getUUID());
            tran.setCreateTime(DateTimeUtil.getSysTime());
            tran.setOwner(clue.getOwner());
            tran.setDescription(clue.getDescription());
            tran.setSource(clue.getSource());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setCustomerId(customerId);
            tran.setContactsId(contactsId);
            tran.setContactSummary(clue.getContactSummary());
            // 调用dao层添加交易
            int i6 = tranDao.saveTranFromConvert(tran);
            if (i6 != 1) {
                flag = false;
            }
            // 7.如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(DateTimeUtil.getSysTime());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setStage(tran.getStage());
            tranHistory.setTranId(tran.getId());
            // 添加交易历史
            int i7 = tranHistoryDao.saveHistory(tranHistory);
            if (i7 != 1) {
                flag = false;
            }
        }

        // 8.删除线索备注
        for (ClueRemark clueRemark: clueRemarkList) {
            // 直接传入对象进行删除
            int i8 = clueRemarkDao.deleteRemark(clueRemark);
            if (i8 != 1) {
                flag = false;
            }
        }

        // 9.删除线索和市场活动的关联关系
        for (ClueActivityRelation clueActivityRelation: clueActivityRelationList) {
            int i9 = clueActivityRelationDao.delete(clueActivityRelation);
            if (i9 != 1) {
                flag = false;
            }
        }

        // 10.删除线索
        int i10 = clueDao.deleteClue(clueId);
        if (i10 != 1) {
            flag = false;
        }
        return flag;
    }

}
