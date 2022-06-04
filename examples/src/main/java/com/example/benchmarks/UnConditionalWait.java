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
        Thread printingThread = new Thread(() -> {
            printer.printingPages(pagesToPrint);
        });
        Thread addingPagesThread = new Thread(() -> {
            printer.addPages(pagesToAdd);
        });
        printingThread.start();
        addingPagesThread.start();
    }

    synchronized void printingPages(int pages) {
        System.out.println("$");
        if (this.noOfPaper < pages) {
            try {
                wait();
            } catch (Exception e) {
            }
        }
    }

    synchronized void addPages(int noOfPages) {
        System.out.println("%");
        this.noOfPaper += noOfPages;
        notify();
    }
}