package per.zqa.crm.settings.domain;

public class UserLogin {

    private String ulLoginAct;
    private String ulLoginPwd;
    private String Ip;

    public UserLogin() {
    }

    public UserLogin(String ulLoginAct, String loginPwd) {
        this.ulLoginAct = ulLoginAct;
        this.ulLoginPwd = loginPwd;
    }

    public String getUlLoginAct() {
        return ulLoginAct;
    }

    public void setUlLoginAct(String ulLoginAct) {
        this.ulLoginAct = ulLoginAct;
    }

    public String getUlLoginPwd() {
        return ulLoginPwd;
    }

    public void setUlLoginPwd(String ulLoginPwd) {
        this.ulLoginPwd = ulLoginPwd;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        Ip = ip;
    }
}
