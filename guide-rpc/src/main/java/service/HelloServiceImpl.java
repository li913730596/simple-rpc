package service;

public class HelloServiceImpl implements HelloService{

    @Override
    public String sayHello(Hello hello) {
        return "hello :" + hello.getMessage();
    }
}
