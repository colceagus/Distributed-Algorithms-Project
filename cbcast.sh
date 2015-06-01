#!/bin/bash
gnome-terminal --tab -e "java -jar build/da.jar 0 cbcast > logs/machine1.log" --tab -e "java -jar build/da.jar 1 cbcast > logs/machine2.log" --tab -e "java -jar build/da.jar 2 cbcast > logs/machine3.log"
