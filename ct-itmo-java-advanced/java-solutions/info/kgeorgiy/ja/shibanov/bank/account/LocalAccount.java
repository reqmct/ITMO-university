package info.kgeorgiy.ja.shibanov.bank.account;

import java.io.Serializable;
import java.rmi.RemoteException;

public class LocalAccount extends AccountImpl implements Serializable {
    public LocalAccount(final String id) {
        super(id);
    }

    public LocalAccount(final Account account) throws RemoteException {
        super(account.getId());
        setAmount(account.getAmount());
    }
}
