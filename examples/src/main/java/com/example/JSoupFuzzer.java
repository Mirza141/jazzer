package com.example;

import org.jsoup.Jsoup;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class JSoupFuzzer{
  public static void fuzzerTestOneInput(FuzzedDataProvider data) {
int[] input = { 2, 40, 41, 5};
input[0]=data.consumeInt();
input[1]=data.consumeInt();
input[2]=data.consumeInt();
input[3]=data.consumeInt();
var sumNumbers = new Sum();
var sumSquares = new Sum();
var threads = new ArrayList<Thread>();
 for (var i: input) 
{
            if ( i % 2 == 0)
	    {
                threads.add(new Thread()
                {
                    public void run() 
                    {
                        synchronized(sumNumbers)
                        {
                            sumNumbers.value += i;
                            System.out.println("a1 " + i );
                            try
			    { Thread.sleep(i);} 
			    catch (Exception e) {}  
                            synchronized(sumSquares) 
			    {
                                sumSquares.value += i * i;
                                System.out.println("a2 " + i );
                            }
                            System.out.println("a3 "+i+": "+sumNumbers + ", " + sumSquares );

                        }
                    }
                });
            } else
		{
                   threads.add(new Thread () 
		   {
                     public void run() 
		     {
                       synchronized(sumSquares)
		       {
                            sumSquares.value += i * i;
                            System.out.println("b1 " + i );
                            try { Thread.sleep(i);} catch (Exception e) {}
                            synchronized(sumNumbers) 
 			    {
                                sumNumbers.value += i;
                                System.out.println("b2 " + i );
                            }
                            System.out.println("b3 "+i+": "+sumNumbers + ", " + sumSquares );
                        }
                      }
                     });
                  }
}

        for(var t: threads)
	{
            t.start();
            try { Thread.sleep(10);} catch (Exception e) {}  
            System.out.println("t started");
        }
        System.out.println(sumNumbers + "/" + sumSquares +"= something" );
    }
  }


 static class Sum {
        int value = 0;

        public String toString(){
            return value+"";
        }
}
