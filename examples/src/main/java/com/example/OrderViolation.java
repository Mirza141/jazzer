package com.company;

public class OrderViolation
{
    static boolean isScanned=false;
    static OrderViolation oV= new OrderViolation();
    //Main Function
    public static void main(String[] args)
    {
        test(oV);
    }

    //FuzzerTestOneInput for randomized inputs
  /*public static void fuzzerTestOneInput(FuzzedDataProvider data)
  {

  }*/

    //Bug type under test
    public static void test(OrderViolation oV)
    {
        Thread firstThread = new Thread(() ->
        {
            synchronized (oV)
            {
                oV.scan();
            }

        });

        Thread secondThread = new Thread(() ->
        {
            synchronized (oV)
            {
                oV.print();
            }
        });

        firstThread.start();
        secondThread.start();
    }

    private static void scan()
    {
        isScanned=true;
    }
    private static void print()
    {
        if(isScanned)
        {
            System.out.println("printed");
        }
        else
        {
            System.out.println("Please scan first");
        }
    }

    private static void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
