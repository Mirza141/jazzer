package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class UnConditionalWait {
    static int noOfPaper = 100;

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        noOfPaper = data.consumeInt();
        test(data.consumeInt(),data.consumeInt());
    }

    public static void main(String[] args) {
        test(120,100);
    }
    public static void test(int pagesToPrint, int pagesToAdd) {
        UnConditionalWait printer = new UnConditionalWait();
        new Thread(() -> {
            printer.printingPages(pagesToPrint);
        }).start();
        new Thread(() -> {
            printer.addPages(pagesToAdd);
        }).start();
    }

    synchronized void printingPages(int pages) {
        System.out.println("Printing pages");
        if (this.noOfPaper < pages) {
            System.out.println("Number of Papers in printer are less");
            try {
                System.out.println("Waiting Unconditionally");
                wait();
            } catch (Exception e) {
            }
        }
        System.out.println("After called notify() method number of Paper : " + this.noOfPaper);
        System.out.println("Printing process complete");
    }

    synchronized void addPages(int noOfPages) {
        this.noOfPaper += noOfPages;
        notify();
    }
}