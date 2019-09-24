# 一、微服务 houserush-customer-manage

管理客户，开立客户的账号，管理客户的基本信息， 录入客户的选房资格。

## 1.1 主要功能

客户账号信息维护;创建客户信息同时创建用户信息，姓名username默认是real_name姓名.默认密码是'123456'

客户选房资格信息维护

## 1.2 数据库设计

### 1）客户账户信息（customers）

| id         | int            | 主键id         |
| ---------- | -------------- | -------------- |
| name       | varchar（32）  | 客户姓名       |
| phone      | varchar（32）  | 手机号         |
| real_name  | varchar（32）  | 客户登陆用户名 |
| id_card    | varchar（32）  | 身份证证号     |
| address    | varchar（255） | 住址           |
| deleted_at | timestamp      | 删除时间       |
| created_at | timestamp      | 创建时间       |
| update_at  | timestamp      | 更新时间       |

### 2）选房资格信息（qualifications）

| id          | int       | 主键id   |
| :---------- | --------- | -------- |
| customer_id | int       | 客户id   |
| sale_id     | int       | 售卖Id   |
| created_at  | timestamp | 创建时间 |
| update_at   | timestamp | 更新时间 |

## 1.3 接口设计

```
/**
**创建客户账户信息
*/
Customer createCustomer(Customer customer);

/**
**查询客户账户信息
*/
Customer findCustomer(int id);

/**
**更新客户账户信息
*/
Customer updateCustomer(int id, Customer customer);

/**
**删除客户账户信息
*/
void removeCustomer(int id);

/**
**查询所有客户账户信息
*/
List<Customer> indexCustomers();

/**
**更新客户选房资质信息
*/
Customer updateCustomerQualifications(int id, List<Qualification> qualifications);

/**
**创建客户账户信息
*/
int getQualificationsCount(int customerId, int saleId);
```
