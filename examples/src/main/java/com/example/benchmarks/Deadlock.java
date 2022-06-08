package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.ArrayList;

public class Deadlock {

    public static void main(String[] args) {
        test(new Sum(),new Sum(),initializeArray(1));
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        test(new Sum(),new Sum(),initializeArray(data.consumeInt()));
    }

    public static int[] initializeArray(int value) {
        int[] input = new int[4];
        for (int i = 0; i < input.length; i++) {
            input[i] = value+i;
        }
        return input;
    }

    public static void test(final Sum sumNumbers, final Sum sumSquares, int [] input) {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (final int i : input) {
            if (i % 2 == 0) {
                threads.add(new Thread() {
                    @Override
                    public void run() {
                        synchronized (sumNumbers) {
                            sumNumbers.value += i;
                            delay(100);
                            synchronized (sumSquares) {
                                sumSquares.value += i * i;
                            }
                        }
                    }
                });
            } else {
                threads.add(new Thread() {
                    @Override
                    public void run() {
                        synchronized (sumSquares) {
                            sumSquares.value += i * i;
                            delay(100);
                            synchronized (sumNumbers) {
                                sumNumbers.value += i;
                            }
                        }
                    }
                });
            }
        }

        for (Thread t : threads) {
            t.start();
        }
    }

    private static void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Sum {
        int value = 0;

        @Override
        public String toString() {
            return value + "";
        }
    }
}