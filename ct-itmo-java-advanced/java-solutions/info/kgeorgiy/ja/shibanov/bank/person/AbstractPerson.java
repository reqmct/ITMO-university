package info.kgeorgiy.ja.shibanov.bank.person;

import java.rmi.RemoteException;

public abstract class AbstractPerson implements Person {
    private final String name;
    private final String surname;
    private final String passport;

    public AbstractPerson(String name, String surname, String passport) {
        this.name = name;
        this.surname = surname;
        this.passport = passport;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public String getSurname() throws RemoteException {
        return surname;
    }

    @Override
    public String getPassport() throws RemoteException {
        return passport;
    }
}
