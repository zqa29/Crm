package per.zqa.crm.workbench.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.zqa.crm.workbench.dao.TranDao;
import per.zqa.crm.workbench.service.TranService;
@Service
public class TranServiceImpl implements TranService {

    @Autowired
    private TranDao tranDao;

}
