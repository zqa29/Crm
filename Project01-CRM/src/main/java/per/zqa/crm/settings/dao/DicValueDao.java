package per.zqa.crm.settings.dao;

import per.zqa.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {

    List<DicValue> getValueListByCode(String typeCode);
}
