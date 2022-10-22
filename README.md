# tidp

### 特性
- 实现共享 session
- 查询缓存
- 秒杀
- List 点赞列表
- SortedSet 点赞排行榜
- Set 好友关注
- feed 流
- BitMap 数据统计
- GeoHash 附近商户
- HyperLogLog UV 统计


### 启动

启动前端项目
```shell
nginx -c /nginx-1.18.0/conf/nginx.conf
```

脚本 [create_cluster_6.sh](https://github.com/tiiaan/tidp/blob/master/myredis/create_cluster_6.sh) 可以快速创建 Redis 6节点集群
```shell
cd myredis
sudo chmod +x *.sh
./create_cluster_6.sh
```


### 更新日志
- [[6a737dd]](https://github.com/tiiaan/tidp/commit/6a737dd5084eb5d013150a505cb761d11f1b2e4e) :tada: redis 共享 session 实现短信登录
- [[7910d1d]](https://github.com/tiiaan/tidp/commit/7910d1dac857c23ad373e67312b7fa04265e06bb) :tada: redis 集群脚本
- [[f4f55e2]](https://github.com/tiiaan/tidp/commit/f4f55e2bc9d431c93512ddcba10d238f2f9846fb) :tada: 拆分登陆校验拦截器和 token 有效期刷新拦截器
- [[1350ca4]](https://github.com/tiiaan/tidp/commit/1350ca4c000a50c800fb5e8892a1393a170ac288) :tada: 商铺查询缓存
- [[96ccdcf]](https://github.com/tiiaan/tidp/commit/96ccdcf100b1c971f00685e987770db533009e31) :tada: 商铺查询缓存双写一致性
