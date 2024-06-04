package info.kgeorgiy.ja.shibanov.bank.person;

import info.kgeorgiy.ja.shibanov.bank.account.Account;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * The Person interface for remote access to a person's data.
 */
public interface Person extends Remote {
    /**
     * Retrieves the person's first name.
     * @return The first name of the person.
     * @throws RemoteException If there is an error during remote method invocation.
     */
    String getName() throws RemoteException;

    /**
     * Retrieves the person's surname.
     * @return The surname of the person.
     * @throws RemoteException If there is an error during remote method invocation.
     */
    String getSurname() throws RemoteException;

    /**
     * Retrieves the person's passport data.
     * @return The passport data of the person.
     * @throws RemoteException If there is an error during remote method invocation.
     */
    String getPassport() throws RemoteException;

    /**
     * Creates a new account for the person with a given sub-identifier.
     * @param subId The sub-identifier for the new account.
     * @return The newly created account.
     * @throws RemoteException If there is an error during remote method invocation.
     */
    Account createAccount(String subId) throws RemoteException;

    /**
     * Retrieves an account for the person with a given sub-identifier.
     * @param subId The sub-identifier for the account.
     * @return The account associated with the sub-identifier.
     * @throws RemoteException If there is an error during remote method invocation.
     */
    Account getAccount(String subId) throws RemoteException;

    /**
     * Retrieves a set of all accounts belonging to the person.
     * @return A set of all accounts.
     * @throws RemoteException If there is an error during remote method invocation.
     */
    Set<Account> getAccounts() throws RemoteException;
}
