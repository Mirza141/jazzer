package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.LinkedList;
import java.util.Queue;

public class Suspension {
  public static void main(String[] args) {
    int inventorySize = 10;
    InventoryControl inventoryControl = new InventoryControl();
    for (int i = 0; i < inventorySize; i++) {
      inventoryControl.addInventory(i);
    }
    test(inventoryControl);
  }
  public static void fuzzerTestOneInput(FuzzedDataProvider data)
  {
    int inventorySize = data.consumeInt();
    InventoryControl inventoryControl = new InventoryControl();
    for (int i = 0; i < inventorySize; i++) {
      inventoryControl.addInventory(i);
    }
    test(inventoryControl);
  }
  public static void test(InventoryControl inventory) {
    Thread firstAssociate = new Thread(() -> {
        check(inventory,"First");
    });
    Thread secondAssociate = new Thread(() -> {
      check(inventory,"Second");
    });
    firstAssociate.start();
    secondAssociate.start();
  }
  public static void check(InventoryControl inventory, String threadName)  {
    synchronized(inventory) {
      inventory.reduceQunatityOnHand();
      System.out.println(threadName+" Associate is getting rid of items");
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
      processing(1000);
    }
    public boolean isEmpty() {
      return inventory.isEmpty();
    }
    public Integer nextInventory() {
      return inventory.poll();
    }
  }
}