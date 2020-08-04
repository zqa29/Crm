package per.zqa.crm.exception;

/*

    没有登录，拦截请求异常

 */
public class InterceptorException extends Exception{

    public InterceptorException(){

        super();

        System.out.println("interceptor");
    }

}
