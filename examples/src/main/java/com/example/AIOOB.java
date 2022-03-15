package com.example;
import java.util.ArrayList;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

class AIOOB
{
    public static void main(String[] args)
    {
        /*try
	{
            test(2, 4, 5);
            System.out.println("1 ok");
        } catch (Exception e) {
            System.out.println("1 fail:\n" + e);
        }

        try {
            test(12, 4, 5);
            System.out.println("2 ok");
        } catch (Exception e) {
            System.out.println("2 fail:\n" + e);
        }

        try {
            test(12, -4, 5);
            System.out.println("3 ok");
        } catch (Exception e) {
            System.out.println("3 fail:\n" + e);
        }*/
    }

    public static void test(int size, int indexToBeRead, int indexToChange)
    {
        ArrayList<Integer> array = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            array.add(i);
        }

        printArrayMultiThreaded(array);
        // updateArrayMultiThreaded(indexToChange)

        array.get(indexToBeRead); // Will sometimes throw an ArrayIndexOutOfBoundsException
    }

    private static void printArrayMultiThreaded(ArrayList<Integer> array)
    {
        ArrayList<Thread> threads = new ArrayList<>();
        int size = array.size();
        for (int i = 0; i < size; i++) {
            final int j = i;
            threads.add(new Thread()
            {
                @Override
                public void run()
                {
                    {
                        System.out.println(array.get(j));
                    }
                }
            });
        }

        for (Thread t : threads) {
            t.start();
        }
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data)
    {
        test(data.consumeInt(), data.consumeInt(), data.consumeInt());
    }

}
