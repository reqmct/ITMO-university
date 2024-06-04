package info.kgeorgiy.ja.shibanov.bank;

import info.kgeorgiy.ja.shibanov.bank.account.Account;
import info.kgeorgiy.ja.shibanov.bank.bank.Bank;
import info.kgeorgiy.ja.shibanov.bank.person.Person;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class PersonDemonstration {
    public static void main(String[] args) throws RemoteException {
        if (args == null || args.length != 5) {
            return;
        }

        final Bank bank;
        try {
            bank = (Bank) Naming.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        } catch (final MalformedURLException e) {
            System.out.println("Bank URL is invalid");
            return;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        try {
            String name = args[0];
            String surname = args[1];
            String passport = args[2];
            String subId = args[3];
            int amount = Integer.parseInt(args[4]);

            Person person = bank.getRemotePerson(passport);
            if (person == null) {
                person = bank.createPerson(name, surname, passport);
                System.out.println("Create person");
            }

            Account account = person.getAccount(subId);

            if (account == null) {
                person.createAccount(subId);
                System.out.println("Create balance");
            } else {
                account.setAmount(amount);
                System.out.println("Update balance: " + account.getAmount());
            }

        } catch (NumberFormatException e) {
            System.out.println("Incorrect argument");
        }
    }
}
