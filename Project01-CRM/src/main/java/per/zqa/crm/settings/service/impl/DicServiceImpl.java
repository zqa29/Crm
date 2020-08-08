package per.zqa.crm.settings.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.zqa.crm.settings.dao.DicTypeDao;
import per.zqa.crm.settings.dao.DicValueDao;
import per.zqa.crm.settings.domain.DicType;
import per.zqa.crm.settings.domain.DicValue;
import per.zqa.crm.settings.service.DicService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DicServiceImpl implements DicService {
    @Autowired
    private DicTypeDao dicTypeDao;
    @Autowired
    private DicValueDao dicValueDao;

    @Override
    public Map<String, List<DicValue>> getAll() {
        // 1.获取type表的list
        List<DicType> typeList = dicTypeDao.getTypeList();

        // 2.创建map
        Map<String,List<DicValue>> map = new HashMap<>();

        // 3.遍历集合获取得到List里的DicType
        for (DicType dicType:typeList) {
            // 取得字典类型编码
            String typeCode = dicType.getCode();
            // 4.获取value表typeCode与type表code相等的List
            List<DicValue> valueList = dicValueDao.getValueListByCode(typeCode);
            // 5.将返回的List放入到map中,key为typeCode
            map.put(typeCode, valueList);
        }

        return map;
    }
}
