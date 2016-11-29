## 单机版运行DynamoDB

首先给出一个下载链接：[DynamoDB](http://dynamodb-local.s3-website-us-west-2.amazonaws.com/dynamodb_local_latest.tar.gz)

解压 tar 包，使用 shell 启动本地服务：
<pre>
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
</pre>
在 pom.xml 中添加依赖：

```
<dependency>
  <groupId>com.amazonaws</groupId>
  <artifactId>aws-java-sdk-dynamodb</artifactId>
  <version>1.10.72</version>
</dependency>
```
在进行下面的各种操作之前，先创建一个客户端连接：

```
BasicAWSCredentials awsCreds = new BasicAWSCredentials("local_db", "local_db");
AmazonDynamoDBClient client = new AmazonDynamoDBClient(awsCreds);
client.setEndpoint("http://localhost:8000");
dynamoDB = new DynamoDB(client);
```
### 表操作
AWS 文档对应链接：[WorkingWithTables](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html)
### 数据操作
AWS 文档对应链接：[WorkingWithItems](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithItems.html)