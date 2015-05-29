#!/usr/bin/env bash
java -jar build/da.jar 0 >& logs/machine1.log
java -jar build/da.jar 1 >& logs/machine2.log
java -jar build/da.jar 2 >& logs/machine3.log