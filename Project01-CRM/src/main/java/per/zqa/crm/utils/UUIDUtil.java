package per.zqa.crm.utils;

import java.util.UUID;

/**
 * 生成32位的随机串
 */
public class UUIDUtil {

    public static String getUUID() {

        return UUID.randomUUID().toString().replaceAll("-", "");

    }

}
