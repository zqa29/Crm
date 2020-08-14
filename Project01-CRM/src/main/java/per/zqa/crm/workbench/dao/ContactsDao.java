package per.zqa.crm.workbench.dao;

import org.apache.ibatis.annotations.Param;
import per.zqa.crm.workbench.domain.Clue;
import per.zqa.crm.workbench.domain.Contacts;

public interface ContactsDao {

    int saveContactsByClueAndId(@Param("id") String contactsId, @Param("customerId") String customerId, @Param("c") Clue clue);

    Contacts getContactById(String id);
}
