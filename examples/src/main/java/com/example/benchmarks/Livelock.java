package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Livelock {

    public static void main(String[] args) {
        test(new BankAccount(101, 50000),new BankAccount(102, 100000));
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        test(new BankAccount(data.consumeInt(), data.consumeInt()),new BankAccount(data.consumeInt(), data.consumeInt()));
    }

    public static void test(final BankAccount studentAccount, final BankAccount universityAccount) {
        Thread t1 = new Thread() {
            //int i=0, max=10;
            @Override
            public void run() {
                //do {
                    studentAccount.transaction(studentAccount, universityAccount, 4000);
                  //  i++;
                //}while(i<max);
            }
        };

        Thread t2 = new Thread() {
            //int i=0,max=10;
            @Override
            public void run() {
                //do {
                    universityAccount.transaction(universityAccount, studentAccount, 2000);
                  //  i++;
                //}while(i<max);
            }
        };
        t1.start();
        t2.start();
    }
    private static void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    static class BankAccount {
        final Lock lock = new ReentrantLock();
        private double balance;
        private int accountNumber;

        public BankAccount(int AccountNumber, double InitialBalance) {
            setBalance(InitialBalance);
            setAccountNumber(AccountNumber);
        }

        public boolean deposit(double amount) {
            if (this.lock.tryLock()) {
                delay(1000);
                balance = balance + amount;
                return true;
            } else {
                return false;
            }
        }

        public boolean withdraw(double amount)  {
            if (this.lock.tryLock()) {
                delay(1000);
                balance = balance - amount;
                return true;
            } else {
                return false;
            }
        }

        public boolean transaction(BankAccount from, BankAccount to, double amount) {
            if (from.withdraw(amount)) {
                System.out.println("Withdrawing " + amount + " from " + accountNumber);
                if (from.deposit(amount)) {
                    System.out.println("Depositing " + amount + " to " + accountNumber);
                    return true;
                } else {
                    from.deposit(amount);
                    System.out.println("Refunding amount: " + amount + " to account: " + from.accountNumber);
                }
            }
            return false;
        }

        public void setAccountNumber(int accountNumber) {
            this.accountNumber = accountNumber;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }
    }
}