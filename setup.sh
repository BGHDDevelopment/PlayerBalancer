#!/usr/bin/env bash

mkdir libs
cd libs

wget https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar
mvn install:install-file -Dfile=BungeeCord.jar -DgroupId=net.md-5 -DartifactId=bungeecord-proxy -Dversion=local -Dpackaging=jar