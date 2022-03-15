package com.example;
import java.lang.Math;
import java.util.HashMap;
import java.util.Random;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class UnconditionalWait_5000
{
    static int maxCapacity = 5000;
    static int maxWaitingTime = 10000;
    static int stop = 0;

    public static void fuzzerTestOneInput(FuzzedDataProvider data)
    {
        //maxCapacity=data.consumeInt();
        test(data.consumeInt());
    }

    public static void main(String args[])
    {
        for (; stop < 2;) {
            test(5);
        }
    }

    public static void test(int tdata)
    {
        UnConditionalWait ucw = new UnConditionalWait();
        ucw.maxWaitingTime = tdata;
        for (int i = 0; i < maxCapacity; i++) {
            Library.add("Book" + i);
        }
        int randomNumber = ucw.getRandomNumber(0, maxCapacity);
        Boolean bookAvailable = Library.booksHashMap.get(randomNumber).isReady();
        Thread t1 = new Thread()
        {
            @Override
            public void run()
            {
                if (bookAvailable) {
                    System.out.print(": Inside First Thread : \n");
                    synchronized (Library.booksHashMap) {
                        try {
                            Library.booksHashMap.wait(maxWaitingTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Library.checkOut(Library.getBookName(randomNumber));
                        ucw.stop++;
                        System.out.print(": Order successfully purchased  : \n");
                    }
                }

            }
        };

        Thread t2 = new Thread()
        {
            {
                System.out.print(": Inside Second thread : \n");
            }

            @Override
            public void run()
            {
                {
                    synchronized (Library.booksHashMap) {
                        Library.checkOut(Library.getBookName(randomNumber));
                        ucw.stop++;
                        System.out.print(" : Unconditional wait has successfully checked out : \n");
                    }
                }
            }
        };

        t1.start();
        t2.start();
    }

    public int getRandomNumber(int min, int max)
    {
        return (int)(Math.random()*(max-min+1)+min);
    }

}

class Library
{
    final static HashMap<Integer, Book> booksHashMap = new HashMap<>();
    private static int counter = 0;

    public static String getBookName(int bookID)
    {
        String s = null;
        for (Book b : booksHashMap.values()) {
            if (b.bookID == bookID) {
                s = b.bookName;
            }
        }

        return s;
    }

    public static int getKeyOfBookName(String bookName)
    {
        int s = -1;
        for (Book b : booksHashMap.values()) {
            if (b.bookName == bookName) {
                s = b.bookID;
            }
        }

        return s;
    }

    public boolean getisReadyBookName(String bookName)
    {
        boolean s = false;
        for (Book b : booksHashMap.values()) {
            if (b.bookName == bookName) {
                s = b.isBookAvailable;
            }
        }

        return s;
    }

    public static void add(String bookName)
    {
        int uid = counter++;
        booksHashMap.put(uid, new Book(uid, bookName));
    }

    public static void checkOut(String bookName)
    {
        if (bookName == getBookName(getKeyOfBookName(bookName))) {
            int id = getKeyOfBookName(bookName);
            booksHashMap.get(id).isBookAvailable = false;
            booksHashMap.remove(id);
            counter--;
        }
    }

}

class Book
{
    public String bookName;
    public Boolean isBookAvailable = false;
    public int bookID;

    public Book(int id, String title)
    {
        bookName = title;
        bookID = id;
        isBookAvailable = true;
    }

    public Boolean isReady()
    {
        return isBookAvailable;
    }

}
