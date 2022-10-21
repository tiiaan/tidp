#!/bin/zsh

DIR=$(dirname $0)
for port in $(seq 6379 6384);
do
mkdir -p ${DIR}/cluster/node-${port}/conf
mkdir -p ${DIR}/cluster/node-${port}/data
touch ${DIR}/cluster/node-${port}/conf/redis.conf
cat << EOF > ${DIR}/cluster/node-${port}/conf/redis.conf
port ${port}
protected-mode no
daemonize yes
appendonly no
pidfile /var/run/redis_${port}.pid
dir ${DIR}/cluster/node-${port}/data/
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 15000
EOF
redis-server ${DIR}/cluster/node-${port}/conf/redis.conf
echo 'redis node '${port}' started'
done
redis-cli --cluster create 127.0.0.1:6379 127.0.0.1:6380 127.0.0.1:6381 127.0.0.1:6382 127.0.0.1:6383 127.0.0.1:6384 --cluster-replicas 1
echo 'cluster created'