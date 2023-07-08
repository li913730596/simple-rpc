import com.remoting.transport.client.RpcClient;
import com.remoting.transport.client.RpcClientProxy;
import com.remoting.transport.netty.client.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;
import service.Hello;
import service.HelloService;
import service.HelloServiceImpl;
@Slf4j
public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        RpcClient client = new NettyRpcClient(8080,"127.0.0.1");
        RpcClientProxy proxy = new RpcClientProxy(client);

        HelloService proxyClient = proxy.getClient(HelloService.class);
        String hello = proxyClient.sayHello(new Hello("黎明", "2"));
        System.out.println(hello);
        log.info(hello);

    }
}
