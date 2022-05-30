import java.util.concurrent.locks.ReentrantLock;

public class BoundedPriorityInversion {
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        Thread threadA = new Thread(() ->
        {
            System.out.println("Doing something in first thread");
            if (lock.tryLock()) {
                System.out.println("first thread which has the low priority is entered in critical section");
                processing(13000);
            }
        });

        Thread threadB = new Thread(() ->
        {
            System.out.println("Doing something in Second thread");
            processing(3000);
            if (lock.tryLock()) {
                System.out.println("second thread which has the highest priority is entered in critical section after low priority task");
                processing(5000);
            } else {
                System.out.println("First thread is holding the lock although it has low priority");
            }
        });
        threadA.setPriority(Thread.MIN_PRIORITY);
        threadB.setPriority(Thread.MAX_PRIORITY);
        threadA.start();
        threadB.start();
    }

    private static void processing(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
