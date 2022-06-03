package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedPriorityInversion { // adapt with order violation
    static boolean isScanned = false;
    public static void main(String[] args) {
        test(new BoundedPriorityInversion());
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        BoundedPriorityInversion oV=new BoundedPriorityInversion();
        if (data.consumeInt() % 2 == 0) { oV.isScanned=false; }
        else { oV.isScanned=true; }
        test(oV);
    }
    public static void test(BoundedPriorityInversion bpi) {
        ReentrantLock lock = new ReentrantLock();
        Thread threadA = new Thread(() ->
        {
            bpi.scan();
            if (lock.tryLock()) {
                bpi.print();
                processing(4000);
            }
        });
        Thread threadB = new Thread(() ->
        {
            processing(2000);
            if (lock.tryLock()) {
                bpi.print();
                processing(3000);
            }
        });
        threadA.setPriority(Thread.MIN_PRIORITY);
        threadB.setPriority(Thread.MAX_PRIORITY);
        threadA.start();
        threadB.start();
    }
    private static void scan() {
        isScanned = true;
    }
    private static void print() {
        if (isScanned) { System.out.println("$"); }
        else { System.out.println("%"); }
    }

    private static void processing(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}