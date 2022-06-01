package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedPriorityInversion {
    public static void main(String[] args) {
        test(1, 2, 3);
    }
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        test(data.consumeInteger(), data.consumeInteger(), data.consumeInteger());
    }
    public static void test(final int a, final int b, final int c) {
        ReentrantLock lock = new ReentrantLock();
        final int[] i = new int[1];
        Thread threadA = new Thread(() -> {
            i[0] = b + c;
            if (lock.tryLock()) {
                i[0] = b - c;
                processing(4000);
            }
        });
        Thread threadB = new Thread(() -> {
            i[0] = c + a;
            processing(2000);
            if (lock.tryLock()) {
                i[0] = c - a;
                processing(3000);
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