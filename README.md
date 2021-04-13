# mall
  新电商后台后端
  
    #### 后台管理系统
  
  前端项目`mall-admin-web`地址：http://192.168.1.98/cerong_dev/mall-admin-web
   
  ### 组织结构
  
  ``` lua
  mall
  ├── mall-common -- 工具类及通用代码
  ├── mall-mbg -- MyBatisGenerator生成的数据库操作代码
  ├── mall-security -- SpringSecurity封装公用模块
  ├── mall-admin -- 后台商城管理系统接口
  ├── mall-search -- 基于Elasticsearch的商品搜索系统
  └── mall-portal -- 前台商城系统接口
  ```
  
  ### 技术选型
  
  #### 后端技术
  
  | 技术                 | 说明                | 官网                                           |
  | -------------------- | ------------------- | ---------------------------------------------- |
  | SpringBoot           | 容器+MVC框架        | https://spring.io/projects/spring-boot         |
  | SpringSecurity       | 认证和授权框架      | https://spring.io/projects/spring-security     |
  | MyBatis              | ORM框架             | http://www.mybatis.org/mybatis-3/zh/index.html |
  | MyBatisGenerator     | 数据层代码生成      | http://www.mybatis.org/generator/index.html    |
  | Elasticsearch        | 搜索引擎            | https://github.com/elastic/elasticsearch       |
  | RabbitMQ             | 消息队列            | https://www.rabbitmq.com/                      |
  | Redis                | 分布式缓存          | https://redis.io/                              |
  | MongoDB              | NoSql数据库         | https://www.mongodb.com                        |
  | LogStash             | 日志收集工具        | https://github.com/elastic/logstash            |
  | Kibina               | 日志可视化查看工具  | https://github.com/elastic/kibana              |
  | Nginx                | 静态资源服务器      | https://www.nginx.com/                         |
  | Docker               | 应用容器引擎        | https://www.docker.com                         |
  | Jenkins              | 自动化部署工具      | https://github.com/jenkinsci/jenkins           |
  | Druid                | 数据库连接池        | https://github.com/alibaba/druid               |
  | OSS                  | 对象存储            | https://github.com/aliyun/aliyun-oss-java-sdk  |
  | MinIO                | 对象存储            | https://github.com/minio/minio                 |
  | JWT                  | JWT登录支持         | https://github.com/jwtk/jjwt                   |
  | Lombok               | 简化对象封装工具    | https://github.com/rzwitserloot/lombok         |
  | Hutool               | Java工具类库        | https://github.com/looly/hutool                |
  | PageHelper           | MyBatis物理分页插件 | http://git.oschina.net/free/Mybatis_PageHelper |
  | Swagger-UI           | 文档生成工具        | https://github.com/swagger-api/swagger-ui      |
  | Hibernator-Validator | 验证框架            | http://hibernate.org/validator                 |
  
    
  ## 环境搭建
  
  - 克隆源代码到本地，使用IDEA打开，并完成编译
  - 只需启动mall-admin即可

  ## 项目演示
    `mall-admin-web`地址：http://192.168.1.31:808/#/login

  