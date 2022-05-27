public class AtomicityViolation {
  static int y = 0;
  static int a = -1;
  static int x = -1;
  //Main Function
  public static void main(String[] args) {
    test();
  }

  //FuzzerTestOneInput for randomized inputs
  /*public static void fuzzerTestOneInput(FuzzedDataProvider data)
  {

  }*/

  //Bug type under test
  public static void test() {
    Thread firstThread = new Thread(() -> {
      y = calculate();
      sleep(3000);

    });

    Thread secondThread = new Thread(() -> {

      a = x / y;
    });

    firstThread.start();
    secondThread.start();
  }

  private static int calculate() {
    return y + y;
  }

  private static void sleep(int delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
