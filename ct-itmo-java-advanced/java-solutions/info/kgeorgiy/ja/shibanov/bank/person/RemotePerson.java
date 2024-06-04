package info.kgeorgiy.ja.shibanov.bank.person;

import info.kgeorgiy.ja.shibanov.bank.account.Account;
import info.kgeorgiy.ja.shibanov.bank.bank.Bank;
import info.kgeorgiy.ja.shibanov.bank.validation.Validator;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Set;

public class RemotePerson extends AbstractPerson {

    private final Bank bank;

    public RemotePerson(String name, String surname, String passport, Bank bank) {
        super(name, surname, passport);
        this.bank = bank;
    }

    @Override
    public Account createAccount(String subId) throws RemoteException {
        Validator.validate(subId);
        return bank.createAccount(getPassport(), subId);
    }

    @Override
    public Account getAccount(String subId) throws RemoteException {
        Validator.validate(subId);
        return bank.getAccount(getPassport(), subId);
    }

    @Override
    public Set<Account> getAccounts() throws RemoteException {
        return Collections.unmodifiableSet(bank.getAccounts(getPassport()));
    }
}
