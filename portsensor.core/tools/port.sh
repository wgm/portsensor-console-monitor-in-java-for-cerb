#!/bin/bash
nc -z $1 $2; if [ $? == 0 ]; then echo "UP"; else echo "DOWN"; fi;
