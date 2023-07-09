import com.remoting.transport.socket.SocketServer;
import service.HelloServiceImpl;

public class SocketServerTest {

    public static void main(String[] args) {
        SocketServer server = new SocketServer();
        HelloServiceImpl service = new HelloServiceImpl();
        server.register(service,8080);

    }
}
