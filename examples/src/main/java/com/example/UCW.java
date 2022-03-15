package com.example;

import java.util.HashMap;
import java.util.Random;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class UCW
{
    private final HashMap<Integer, Book> booksHashMap = new HashMap<>();
    private int counter = 0;
    final static int maxCapacity = 5; // maximum books that can be stored
    static int maxWaitingTime = 10000;
    static int stop = 0;

    public static void fuzzerTestOneInput(FuzzedDataProvider data)
    {
        test(data.consumeInt());
    }

    //public static void main(String args[])
    //{
        //for (; stop < 2;) {
        //    test(5);
      //  }
    //}

    public static void test(int tdata)
    {
        UCW ucw = new UCW();
        ucw.maxWaitingTime = tdata;
        for (int i = 0; i < maxCapacity; i++) {
            ucw.add("Book" + i);
        }
        int randomNumber = ucw.getRandomNumber(0, maxCapacity);

        Thread t1 = new Thread()
        {
            //int randomWait = ucw.getRandomNumber(0, maxWaitingTime);
            Boolean bookAvailable = ucw.booksHashMap.get(randomNumber).isReady();

            @Override
            public void run()
            {
                if (bookAvailable) {
                    System.out.print(": Inside First Thread : ");
                    synchronized (ucw.booksHashMap) {
                        try {
                            ucw.booksHashMap.wait(maxWaitingTime); // wait should be provided by fuzzeddataProvider
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ucw.checkOut(ucw.getBookName(randomNumber));
                        ucw.stop++;
                        System.out.print(": Order successfully purchased  : ");
                    }
                }

            }
        };

        Thread t2 = new Thread()
        {
            {
                System.out.print(": Inside Second thread : ");
            }
            //int randomWait = ucw.getRandomNumber(0, maxWaitingTime);

            @Override
            public void run()
            {
//                if (!bookAvailable)
                {
                    synchronized (ucw.booksHashMap) {
                        // ucw.checkOut(ucw.getBookName(randomNumber)); // this will throw exception for
                        // first thread
                        ucw.stop++;
                        System.out.print(" : Unconditional wait has successfully checked out : ");
                    }
                }
            }
        };

        t1.start();
        t2.start();
    }

    public String getBookName(int bookID)
    {
        String s = null;
        for (Book b : booksHashMap.values()) {
            if (b.bookID == bookID) {
                s = b.bookName;
            }
        }

        return s;
    }

    public int getKeyOfBookName(String bookName)
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

    public void add(String bookName)
    {
        int uid = counter++;
        booksHashMap.put(uid, new Book(uid, bookName));

        // System.out.print(" Book Added with value : "+books.get(uid).bookName);
    }

    private void checkOut(String bookName)
    {
        if (bookName == getBookName(getKeyOfBookName(bookName))) {
            int id = getKeyOfBookName(bookName);
            booksHashMap.get(id).isBookAvailable = false;
            booksHashMap.remove(id);
            counter--;
        }
    }

    public int getRandomNumber(int min, int max)
    {
        Random random = new Random();
        return random.ints(min, max).findFirst().getAsInt();
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
