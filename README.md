# tidp

### 特性
- [x] 实现共享 session
- [x] 查询缓存
- [x] 秒杀
- [x] 分布式锁
- [ ] List 点赞列表
- [ ] SortedSet 点赞排行榜
- [ ] Set 好友关注
- [ ] feed 流
- [ ] BitMap 数据统计
- [ ] GeoHash 附近商户
- [ ] HyperLogLog UV 统计


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
- [[1765299]](https://github.com/tiiaan/tidp/commit/1765299510f41e91de95298969b7dfc2d997bfc8) :tada: 商铺查询缓存双写一致性
- [[e05f21e]](https://github.com/tiiaan/tidp/commit/e05f21e434046e52f8daae2b2a2d9f8b5639aa81) :tada: 缓存空值解决缓存雪崩问题
- [[64465ab]](https://github.com/tiiaan/tidp/commit/64465abb0d873edf428fa193d648b135ddc633a9) :tada: 互斥锁解决缓存击穿问题
- [[e56b9bf]](https://github.com/tiiaan/tidp/commit/e56b9bf4c6c5bd63b3d3707730f5d260bac8c532) :tada: Redis 缓存工具类, 逻辑过期解决缓存击穿问题
- [[161f9f0]](https://github.com/tiiaan/tidp/commit/161f9f092f752001463803f559fb5d0869b35810) :bug: 分布式锁优化, 修复工具类bug
- [[730f9eb]](https://github.com/tiiaan/tidp/commit/730f9eb6fb9e353f443de9d839ba948f88662aa2) :tada: redis set 实现点赞和取消点赞
- [[fbee692]](https://github.com/tiiaan/tidp/commit/fbee692744d22b5877b1f0cdce77e1e5ce9544a8) :tada: redis zset 实现前5名点赞用户排序
- [[f74f358]](https://github.com/tiiaan/tidp/commit/f74f35864cc0cc83f5162597c985aa6868041329) :tada: redis set 关注取关和共同关注
