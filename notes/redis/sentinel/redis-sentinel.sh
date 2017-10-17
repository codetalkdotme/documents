#!/bin/bash

nohup redis-sentinel /etc/redis/sentinel1.conf  > /var/log/sentinel1.log 2>&1 &
nohup redis-sentinel /etc/redis/sentinel2.conf  > /var/log/sentinel2.log 2>&1 &




