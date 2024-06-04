package info.kgeorgiy.ja.shibanov.bank.person;

import info.kgeorgiy.ja.shibanov.bank.account.Account;
import info.kgeorgiy.ja.shibanov.bank.account.LocalAccount;
import info.kgeorgiy.ja.shibanov.bank.validation.Validator;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

public class LocalPerson extends AbstractPerson implements Serializable {

    private final Map<String, Account> accounts;

    public LocalPerson(
            final String name,
            final String surname,
            final String passport,
            final Map<String, Account> accounts
    ) {
        super(name, surname, passport);
        this.accounts = accounts;
    }


    public LocalPerson(final Person person,
                       final Map<String, Account> accounts) throws RemoteException {
        super(person.getName(), person.getSurname(), person.getPassport());
        this.accounts = accounts;
    }

    @Override
    public Account createAccount(final String subId) throws RemoteException {
        Validator.validate(subId);
        return accounts.computeIfAbsent(subId, LocalAccount::new);
    }

    @Override
    public Account getAccount(final String subId) throws RemoteException {
        Validator.validate(subId);
        return accounts.get(subId);
    }

    @Override
    public Set<Account> getAccounts() throws RemoteException {
        return Set.copyOf(accounts.values());
    }
}
