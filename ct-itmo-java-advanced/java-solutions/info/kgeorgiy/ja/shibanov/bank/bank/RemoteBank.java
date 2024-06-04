package info.kgeorgiy.ja.shibanov.bank.bank;

import info.kgeorgiy.ja.shibanov.bank.account.Account;
import info.kgeorgiy.ja.shibanov.bank.account.LocalAccount;
import info.kgeorgiy.ja.shibanov.bank.account.RemoteAccount;
import info.kgeorgiy.ja.shibanov.bank.person.LocalPerson;
import info.kgeorgiy.ja.shibanov.bank.person.Person;
import info.kgeorgiy.ja.shibanov.bank.person.RemotePerson;
import info.kgeorgiy.ja.shibanov.bank.validation.Validator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class RemoteBank implements Bank {
    private final int port;
    private final ConcurrentMap<String, RemoteAccount> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RemotePerson> persons = new ConcurrentHashMap<>();

    public RemoteBank(final int port) {
        this.port = port;
    }


    private String getAccountId(final String passport, final String id) {
        return String.format("%s:%s", passport, id);
    }


    @Override
    public Account createAccount(final String passport, final String id) throws RemoteException {
        Validator.validate(passport, id);
        final RemoteAccount account = new RemoteAccount(id);
        if (accounts.putIfAbsent(getAccountId(passport, id), account) == null) {
            UnicastRemoteObject.exportObject(account, port);
            return account;
        } else {
            return accounts.get(getAccountId(passport, id));
        }
    }

    @Override
    public Account getAccount(final String passport, final String id) {
        Validator.validate(passport, id);
        return accounts.get(getAccountId(passport, id));
    }

    private Map<String, Account> getAccountsMap(final String passport) {
        return accounts.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(passport + ":"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<Account> getAccounts(final String passport) {
        Validator.validate(passport);
        return new HashSet<>(getAccountsMap(passport).values());
    }

    @Override
    public RemotePerson getRemotePerson(final String passport) {
        Validator.validate(passport);
        return persons.get(passport);
    }

    @Override
    public Person getLocalPerson(final String passport) throws RemoteException {
        final RemotePerson person = getRemotePerson(passport);
        final Map<String, Account> localAccounts = new HashMap<>();
        for (final var account : person.getAccounts()) {
            final Account localAccount = new LocalAccount(account);
            localAccounts.put(localAccount.getId(), localAccount);
        }
        return new LocalPerson(person, localAccounts);
    }


    @Override
    public Person createPerson(final String name, final String surname, final String passport) throws RemoteException {
        Validator.validate(name, surname, passport);
        final RemotePerson person = new RemotePerson(name, surname, passport, this);
        if (persons.putIfAbsent(passport, person) == null) {
            UnicastRemoteObject.exportObject(person, port);
            return person;
        } else {
            return persons.get(passport);
        }
    }
}
