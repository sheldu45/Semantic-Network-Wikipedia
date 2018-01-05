#!/bin/bash

sleep "$1s"
for i in *; do 
    head -n 10000 $i > "head$i"
done
