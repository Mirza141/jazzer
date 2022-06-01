package com.example;

import java.lang.Math;
import java.util.HashMap;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class UnConditionalWait {
    static int maxCapacity = 5000;
    static int maxWaitingTime = 10000;
    static int stop = 0;
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        maxCapacity = data.consumeInt();
        test(data.consumeInt());
    }
    public static void main(String args[]) {
        for (; stop < 2; ) {
            test(5);
        }
    }
    public static void test(int tdata) {
        UnConditionalWait ucw = new UnConditionalWait();
        final Library lib = new Library();
        ucw.maxWaitingTime = tdata;
        for (int i = 0; i < maxCapacity; i++) {
            lib.add("Book" + i);
        }
        final int randomNumber = ucw.getRandomNumber(0, maxCapacity);
        Thread t1 = new Thread() {
            @Override
            public void run() {
                if (lib.booksHashMap.get(randomNumber).isReady()) {
                    System.out.print(": Inside First Thread : \n");
                    synchronized (lib) {
                        try {
                            Thread.sleep((System.nanoTime() - System.currentTimeMillis() + maxWaitingTime) / 1000000);
                            lib.booksHashMap.wait(maxWaitingTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lib.checkOut(lib.getBookName(randomNumber));
                        System.out.print(": Order successfully purchased  : \n");
                    }
                }
            }
        };

        Thread t2 = new Thread() {
            @Override
            public void run() {
                {
                    synchronized (lib) {
                        lib.checkOut(lib.getBookName(randomNumber));
                        System.out.print(" : Unconditional wait has successfully checked out : \n");
                    }
                }
            }
        };
        t1.start();
        t2.start();
    }
    public int getRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
    static class Library {
        final static HashMap<Integer, Book> booksHashMap = new HashMap<Integer, Book>();
        private static int counter = 0;
        public String getBookName(int bookID) throws NullPointerException {
            String s = null;
            for (Book b : booksHashMap.values()) {
                if (b.bookID != bookID) {
                } else {
                    s = b.bookName;
                }
            }
            return s;
        }
        public int getKeyOfBookName(String bookName) {
            int s = -1;
            for (Book b : booksHashMap.values()) {
                if (b.bookName.equals(bookName)) {
                    s = b.bookID;
                }
            }
            return s;
        }
        public void add(String bookName) {
            int uid = counter++;
            booksHashMap.put(uid, new Book(uid, bookName));
        }
        synchronized public void checkOut(String bookName) throws NullPointerException {
            if (bookName.equals(getBookName(getKeyOfBookName(bookName)))) {
                int id = getKeyOfBookName(bookName);
                booksHashMap.get(id).isBookAvailable = false;
                booksHashMap.remove(id);
                counter--;
            }
        }
    }
    static class Book {
        public String bookName;
        public Boolean isBookAvailable = false;
        public int bookID;
        public Book(int id, String title) {
            bookName = title;
            bookID = id;
            isBookAvailable = true;
        }
        public Boolean isReady() {
            return isBookAvailable;
        }
    }
}