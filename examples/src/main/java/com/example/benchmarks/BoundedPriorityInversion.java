package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedPriorityInversion {
    public static void main(String[] args) {
        test(new BoundedPriorityInversion(),10);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        BoundedPriorityInversion bpi=new BoundedPriorityInversion();
        test(bpi,data.consumeInt());
    }
    public static void test(BoundedPriorityInversion bpi, int x) {
        ReentrantLock lock = new ReentrantLock();
        boolean[] isScanned = new boolean[]{(x%2==0) ? true: false};
        Thread threadA = new Thread(() ->
        {
            if (lock.tryLock()) {
                bpi.print(isScanned[0]);
                processing(4000);
            }
        });
        Thread threadB = new Thread(() ->
        {
            processing(2000);
            if (lock.tryLock()) {
                bpi.print(isScanned[0]);
                processing(3000);
            }
        });
        threadA.setPriority(Thread.MIN_PRIORITY);
        threadB.setPriority(Thread.MAX_PRIORITY);
        threadA.start();
        threadB.start();
    }
    private static void print(boolean isScanned) {
        if (isScanned) { //System.out.println("$");
             }
        else { //System.out.println("%");
             }
    }
    private static void processing(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}