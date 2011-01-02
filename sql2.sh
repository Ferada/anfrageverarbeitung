#!/bin/sh

java -ea -cp .:lib/jopt-simple.jar main.Main sql2.txt $*
