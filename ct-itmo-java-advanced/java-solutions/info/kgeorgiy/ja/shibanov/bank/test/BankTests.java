package info.kgeorgiy.ja.shibanov.bank.test;


import info.kgeorgiy.ja.shibanov.bank.account.Account;
import info.kgeorgiy.ja.shibanov.bank.account.LocalAccount;
import info.kgeorgiy.ja.shibanov.bank.account.RemoteAccount;
import info.kgeorgiy.ja.shibanov.bank.bank.Bank;
import info.kgeorgiy.ja.shibanov.bank.bank.RemoteBank;
import info.kgeorgiy.ja.shibanov.bank.person.LocalPerson;
import info.kgeorgiy.ja.shibanov.bank.person.Person;
import info.kgeorgiy.ja.shibanov.bank.person.RemotePerson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.platform.launcher.EngineFilter.includeEngines;

public class BankTests {
    private static Bank bank;
    private final static String NAME = "Bobich";
    private final static String SURNAME = "Chepka";
    private final static String PASSPORT = "123456";
    private final static String EXAMPLE_ID = "1337";

    public static void main(final String[] args) {
        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(
                LauncherDiscoveryRequestBuilder.request()
                        .selectors(DiscoverySelectors.selectClass(BankTests.class))
                        .filters(includeEngines("junit-jupiter"))
                        .build());

        final TestExecutionSummary summary = listener.getSummary();

        summary.getFailures().forEach(failure -> System.err.println("Test failed: " + failure.getTestIdentifier().getDisplayName()));

        summary.printTo(new PrintWriter(System.out));

        System.exit(summary.getTestsFailedCount() > 0 ? 1 : 0);
    }

    @BeforeAll
    public static void beforeAll() {
        try {
            final Registry registry = LocateRegistry.createRegistry(7878);
            bank = new RemoteBank(5656);
            try {
                UnicastRemoteObject.exportObject(bank, 5656);
                registry.rebind("//localhost/bank", bank);
            } catch (final RemoteException e) {
                System.out.println("Cannot export object: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (final RemoteException e) {
            System.out.println("Cannot create registry: " + e.getMessage());
            e.printStackTrace();
        }

    }


    public void testAccount(final Account account) throws RemoteException {
        Assertions.assertEquals(EXAMPLE_ID, account.getId());
        Assertions.assertEquals(0, account.getAmount());
        account.setAmount(1337);
        Assertions.assertEquals(1337, account.getAmount());
        assertThrowsIllegalArgumentException(() -> account.setAmount(-1337));
    }

    @Test
    public void testLocalAccount() throws RemoteException {
        testAccount(new LocalAccount(EXAMPLE_ID));
    }

    @Test
    public void testRemoteAccount() throws RemoteException {
        testAccount(new RemoteAccount(EXAMPLE_ID));
    }

    public void testPerson(final Person person) throws RemoteException {
        Assertions.assertEquals(NAME, person.getName());
        Assertions.assertEquals(SURNAME, person.getSurname());
        Assertions.assertEquals(PASSPORT, person.getPassport());
    }

    @Test
    public void testLocalPerson() throws RemoteException {
        testPerson(new LocalPerson(NAME, SURNAME, PASSPORT, null));
    }

    @Test
    public void testRemotePerson() throws RemoteException {
        testPerson(new RemotePerson(NAME, SURNAME, PASSPORT, null));
    }

    @Test
    public void testCreatePerson() throws RemoteException {
        assertThrowsIllegalArgumentException(() -> bank.createPerson(null, null, null));
        assertThrowsIllegalArgumentException(() -> bank.createPerson("    ", null, ""));
        final Person person = bank.createPerson(NAME, SURNAME, PASSPORT);
        Assertions.assertEquals(NAME, person.getName());
        Assertions.assertEquals(SURNAME, person.getSurname());
        Assertions.assertEquals(PASSPORT, person.getPassport());
        Assertions.assertEquals(bank.createPerson(NAME, SURNAME, PASSPORT), person);
    }

    @Test
    public void testGetRemotePerson() throws RemoteException {
        assertThrowsIllegalArgumentException(() -> bank.getRemotePerson(null));
        Assertions.assertNull(bank.getRemotePerson(EXAMPLE_ID));
        final Person person = bank.createPerson(NAME, SURNAME, PASSPORT);
        Assertions.assertSame(bank.getRemotePerson(PASSPORT), person);
    }

    @Test
    public void testGetLocalPerson() throws RemoteException {
        assertThrowsIllegalArgumentException(() -> bank.getLocalPerson(null));
        Assertions.assertNull(bank.getLocalPerson(EXAMPLE_ID));
        final Person person = bank.getLocalPerson(PASSPORT);
        Assertions.assertNotNull(person);
        Assertions.assertNotSame(person, bank.getRemotePerson(PASSPORT));
        Assertions.assertEquals(NAME, person.getName());
        Assertions.assertEquals(SURNAME, person.getSurname());
        Assertions.assertEquals(PASSPORT, person.getPassport());
    }


    @Test
    public void testCreateAccount() throws RemoteException {
        assertThrowsIllegalArgumentException(() -> bank.createAccount(null, null));
        assertThrowsIllegalArgumentException(() -> bank.createAccount("", ""));

        final Account account = bank.createAccount(PASSPORT, "1_bank");
        Assertions.assertEquals("1_bank", account.getId());
        Assertions.assertEquals(0, account.getAmount());
        Assertions.assertEquals(account, bank.createAccount(PASSPORT, "1_bank"));
        Assertions.assertNotEquals(account, bank.createAccount(PASSPORT, "2_bank"));
    }

    @Test
    public void testGetAccount() throws RemoteException {
        assertThrowsIllegalArgumentException(() -> bank.getAccount(null, null));
        assertThrowsIllegalArgumentException(() -> bank.getAccount("", ""));
        Assertions.assertNull(bank.getAccount(EXAMPLE_ID, EXAMPLE_ID));
        assertThrowsIllegalArgumentException(() -> bank.getAccount(PASSPORT, ""));
        assertThrowsIllegalArgumentException(() -> bank.getAccount(PASSPORT, null));
        Assertions.assertNotNull(bank.getAccount(PASSPORT, "1_bank"));
        Assertions.assertNotNull(bank.getAccount(PASSPORT, "2_bank"));

        final Account account = bank.createAccount(PASSPORT, "1_bank");
        Assertions.assertEquals(bank.getAccount(PASSPORT, "1_bank"), account);
    }

    private static void assertThrowsIllegalArgumentException(final Executable executable) {
        Assertions.assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    public void testGetAccounts() throws RemoteException {
        assertThrowsIllegalArgumentException(() -> bank.getAccounts(null));
        assertThrowsIllegalArgumentException(() -> bank.getAccounts(""));
        Assertions.assertNotNull(bank.getAccounts(EXAMPLE_ID));
        Assertions.assertTrue(bank.getAccounts(EXAMPLE_ID).isEmpty());

        final var accounts = bank.getAccounts(PASSPORT);

        Assertions.assertTrue(accounts.contains(bank.getAccount(PASSPORT, "1_bank")));
        Assertions.assertTrue(accounts.contains(bank.getAccount(PASSPORT, "2_bank")));
    }


    private void testPersonAccounts(final Person person) throws RemoteException {
        assertThrowsIllegalArgumentException(() -> person.getAccount(null));
        assertThrowsIllegalArgumentException(() -> person.getAccount(""));
        Assertions.assertNull(person.getAccount(EXAMPLE_ID));
        Assertions.assertNotNull(person.getAccount("1_bank"));
        Assertions.assertNotNull(person.getAccount("2_bank"));
        Assertions.assertTrue(person.getAccounts().contains(person.getAccount("1_bank")));
        Assertions.assertTrue(person.getAccounts().contains(person.getAccount("2_bank")));
    }

    @Test
    public void testLocalPersonAccounts() throws RemoteException {
        final Person person = bank.getLocalPerson(PASSPORT);
        testPersonAccounts(person);

        final Account account = person.createAccount("1_local");
        Assertions.assertNull(bank.getAccount(PASSPORT, "1_local"));
        Assertions.assertEquals(account, person.getAccount("1_local"));

        bank.createAccount(PASSPORT, "test_local");
        Assertions.assertNull(person.getAccount("test_local"));

        final Person newPerson = bank.getLocalPerson(PASSPORT);
        Assertions.assertNotNull(newPerson.getAccount("test_local"));
    }


    @Test
    public void testRemotePersonAccounts() throws RemoteException {
        final Person person = bank.getRemotePerson(PASSPORT);
        testPersonAccounts(person);

        final Account account = person.createAccount("1_remote");
        Assertions.assertNotNull(bank.getAccount(PASSPORT, "1_remote"));
        Assertions.assertEquals(account, person.getAccount("1_remote"));

        final Account bankAccount = bank.createAccount(PASSPORT, "test_remote");
        Assertions.assertNotNull(person.getAccount("test_remote"));
        bankAccount.setAmount(100);

        Assertions.assertEquals(100, person.getAccount("test_remote").getAmount());
    }

    @Test
    public void testMultiThreadCreatePerson() throws InterruptedException, RemoteException, ExecutionException {
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final String ind = Integer.toString(i);
            final Future<?> future = executorService.submit(() -> {
                final Person person = bank.createPerson("name" + ind, "surname" + ind, "passport" + ind);
                Assertions.assertNotNull(person);
                return person;
            });
            futures.add(future);
        }

        executorService.close();

        for (final Future<?> future : futures) {
            final Person person = Assertions.assertInstanceOf(Person.class, future.get(), "Incorrect object");
            Assertions.assertEquals(person, bank.getRemotePerson(person.getPassport()));
        }
    }

    @Test
    public void testMultiThreadCreateAccount() throws InterruptedException, RemoteException {
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final String ind = Integer.toString(i);
            final Future<?> future = executorService.submit(() ->
                    Objects.requireNonNull(bank.createAccount(PASSPORT, "account" + ind)));
            futures.add(future);
        }

        executorService.close();

        final List<Account> accounts = new ArrayList<>();
        for (final Future<?> future : futures) {
            try {
                final Object object = future.get();
                if (object instanceof Account) {
                    accounts.add((Account) object);
                } else {
                    Assertions.fail("Incorrect object");
                }
            } catch (final ExecutionException e) {
                Assertions.fail(e);
            }
        }
        for (final var account : accounts) {
            Assertions.assertEquals(account, bank.getAccount(PASSPORT, account.getId()));
        }
    }


    @Test
    public void testMultiThreadRemotePerson() throws InterruptedException, RemoteException {
        final Person person = bank.getRemotePerson(PASSPORT);
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final String ind = Integer.toString(i);
            final Future<?> future = executorService.submit(() -> {
                final Account account = person.createAccount("account" + ind);
                Assertions.assertNotNull(account);
                return account;
            });
            futures.add(future);
        }

        executorService.close();

        final List<Account> accounts = new ArrayList<>();
        for (final Future<?> future : futures) {
            try {
                final Object object = future.get();
                if (object instanceof Account) {
                    accounts.add((Account) object);
                } else {
                    Assertions.fail("Incorrect object");
                }
            } catch (final ExecutionException e) {
                Assertions.fail(e);
            }
        }

        for(final var account : accounts) {
            Assertions.assertEquals(account, person.getAccount(account.getId()));
        }

        Assertions.assertTrue(person.getAccounts().size() >= 100);
    }

}
