package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class OrderViolation {
    static boolean isScanned = false;

    public static void main(String[] args) {
        test(new OrderViolation());
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        OrderViolation oV=new OrderViolation();
        if (data.consumeInt() % 2 == 0) { oV.isScanned=false; }
        else { oV.isScanned=true; }
        test(oV);
    }
    public static void test(OrderViolation oV) {
        Thread firstThread = new Thread(() ->
        {
            synchronized (oV) {
                oV.scan();
            }
        });
        Thread secondThread = new Thread(() ->
        {
            synchronized (oV) {
                oV.print();
            }
        });
        firstThread.start();
        secondThread.start();
    }
    private static void scan() {
        isScanned = true;
    }
    private static void print() {
        if (isScanned) { System.out.println("$"); }
        else { System.out.println("%"); }
    }
}