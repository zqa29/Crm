package per.zqa.crm.exception;

/*

    传统请求异常

 */
public class TraditionRequestException extends Exception{

    public TraditionRequestException(){}

    public TraditionRequestException(String msg){

        super(msg);

        System.out.println(msg);
    }

}
