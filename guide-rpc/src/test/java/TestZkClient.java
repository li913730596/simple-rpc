import com.common.provider.ChannelProvider;
import com.common.registry.ZkServiceRegistry;
import com.remoting.transport.client.RpcClientProxy;
import com.remoting.transport.netty.client.NettyClient;
import com.remoting.transport.netty.client.NettyRpcClientTransport;
import org.junit.jupiter.api.Test;
import service.Hello;
import service.HelloService;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class TestZkClient {
   @Test
    public void zkTest(){




   }

    public static void main(String[] args) {
        ZkServiceRegistry zkServiceRegistry = new ZkServiceRegistry();
        InetSocketAddress address = zkServiceRegistry.lookupService(HelloService.class.getCanonicalName());

        String hostName = address.getHostName();
        int port = address.getPort();

        RpcClientProxy proxy = new RpcClientProxy(new NettyRpcClientTransport(new ChannelProvider(hostName, port).getChannel()));
        HelloService service = proxy.getClient(HelloService.class);
        String s = service.sayHello(new Hello("李红", "11"));
        System.out.println(s);
    }

}

