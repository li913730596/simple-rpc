import com.remoting.transport.client.ClientTransport;
import com.remoting.transport.client.RpcClientProxy;
import com.common.provider.ChannelProvider;
import com.remoting.transport.netty.client.NettyRpcClientTransport;
import lombok.extern.slf4j.Slf4j;
import service.Hello;
import service.HelloService;

import java.net.InetSocketAddress;

@Slf4j
public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        ChannelProvider channelProvider = new ChannelProvider();
        ClientTransport client = new NettyRpcClientTransport(channelProvider.getChannel(new InetSocketAddress("127.0.0.1",8080)));
        RpcClientProxy proxy = new RpcClientProxy(client);

        HelloService proxyClient = proxy.getClient(HelloService.class);
        String hello = proxyClient.sayHello(new Hello("黎明", "2"));
        System.out.println(hello);
        log.info(hello);

    }
}
