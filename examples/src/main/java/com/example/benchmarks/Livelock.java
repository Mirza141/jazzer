package com.example.benchmarks;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Livelock {

    public static void main(String[] args) {
        test(new BankAccount(101, 50000),new BankAccount(102, 100000));
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        test(new BankAccount(data.consumeInteger(), data.consumeInteger()),new BankAccount(data.consumeInteger(), data.consumeInteger()));
    }

    public static void test(final BankAccount studentAccount, final BankAccount universityAccount) {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        studentAccount.transaction(studentAccount, universityAccount, 4000);
                    } catch (InterruptedException e) {
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
                        universityAccount.transaction(universityAccount, studentAccount, 2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();
        t2.start();
    }
    static class BankAccount {
        final Lock lock = new ReentrantLock();
        private double balance;
        private int accountNumber;

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

        public void setAccountNumber(int accountNumber) {
            this.accountNumber = accountNumber;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }
    }
}