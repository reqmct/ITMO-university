#!/bin/bash

javac -cp "../lib/*" \
../java-solutions/info/kgeorgiy/ja/shibanov/bank/*.java \
../java-solutions/info/kgeorgiy/ja/shibanov/bank/bank/*.java \
../java-solutions/info/kgeorgiy/ja/shibanov/bank/test/*.java \
../java-solutions/info/kgeorgiy/ja/shibanov/bank/person/*.java \
../java-solutions/info/kgeorgiy/ja/shibanov/bank/validation/*.java \
../java-solutions/info/kgeorgiy/ja/shibanov/bank/account/*.java \
-d "classes"
java -cp "classes:../lib/*" "info/kgeorgiy/ja/shibanov/bank/test/BankTests"
echo "exit code:" $?
exit $?