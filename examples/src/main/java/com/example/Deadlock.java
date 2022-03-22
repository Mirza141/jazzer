package com.example;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.util.ArrayList;

public class Deadlock {
    public static int[] input = new int[4];

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        for (int i = 0; i < input.length; i++)
            InitializeIntegerArray(i, data.consumeInt());
    }

    public static void InitializeIntegerArray(int index, int value) {
        input[index] = value;
        System.out.println(input[index]);
    }

    public static void main(String args[]) {
        final Sum sumNumbers = new Sum();
        final Sum sumSquares = new Sum();
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (final int i : input) {
            if (i % 2 == 0) {
                threads.add(new Thread() {
                    @Override
                    public void run() {
                        synchronized (sumNumbers) {
                            sumNumbers.value += i;
                            System.out.println("a1 " + i);
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                            }
                            synchronized (sumSquares) {
                                sumSquares.value += i * i;
                                System.out.println("a2 " + i);
                            }
                            System.out.println("a3 " + i + ": " + sumNumbers + ", " + sumSquares);
                        }
                    }
                });
            } else {
                threads.add(new Thread() {
                    @Override
                    public void run() {
                        synchronized (sumSquares) {
                            sumSquares.value += i * i;
                            System.out.println("b1 " + i);
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                            }
                            synchronized (sumNumbers) {
                                sumNumbers.value += i;
                                System.out.println("b2 " + i);
                            }
                            System.out.println("b3 " + i + ": " + sumNumbers + ", " + sumSquares);
                        }
                    }
                });
            }
        }

        for (Thread t : threads) {
            t.start();
            System.out.println("t started");
        }
        System.out.println(sumNumbers + "/" + sumSquares + "= something");
    }

    static class Sum {
        int value = 0;

        @Override
        public String toString() {
            return value + "";
        }
    }
}