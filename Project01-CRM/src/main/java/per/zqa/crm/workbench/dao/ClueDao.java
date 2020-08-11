package per.zqa.crm.workbench.dao;

import org.apache.ibatis.annotations.Param;
import per.zqa.crm.workbench.domain.Clue;

public interface ClueDao {

    int saveClue(Clue clue);

    Clue getDetail(String id);

    int deleteRelationById(String id);

    int boundRelation(@Param("id") String uuid, @Param("cId") String cId, @Param("aId") String aId);

    Clue getClueById(String clueId);

    int deleteClue(String clueId);
}
