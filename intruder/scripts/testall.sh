#!/bin/bash
for i in `seq 1 12`
do
sh ./scripts/test$i.sh
done
