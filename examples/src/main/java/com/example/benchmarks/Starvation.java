import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Starvation {

  public static int maxPassengers = 10;
  public static int passengersOnboard = 0;

  public static void main(String[] args) {
    for (int i = 0; i < 15; i++) {
      if (i % 2 == 0) {
        new PassengerDetails("XYZ", i, PassengerClass.Economy);
      } else {
        new PassengerDetails("ABC", i, PassengerClass.Business);
      }
    }

    test();
  }

  public static void test() {

    Thread t1 = new Thread() {
      @Override
      public void run() {
        while (true) {
          System.out.println("Onboarded Customers :" + passengersOnboard);
          if (passengersOnboard < maxPassengers) {
            System.out.println("Checking If que is avaiable in thread 1:");
            if (!CheckInCounter.que.isEmpty()) {
              System.out.println("Checking If Business Customers Are Present :");
              if (CheckInCounter.CheckIfBusinessCustomersArePresent()) {
                System.out.println("Onboarding Business Class Customer :");
                passengersOnboard++;
                CheckInCounter.que.remove(CheckInCounter.que.get(CheckInCounter.SendTheIDOfThePresentBusinessCustomer()).idNumber);
                try {
                  Thread.sleep(6000); 
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              } else {
                System.out.println("All Business Customers are Onboarded");
              }
            }
          } else
            break;
        }
      }
    };

    Thread t2 = new Thread() {
      @Override
      public void run() {
        while (true) {
          if (passengersOnboard < maxPassengers) {
            System.out.println("Checking If que is avaiable in thread 2:");
            if (!CheckInCounter.que.isEmpty()) {
              System.out.println("Checking If there are no business Customers left :" + CheckInCounter.CheckIfBusinessCustomersArePresent());
              if (!CheckInCounter.CheckIfBusinessCustomersArePresent()) {
                System.out.println("Onboarding Economy Class Customer :");
                passengersOnboard++;
                CheckInCounter.que.remove(CheckInCounter.que.get(CheckInCounter.SendTheIDOfThePresentBusinessCustomer()).idNumber);
              } else
                System.out.println("Please wait until Business class customers are onboarded");
              try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
          } else
            break;
        }
      }
    };

    t1.start();
    t2.start();
  }
}

class CheckInCounter {

  final Lock lock = new ReentrantLock();
  public final static HashMap < Integer, PassengerDetails > que = new HashMap < Integer, PassengerDetails > ();


  public static boolean CheckIfBusinessCustomersArePresent() {
    boolean s = false;
    for (int i = 0; i < que.size(); i++) {
      try {
        if (que.get(i).pClass == PassengerClass.Business) {
          s = true;
        }
      } catch (NullPointerException e) {}
    }
    return s;
  }

  public static int SendTheIDOfThePresentBusinessCustomer() {
    int s = -1;
    for (int i = 0; i < que.size(); i++) {
      try {
        if (que.get(i).pClass == PassengerClass.Business) {
          s = i;
        } else {
          s = i;
        }
      } catch (NullPointerException e) {}

    }
    return s;
  }

  public static void add(PassengerDetails pD) {
    que.put(pD.idNumber, pD);
  }
}

class PassengerDetails {
  public PassengerClass pClass;
  public String passengerName;
  public int idNumber;

  public PassengerDetails(String name, int id, PassengerClass customerclass) {
    passengerName = name;
    idNumber = id;
    pClass = customerclass;
    CheckInCounter.add(this);
  }
}

enum PassengerClass {
  Business,
  Economy,
}
