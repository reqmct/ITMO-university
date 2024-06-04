#!/bin/bash

root_dir="../../java-advanced-2024"
solutions_dir="../java-solutions"
module_dir="${solutions_dir}/info/kgeorgiy/ja/shibanov"
implementor="${module_dir}/implementor/*.java"
kgeorgiy_impler="${root_dir}/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/*Impler*.java"

javadoc \
  -link "https://docs.oracle.com/en/java/javase/21/docs/api/" \
  -private \
  -d "../javadoc"\
  ${implementor} ${kgeorgiy_impler}
