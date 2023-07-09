import com.remoting.transport.client.ClientTransport;
import com.remoting.transport.client.RpcClientProxy;
import com.remoting.transport.netty.client.ChannelProvider;
import com.remoting.transport.netty.client.NettyRpcClientTransport;
import lombok.extern.slf4j.Slf4j;
import service.Hello;
import service.HelloService;

@Slf4j
public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        ChannelProvider channelProvider = new ChannelProvider("127.0.0.1", 8080);
        ClientTransport client = new NettyRpcClientTransport(channelProvider.getChannel());
        RpcClientProxy proxy = new RpcClientProxy(client);

        HelloService proxyClient = proxy.getClient(HelloService.class);
        String hello = proxyClient.sayHello(new Hello("黎明", "2"));
        System.out.println(hello);
        log.info(hello);

    }
}
