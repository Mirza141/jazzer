package com.example;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class AtomicityViolation {

    public static void main(String[] args) {
        test(2, 1);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        test(data.consumeInt(), data.consumeInt());
    }

    public static void test(final int x, final int y) {
        final int[] temp = new int[1];
        Thread firstThread = new Thread(() -> {
            temp[0] = calculate(y);
        });
        Thread secondThread = new Thread(() -> {
            temp[0] = x * y;
            System.out.println("%");
        });
        firstThread.start();
        secondThread.start();
    }

    private static int calculate(int y) {
        for (int i=0;i<y;i++) {
            y++;
        }
        delay(30);
        System.out.println("$");
        return y;
    }

    private static void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}