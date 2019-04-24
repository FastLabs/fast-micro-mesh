import ng.abdlquadri.eventbus.EventBus;
import ng.abdlquadri.eventbus.handlers.ConnectHandler;

import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String ... args )  throws Exception{
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        EventBus.connect("127.0.0.1", 7000, new ConnectHandler() {
            @Override
            public void onConnect(boolean isConnected) {

            }

            @Override
            public void onDisConnect(Throwable cause) {

            }


        });
        countDownLatch.await();
    }
}
