import com.remoting.transport.socket.RpcClient;
import com.remoting.transport.socket.RpcClientProxy;
import service.HelloService;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1",8080);
        HelloService service = proxy.getClinet(HelloService.class);
        String s = service.sayHello("李四！");
        System.out.println(s);
    }
}
