package info.kgeorgiy.ja.shibanov.bank.bank;

import info.kgeorgiy.ja.shibanov.bank.account.Account;
import info.kgeorgiy.ja.shibanov.bank.person.Person;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Interface representing a bank system
 */
public interface Bank extends Remote {
    /**
     * Creates a new account for a given passport and account ID.
     *
     * @param passport the passport number
     * @param id       the account ID
     * @return the newly created remote Account object
     * @throws RemoteException if an error occurs during operation
     */
    Account createAccount(String passport, String id) throws RemoteException;

    /**
     * Retrieves an account based on the passport and account ID.
     *
     * @param passport the passport number
     * @param id       the account ID
     * @return the Account object corresponding to the given parameters
     * @throws RemoteException if an error occurs during operation
     */
    Account getAccount(String passport, String id) throws RemoteException;

    /**
     * Retrieves all accounts associated with a given passport number.
     *
     * @param passport the passport number
     * @return a set of Account objects related to the given passport
     * @throws RemoteException if an error occurs during operation
     */
    Set<Account> getAccounts(String passport) throws RemoteException;

    /**
     * Retrieves a remote person profile based on the passport number.
     *
     * @param passport the passport number of the person
     * @return the Person object representing the remote person profile
     * @throws RemoteException if an error occurs during operation
     */
    Person getRemotePerson(String passport) throws RemoteException;

    /**
     * Retrieves a local person profile based on the passport number.
     *
     * @param passport the passport number of the person
     * @return the Person object representing the local person profile
     * @throws RemoteException if an error occurs during operation
     */
    Person getLocalPerson(String passport) throws RemoteException;

    /**
     * Creates a new person profile with the given name, surname, and passport number.
     *
     * @param name     the name of the person
     * @param surname  the surname of the person
     * @param passport the passport number of the person
     * @return the newly created Person object
     * @throws RemoteException if an error occurs during operation
     */
    Person createPerson(String name, String surname, String passport) throws RemoteException;
}
