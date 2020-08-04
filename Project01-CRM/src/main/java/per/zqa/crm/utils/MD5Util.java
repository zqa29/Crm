package per.zqa.crm.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密
 */
public class MD5Util {

    public static String getMD5(String password) {
        try {
            // 信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            // 把每一个byte做一个与运算0xff
            for (byte b: result) {
                int i = b & 0xff; // 与运算
                String s = Integer.toHexString(i);
                if (s.length() == 1) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(s);
            }
            // 标准MD5加密后的结果
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

}
