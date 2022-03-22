package com.example;

import java.util.ArrayList;// Java program for the above approach
import java.io.*;
import java.util.*;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class ArrayIndexOutOfBound {
    public static ArrayList<Integer> array = new ArrayList<Integer>();
    public static int count = 0;
    public static boolean flag = false;
    public static ArrayList<Thread> threads = new ArrayList<Thread>();

    public static void main(String[] args) {
        for (int i = 0; i < count; i++) {
            final int j = i;
            threads.add(new Thread() {
                public void run() {
                    //synchronized (array)
                    {
                        // this thread will simply print values in the array
                        System.out.println("Array Value " + array.get(j));
                        // this thread should have an outer loop that will go until the length of the array
                        // this thread will update the count
                        if (flag) {
                            flag = !flag;
                            UpdateArrayCount();
                        }
                    }
                }
            });
        }

        threads.add(new Thread() {
            public void run() {
                synchronized (array) {
                    // this thread will update the value of the array
                    UpdateArray(30);
                }
            }
        });

        for (Thread t : threads) {
            t.start();
            System.out.println("t started");
        }

    }

    public static void UpdateArray(int value) {
        // one thread should update array and one should update count
        array.set(0, value);
        flag = true;
    }

    public static void UpdateArrayCount() {
        count++;
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {

        for (int i = 0; i < data.consumeInt(); i++) {
            array.add(0);
        }
        UpdateArray(data.consumeInt());
    }
}
