package per.zqa.crm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化插件
 */
public class DateTimeUtil {

    public static String getSysTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date();
        String format = sdf.format(date);

        return format;
    }

    public static String getSysTimeForUpload(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        Date date = new Date();
        String dateStr = sdf.format(date);

        return dateStr;

    }
}
