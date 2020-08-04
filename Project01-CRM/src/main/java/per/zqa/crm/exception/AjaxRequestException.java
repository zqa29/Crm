package per.zqa.crm.exception;

/*

    ajax请求异常

 */
public class AjaxRequestException extends Exception{

    public AjaxRequestException(){}

    public AjaxRequestException(String msg){

        super(msg);

        System.out.println(msg);

    }

}
