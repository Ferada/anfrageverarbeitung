#!/bin/sh

java -ea -cp .:lib/log4j.jar:lib/jopt-simple.jar main.Main $*
