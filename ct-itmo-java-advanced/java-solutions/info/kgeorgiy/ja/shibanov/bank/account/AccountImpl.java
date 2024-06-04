package info.kgeorgiy.ja.shibanov.bank.account;

public class AccountImpl implements Account {
    private final String id;
    private int amount;

    public AccountImpl(final String id) {
        this.id = id;
        amount = 0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized int getAmount() {
        return amount;
    }

    @Override
    public synchronized void setAmount(final int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount < 0");
        }
        this.amount = amount;
    }
}
