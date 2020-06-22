#  项目介绍

预置链码包是我们为应用开发者提供对业务数据进行增删改查基本操作的链码。应用开发者可以在此链码包的基础上根据应用业务需求进一步拓展链码功能。此链码支持存储的数据类型有字符串、整型、浮点型、集合（map、list）等等。

## 链码包功能如下
### 1.增加数据（set）
输入参数说明
baseKey：需要保存的唯一的主键标识
baseValue：保存的数据信息
```base
例：{"baseKey":"str","baseValue":"this is string"}
```
其中baseKey是不能为空的字符串，baseValue可以是任意类型的数据。如果baseKey已经存在，则直接返回已经存在，不能添加；如果不存在，则添加数据。
### 2.修改数据（update）
输入参数说明
baseKey：需要修改的唯一的主键标识
baseValue：保存的数据信息
```base
例：{"baseKey":"str","baseValue":"this is string"} 
```
其中baseKey是不能为空的字符串，baseValue可以是任意类型的数据。如果baseKey不存在，则无法更新，如果已经存在，则修改数据。
### 3.删除数据（delete）
输入参数说明
baseKey：需要删除的唯一的主键标识的值
```base
例："str"
```
其中baseKey的值不能为空，且必须存在，否则将无法删除。
### 4.获取数据（get）
输入参数说明
baseKey：需要获取的唯一的主键标识的值
```base
例："str"
```
其中baseKey的值不能为空，且必须存在，否则将无法获取到相应的信息。

### 5.根据key获取历史数据（getHistory）
输入参数说明
baseKey：需要获取的唯一的主键标识的值
```base
例："str"
```
其中baseKey的值不能为空，响应结果：交易Id（txId）、交易时间（txTime）、是否删除（isDelete）、交易信息（dataInfo）。


## 链码目录介绍

* 链码信息

``` bash
bsnchaincode/
```

* 实体类

``` bash
models/
```

* 工具类

``` bash
utils/
```

* 测试类

``` bash
test/
```

* 索引路径

``` bash
META-INF/statedb/couchdb/indexes
```