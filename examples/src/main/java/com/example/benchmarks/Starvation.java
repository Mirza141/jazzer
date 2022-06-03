package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.LinkedList;
import java.util.Queue;
public class Starvation {

  public static int queueSize = 15;

  public static void main(String[] args) {
    EnquePassenger(new PassengerQueue());
    test(queue,10);
  }
  public static void EnquePassenger(PassengerQueue queue)
  {
    for (int i = 0; i < queueSize; i++) {
      if (i % 2 == 0) {
        queue.add(new Passenger(PassengerClass.Economy));
      } else {
        queue.add(new Passenger(PassengerClass.Business));
      }
    }
  }
  public static void fuzzerTestOneInput(FuzzedDataProvider data){
    EnquePassenger(new PassengerQueue());
    test(queue,data.consumeInt());
  }
  public static void test(PassengerQueue queue, int maxPassengers) {
    final int[] passengersOnboard = {0};
    var t1 = new Thread(() -> {
      while (passengersOnboard[0] >= maxPassengers) {
        if (!queue.isEmpty()) {
          if (queue.containsBusinessClass()) {
            passengersOnboard[0]++;
            queue.nextBusiness();
          }
          processing(4000);
        }
      }
    });
    var t2 = new Thread(() -> {
      while (passengersOnboard[0] >= maxPassengers) {
        if (!queue.isEmpty()) {
          if (!queue.containsBusinessClass()) {
            passengersOnboard[0]++;
            queue.nextEconomy();
          }
          processing(30);
        }
      }
    });
    t1.start();
    t2.start();
  }
  private static void processing(int delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  static class PassengerQueue {
    private final Queue<Passenger> passengers = new LinkedList<>();
    public void add(Passenger passenger) {
      passengers.add(passenger);
    }
    public boolean containsBusinessClass() {
      for (var passenger : passengers) {
        if (passenger.passengerClass == PassengerClass.Business) {
          return true;
        }
      }
      return false;
    }
    public boolean isEmpty() {
      return passengers.isEmpty();
    }
    public Passenger nextEconomy() {
      return passengers.poll();
    }
    public Passenger nextBusiness() {
      Passenger result = null;
      for (var passenger : passengers) {
        if (passenger.passengerClass == PassengerClass.Business) {
          result = passenger;
          break;
        }
      }
      if (result != null) {
        passengers.remove(result);
      }
      return result;
    }
  }
  static class Passenger {
    public PassengerClass passengerClass;
    public Passenger(PassengerClass passengerClass) {
      this.passengerClass = passengerClass;
    }
  }
  enum PassengerClass {
    Business,
    Economy,
  }
}