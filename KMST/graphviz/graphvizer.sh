#!/bin/bash

read vertices
read edges
read k
read threshold

echo graph G {

while read edge
do
	if [ "$edge" != "" ]
	then
		echo $edge | awk '{split($0, a, " "); print a[2], " -- ", a[3], " [weight=", a[4], ", label = ", a[4], "]";}'
	fi
done

echo }
