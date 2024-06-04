#!/bin/bash

root_dir="../../java-advanced-2024"
lib_dir="${root_dir}/lib"
artifacts_dir="${root_dir}/artifacts"
solutions_dir="../java-solutions"
module_info="${solutions_dir}/module-info.java"
module_dir="${solutions_dir}/info/kgeorgiy/ja/shibanov"
implementor="${module_dir}/implementor/*.java"
classes_dir="classes"

javac --module-path "${lib_dir}:${artifacts_dir}" ${module_info} ${implementor} -d ${classes_dir}
jar cfm implementor.jar MANIFEST.MF -C ${classes_dir} .
