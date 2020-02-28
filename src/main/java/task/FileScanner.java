package task;


import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScanner {


    // 1.核心线程数：始终运行的线程数
    // 2.最大线程数：有新任务并且当前运行线程数小于最大线程数，会创建新的线程来处理任务（正式工+临时工）
    // 3-4.超过 3 这个数量，4 这个时间个单位，2-1（最大线程数-核心线程数）这些线程（临时工）就会关闭
    // 5.工作的阻塞队列
    // 6.如果超出工作队列的长度，任务要处理的方式(4中策略需要知道)
//    private ThreadPoolExecutor pool = new ThreadPoolExecutor(
//            3, 3,  0, TimeUnit.MICROSECONDS,
//            new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy()
//    );

    private ExecutorService pool = Executors.newFixedThreadPool(4);


    // 计数器,不传入数值，表示初始化的值为 0
    private volatile AtomicInteger count = new AtomicInteger();

    // 线程等待的锁对象
    // 方法一：synchronized(lock) 进行 wait() 等待
    private Object lock = new Object();

    // 方法二：await() 阻塞等待直到 latch == 0
    private CountDownLatch latch = new CountDownLatch(1);

    // 方法三：acquire() 阻塞并等待一定数量的许可
    private Semaphore semaphore = new Semaphore(0);

    private ScanCallback callback;

    public FileScanner(ScanCallback callback) {
        this.callback = callback;
    }

    /**
     * 扫描文件目录
     * 最开始不知道有多少子文件夹，不知道应该启动多少线程
     * @param path
     */
    public void scan(String path) {
        // 启动根目录是扫描任务，计数器 ++i
        count.incrementAndGet();
        doScan(new File(path));
    }

    /**
     * @param dir 待处理的文件夹
     */
    private void doScan(File dir) {

        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 文件保存操作
                    callback.callback(dir);
                    // 下一级的文件和文件夹
                    File[] children = dir.listFiles();
                    if (children != null) {
                        for (File child : children) {
                            // 如果是文件夹，递归处理
                            if (child.isDirectory()) {
//                                System.out.println("文件夹" + child.getPath());
                                // ++i
                                count.incrementAndGet();
                                doScan(child);
                            }
//                            else {
//                                // 如果是文件
////                                System.out.println("文件" + child.getPath());
//                            }
                        }
                    }
                } finally {
                    // 保证线程计数不管是否出现异常，都能进行--操作
                    // --i
                    int r = count.decrementAndGet();
                    if (r == 0) {
                        // 方法一：
//                        synchronized (lock) {
//                            lock.notify();
//                        }
                        // 方法二：
                        latch.countDown();
                        // 方法三：
//                        semaphore.release();
                    }
                }
            }
        });
    }

    /**
     * 等待扫描任务结束(scan方法)
     * 多线程的任务等待
     * 1.join():需要使用线程 Thread 类的引用对象
     * 2.wait() 线程间的等待
     */
    public void waitFinish() throws InterruptedException {
        try {
            // 方法一：
//        synchronized (lock) {
//            lock.wait();
//        }
            // 方法二：
            latch.await();
            // 方法三：
//        semaphore.acquire();
        } finally {
            // 阻塞等待直到任务完成，完成后需要关闭线程池
            // 两种关闭线程池的方式，内部原理都是通过内部的 Thread.interrupt() 来中断
//        pool.shutdown();
            pool.shutdownNow();
        }
    }



}
