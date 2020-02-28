import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class WaitTest {
    /**
     * 等待所有线程执行完毕
     * 1.CountDownLatch：初始化一个值，可以调用 CountDown() 对数值进行 i--，await() 会阻塞并一直等待，直到 LATCH 的值等于 0
     * 2.Semaphore：release() 进行一定数量许可的颁发，acquire() 阻塞并等待一定数量的许可
     * 相对俩说，Semaphore功能更强大，更灵活一些
     */

    private static int COUNT = 5;
    private static CountDownLatch LATCH = new CountDownLatch(COUNT);
    private static Semaphore SEMAPHORN = new Semaphore(0);


    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < COUNT; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                    // i--
//                    LATCH.countDown();
                    // 颁发一定数量的许可证,无参就是颁发一个数量
                    SEMAPHORN.release();
                }
            }).start();
        }
        // main 在所有子线程执行完毕之后，再运行一下代码
        // await() 会阻塞并一直等待，直到 LATCH 的值等于 0
//        LATCH.await();
        // 无参代表请求资源数量为 1，也可以请求指定数量的资源
        SEMAPHORN.acquire(5);
        System.out.println(Thread.currentThread().getName());
    }
}
