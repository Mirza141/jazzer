package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class OrderViolation {

    public static void main(String[] args) {
        test(new OrderViolation(),3%2);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        OrderViolation oV=new OrderViolation();
        test(oV,data.consumeInt() % 2);
    }
    public static void test(OrderViolation oV,int x) {
        boolean[] isScanned = new boolean[]{(x==0) ? true: false};
        Thread firstThread = new Thread(() ->
        {
            synchronized (oV) {
               oV.scan(x);
            }
        });
        Thread secondThread = new Thread(() ->
        {
            synchronized (oV) {
                oV.print(isScanned[0]);
            }
        });
        firstThread.start();
        secondThread.start();
    }
    private static boolean scan(int x) {
        return (x==0) ? true: false;
    }
    private static void print(boolean isScanned) {
        if (!isScanned) {
            //System.out.println("%");
        } else {
            //System.out.println("$");
        }
    }
}