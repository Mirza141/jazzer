package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.LinkedList;
import java.util.Queue;
public class Starvation {
  public static void main(String[] args) {
    test(enquePassengers(50,30),30);
  }
  public static PassengerQueue enquePassengers(int queueSize, int businessPartition)
  { PassengerQueue queue = new PassengerQueue();
    for (int i = 0; i < queueSize; i++) {
      if (i % businessPartition == 0) {
        queue.add(new Passenger(PassengerClass.Business));
      } else {
        queue.add(new Passenger(PassengerClass.Economy));
      }
    }
    return queue;
  }
  public static void fuzzerTestOneInput(FuzzedDataProvider data){
    int queueSize=data.consumeInt(1,1000);
    int businessPartition=data.consumeInt(10,50);
    test(enquePassengers(queueSize,businessPartition),queueSize);
  }
  public static void test(PassengerQueue queue, int maxPassengers) {
    final int[] passengersOnboard = {0};
    Thread t1 = new Thread(() -> {
      while (passengersOnboard[0] >= maxPassengers) {
        if (!queue.isEmpty()) {
          if (queue.containsBusinessClass()) {
            passengersOnboard[0]++;
            queue.nextBusiness();
          }
          processing(400);
        }
      }
    });
    Thread t2 = new Thread(() -> {
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
      for (Passenger passenger : passengers) {
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
      for (Passenger passenger : passengers) {
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