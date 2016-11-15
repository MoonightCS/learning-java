package ru.popov.bodya.terminal2;

public class Account {

    private long balance;

    public Account(long balance) {
        this.balance = balance;
    }

    public Account() {
        this(0L);
    }


    public long getBalance() {
        return balance;
    }

    public synchronized void deposit(long amount) {
        checkAmountNonNegative(amount);

        balance += amount;
        this.notifyAll();

    }

    public synchronized void withdraw(long amount) {
        checkAmountNonNegative(amount);

        if (balance < amount) {
            throw new IllegalArgumentException("not enough money");
        }
        balance -= amount;

    }
    public synchronized void waitAndWithdraw(long amount) throws Exception {
        checkAmountNonNegative(amount);
        while(balance < amount) {
            wait();
            System.out.println("Wakeup " + balance);
        }
        balance -= amount;
    }

    private static void checkAmountNonNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("negative amount");
        }
    }
}
