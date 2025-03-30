# **All-docs-backend**

## Quick Start

```shell
docker network disconnect docker_default ad_redis
docker network disconnect docker_default ad_elasticsearch
docker network disconnect docker_default ad_kibana
docker network disconnect mongo-cluster mongo-primary
docker network disconnect mongo-cluster mongo-secondary1
docker network disconnect mongo-cluster mongo-secondary2
docker network disconnect mongo-cluster mongo-arbiter

docker network rm mongo-cluster
docker network rm docker_default

docker network create docker_default
docker network create mongo-cluster

docker network connect docker_default ad_redis
docker network connect docker_default ad_elasticsearch
docker network connect docker_default ad_kibana
docker network connect mongo-cluster mongo-primary
docker network connect mongo-cluster mongo-secondary1
docker network connect mongo-cluster mongo-secondary2
docker network connect mongo-cluster mongo-arbiter

docker restart ad_redis
docker restart ad_elasticsearch
docker restart ad_kibana
docker restart mongo-primary
docker restart mongo-secondary1
docker restart mongo-secondary2
docker restart mongo-arbiter
```

