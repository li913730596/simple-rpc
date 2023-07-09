import com.remoting.transport.netty.server.NettyRpcServer;
import service.HelloService;
import service.HelloServiceImpl;

public class TestZkServer {
    public static void main(String[] args) {
        NettyRpcServer rpcServer = new NettyRpcServer("127.0.0.1", 8080);
        HelloService service = new HelloServiceImpl();
        rpcServer.publishService(service, HelloService.class);
    }
}
