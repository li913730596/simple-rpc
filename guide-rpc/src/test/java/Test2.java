import com.remoting.transport.socket.ClientMessageHandlerThread;
import com.remoting.transport.socket.RpcClient;
import com.remoting.transport.socket.RpcClientProxy;
import service.HelloService;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1",8080);
        HelloService service = proxy.getClinet(HelloService.class);
        System.out.println(service.sayHello("张三！"));
    }
}
