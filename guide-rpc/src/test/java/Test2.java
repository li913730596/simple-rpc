import com.remoting.transport.socket.RpcClientProxy;
import service.HelloService;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(2000);

        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 8080);
        HelloService clinet = proxy.getClinet(HelloService.class);
        System.out.println(clinet.sayHello("李明"));
    }
}
