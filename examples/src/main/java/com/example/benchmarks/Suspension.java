package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.LinkedList;
import java.util.Queue;

public class Suspension {
  public static void main(String[] args) {
    test(addInventory(10));
  }
  public static void fuzzerTestOneInput(FuzzedDataProvider data)
  {
    test(addInventory(data.consumeInt(10,50)));
  }
  public static InventoryControl addInventory(int inventorySize)
  {
    InventoryControl inventoryControl = new InventoryControl();
    for (int i = 0; i < inventorySize; i++) {
      inventoryControl.addInventory(i);
    }
    return inventoryControl;
  }
  public static void test(InventoryControl inventory) {
    Thread firstAssociate = new Thread(() -> {
        check(inventory,"$");
    });
    Thread secondAssociate = new Thread(() -> {
      check(inventory,"%");
    });
    firstAssociate.start();
    secondAssociate.start();
  }
  public static void check(InventoryControl inventory, String threadName)  {
    synchronized(inventory) {
      inventory.reduceQunatityOnHand();
      System.out.println(threadName);
    }
  }
  private static void processing(int delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  static class InventoryControl {
    private final Queue < Integer > inventory = new LinkedList < Integer > ();
    public void addInventory(int itemNumber) {
      inventory.add(itemNumber);
    }
    public void reduceQunatityOnHand()  {
      while (!inventory.isEmpty()) {
          inventory.remove();
      }
      processing(50);
    }
    public boolean isEmpty() {
      return inventory.isEmpty();
    }
    public Integer nextInventory() {
      return inventory.poll();
    }
  }
}