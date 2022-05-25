package com.example;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class Suspension {
  public static int inventorySize = 10;
  public static void main(String[] args) {
    InventoryControl inventoryControl = new InventoryControl();
    for (int i = 0; i < inventorySize; i++) {
      inventoryControl.addInventory(i);
    }
    test(inventoryControl);
  }

  //FuzzerTestOneInput for randomized inputs
  /*public static void fuzzerTestOneInput(FuzzedDataProvider data)
  {

  }*/

  //Bug type under test
  public static void test(InventoryControl inventory) {
    Thread firstAssociate = new Thread(() -> {
      synchronized(inventory) {
        while (!inventory.isEmpty()) {
          try {
            inventory.reduceQunatityOnHand();
            System.out.println("First Associate is getting rid of items");
            sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });

    Thread secondAssociate = new Thread(() -> {
      synchronized(inventory) {
        while (!inventory.isEmpty()) {
          try {
            inventory.reduceQunatityOnHand();
            System.out.println("Second Associate is getting rid of items");
            sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });

    firstAssociate.start();
    secondAssociate.start();
  }

  private static void sleep(int delay) {
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
    public void reduceQunatityOnHand() throws InterruptedException {
      inventory.remove();
    }
    public boolean isEmpty() {
      return inventory.isEmpty();
    }
    public Integer nextInventory() {
      return inventory.poll();
    }
  }
}
