package per.zqa.crm.exception;

/*

    登录失败异常

 */
public class LoginException extends Exception{

    public LoginException(String msg){

        super(msg);
        
        System.out.println(msg);

    }

}
