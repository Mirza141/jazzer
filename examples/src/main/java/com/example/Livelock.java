package com.example;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Livelock
{

    static final BankAccount studentAccount = new BankAccount(101, 50000);
    static final BankAccount universityAccount = new BankAccount(102, 100000);
    public static void main(String[] args) {
        test();
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data)
    {

    }

    public static void test()
    {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (studentAccount.transaction(studentAccount, universityAccount, 4000)) {

                        }
                    } catch (InterruptedException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t2 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (universityAccount.transaction(universityAccount, studentAccount, 2000)) {

                        }
                    } catch (InterruptedException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }
        };

        t1.start();
        t2.start();
    }
}


class BankAccount {
    private double balance;
    private int accountNumber;
    final Lock lock = new ReentrantLock();

    public BankAccount(int AccountNumber, double InitialBalance) {
        setBalance(InitialBalance);
        setAccountNumber(AccountNumber);
    }

    public boolean deposit(double amount) throws InterruptedException {
        if (this.lock.tryLock()) {
            Thread.sleep(1000);
            balance = balance + amount;
            return true;
        } else {
            return false;
        }
    }

    public boolean withdraw(double amount) throws InterruptedException {
        if (this.lock.tryLock()) {
            Thread.sleep(1000);
            balance = balance - amount;
            return true;
        } else {
            return false;
        }

    }

    public boolean transaction(BankAccount from, BankAccount to, double amount) throws InterruptedException {
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

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
