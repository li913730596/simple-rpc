import com.remoting.transport.client.RpcClientProxy;
import com.remoting.transport.socket.SocketClient;
import service.Hello;
import service.HelloService;

public class SocketClientTest {

    public static void main(String[] args) {
        SocketClient client = new SocketClient("127.0.0.1",8080);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService proxyClient = proxy.getClient(HelloService.class);
        String s = proxyClient.sayHello(new Hello("张三", "1"));
        System.out.println(s);

    }
}
