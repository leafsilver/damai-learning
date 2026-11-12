# damai-learning

一个从零开始、逐步演进的 Java 票务系统学习项目。

本项目不会复制大麦或大麦 Pro 的业务实现，而是从最小可运行的 Spring Boot 单体应用开始，依次加入工程化基础、MySQL、Redis、消息队列、Elasticsearch 和微服务能力。

学习过程、常用注解速查与每个阶段的理解记录见[学习笔记](docs/学习笔记.md)。

## 当前阶段：核心票务链路与搜索

项目目前已经包含：

- 统一响应、错误码、全局异常和参数校验
- MySQL、MyBatis-Plus和分页查询
- 节目、场次、票档、订单和库存扣减
- 订单超时取消、支付消息Outbox与消费幂等
- 电子票生成、查询和验票
- Redis节目详情缓存、空值缓存和故障降级
- Kafka消息发送、消费及补偿任务
- Elasticsearch中文分词、多字段搜索、权重、分页和高亮
- MySQL节目数据全量重建及Kafka增量同步ES

项目仍按学习路线继续演进，不代表已完成生产环境所需的全部安全、监控和高可用能力。

## 学习路线

1. V0：创建并启动最小 Spring Boot 项目
2. V1：统一返回、错误码、全局异常、参数校验
3. V2：MySQL、MyBatis-Plus和票务核心表
4. V3：用户注册、登录和认证
5. V4：节目、场次、票档和座位
6. V5：订单、事务和库存扣减
7. V6：Redis缓存、库存和分布式锁
8. V7：消息队列和订单超时取消
9. V8：Elasticsearch节目搜索
10. V9：微服务拆分与部署

## 运行要求

- JDK 17
- Maven 3.6.3 或更高版本
- MySQL 8
- Docker Desktop

Docker Compose管理以下本地中间件：

```text
Redis：localhost:6379
Kafka：localhost:9092
Kafka UI：http://localhost:8081
Elasticsearch：http://localhost:9200
Kibana：http://localhost:5601
```

启动中间件：

```bash
docker compose up -d
```

按照`sql`目录中的脚本初始化MySQL，并通过环境变量或不提交Git的`application-local.yml`提供本地数据库密码。不要把真实密码写入仓库。

启动应用后，可访问：

```text
健康检查：http://localhost:8080/api/hello
Swagger UI：http://localhost:8080/swagger-ui.html
OpenAPI文档：http://localhost:8080/v3/api-docs
```

运行测试：

```bash
mvn test
```

启动应用：

```bash
mvn spring-boot:run
```
