module info.kgeorgiy.ja.shibanov {
    requires info.kgeorgiy.java.advanced.implementor;
    requires info.kgeorgiy.java.advanced.student;
    requires info.kgeorgiy.java.advanced.iterative;
    requires info.kgeorgiy.java.advanced.mapper;
    requires info.kgeorgiy.java.advanced.crawler;
    requires info.kgeorgiy.java.advanced.hello;
    requires java.compiler;
    exports info.kgeorgiy.ja.shibanov.implementor;
    exports info.kgeorgiy.ja.shibanov.crawler;
    exports info.kgeorgiy.ja.shibanov.hello;

    exports info.kgeorgiy.ja.shibanov.bank;
    exports info.kgeorgiy.ja.shibanov.bank.person;
    exports info.kgeorgiy.ja.shibanov.bank.account;
    exports info.kgeorgiy.ja.shibanov.bank.bank;
    exports info.kgeorgiy.ja.shibanov.bank.test;

    requires java.rmi;
    requires jdk.httpserver;
    requires org.junit.jupiter.api;
    requires org.junit.platform.launcher;
}