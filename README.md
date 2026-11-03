# damai-learning

一个从零开始、逐步演进的 Java 票务系统学习项目。

本项目不会复制大麦或大麦 Pro 的业务实现，而是从最小可运行的 Spring Boot 单体应用开始，依次加入工程化基础、MySQL、Redis、消息队列、Elasticsearch 和微服务能力。

## 当前阶段：V1

当前只包含：

- Java 17
- Maven
- Spring Boot Web
- 统一接口响应
- 统一错误码
- 业务异常与全局异常处理
- 请求参数校验
- 基础自动化测试

启动项目后访问：

```text
GET http://localhost:8080/api/hello
```

预期响应：

```json
{
  "code": 0,
  "message": "success",
  "data": "damai-learning is running"
}
```

V1的文件结构和请求流程说明见[学习笔记](docs/V1-工程化基础.md)。

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

运行测试：

```bash
mvn test
```

启动应用：

```bash
mvn spring-boot:run
```
