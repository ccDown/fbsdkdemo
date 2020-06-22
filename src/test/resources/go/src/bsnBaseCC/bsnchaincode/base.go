package bsnchaincode

import (
	"bsnBaseCC/models"
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"time"
)

// 定义全局常量
const (
	// 定义key值前缀
	key_prefix = "base_key_"
)

// 定义key值的生成规则
func constructKey(baseKey string) string {
	return key_prefix + baseKey
}

// 保存数据
func Set(stubInterface shim.ChaincodeStubInterface, strings []string) peer.Response {
	SetLogger("保存数据开始......")
	defer SetLogger("保存数据结束......")
	if len(strings) != 1 {
		return shim.Error("参数信息不正确")
	}
	//校验数据 1、键值不为空
	var dtoModel models.DTOBaseModel
	err := json.Unmarshal([]byte(strings[0]), &dtoModel)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据转换失败:%s", err.Error()))
	}
	err = DataCheck(dtoModel.BaseKey)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据格式错误:%s", err.Error()))
	}

	mainKey := constructKey(dtoModel.BaseKey)
	SetLogger("查询key是否存在", mainKey)

	//校验数据 2、数据不存在
	result, err := stubInterface.GetState(mainKey)
	if err != nil {
		return shim.Error(fmt.Sprintf("获取主键失败:%s", err.Error()))
	}
	if len(result) > 0 {
		SetLogger(fmt.Sprintf("该【%s】信息已经存在", mainKey))
		return shim.Error(fmt.Sprintf("该键值信息已存在"))
	}

	SetLogger(fmt.Sprintf("开始新增数据【%s】", mainKey))

	//保存数据到数据库
	dbBaseModel := models.DTOBase2Db(dtoModel)
	reqJsonValue, err := json.Marshal(&dbBaseModel)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据转换失败:%s", err.Error()))
	}

	err = stubInterface.PutState(mainKey, reqJsonValue)
	if err != nil {
		return shim.Error(fmt.Sprintf("添加数据失败:%s", err.Error()))
	}

	SetLogger("保存数据结束", mainKey)

	return shim.Success([]byte("SUCCESS"))
}

// 修改数据
func Update(stubInterface shim.ChaincodeStubInterface, strings []string) peer.Response {
	SetLogger("修改数据开始......")
	defer SetLogger("修改数据结束......")
	if len(strings) != 1 {
		return shim.Error("参数信息不正确")
	}

	//校验数据 1、键值不为空
	var dtoModel models.DTOBaseModel
	err := json.Unmarshal([]byte(strings[0]), &dtoModel)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据转换失败:%s", err.Error()))
	}
	err = DataCheck(dtoModel.BaseKey)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据格式错误:%s", err.Error()))
	}

	mainKey := constructKey(dtoModel.BaseKey)

	//校验数据 2、键值存在
	result, err := stubInterface.GetState(mainKey)
	SetLogger(fmt.Sprintf("查找键值为: 【%s】 的数据", mainKey))

	if err != nil {
		return shim.Error(fmt.Sprintf("获取主键失败:%s", err.Error()))
	}
	if len(result) == 0 {
		return shim.Error(fmt.Sprintf("该键值信息不存在"))
	}

	SetLogger(fmt.Sprintf("查找键值为: 【%s】 的数据成功", mainKey))

	//更改数据到数据库
	dbBaseModel := models.DTOBase2Db(dtoModel)
	reqJsonValue, err := json.Marshal(&dbBaseModel)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据转换失败:%s", err.Error()))
	}

	err = stubInterface.PutState(mainKey, reqJsonValue)
	if err != nil {
		return shim.Error(fmt.Sprintf("更新数据失败:%s", err.Error()))
	}
	SetLogger(fmt.Sprintf("键值为: 【%s】 的数据，更新成功", mainKey))

	SetLogger("修改数据结束")

	return shim.Success([]byte("SUCCESS"))
}

// 删除数据
func Delete(stubInterface shim.ChaincodeStubInterface, strings []string) peer.Response {
	SetLogger("删除数据开始......")
	defer SetLogger("删除数据结束......")
	if len(strings) != 1 {
		return shim.Error("参数信息不正确")
	}

	//校验数据 1、键值不为空
	key := strings[0]
	err := DataCheck(key)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据格式错误:%s", err.Error()))
	}

	mainKey := constructKey(key)

	SetLogger(fmt.Sprintf("查找键值为:【%s】 的数据", mainKey))

	//校验数据 2、键值存在
	result, err := stubInterface.GetState(mainKey)
	if err != nil {
		return shim.Error(fmt.Sprintf("获取主键失败:%s", err.Error()))
	}
	if len(result) == 0 {
		return shim.Error(fmt.Sprintf("该键值信息不存在"))
	}

	//到数据库删除
	err = stubInterface.DelState(mainKey)
	if err != nil {
		return shim.Error(fmt.Sprintf("删除数据失败:%s", err.Error()))
	}
	SetLogger(fmt.Sprintf("键值为: 【%s】 的数据，删除成功", mainKey))

	SetLogger("删除数据结束")

	return shim.Success([]byte("SUCCESS"))
}

// 查询数据
func Get(stubInterface shim.ChaincodeStubInterface, strings []string) peer.Response {
	SetLogger("获取数据开始......")
	defer SetLogger("获取数据结束......")
	if len(strings) != 1 {
		return shim.Error("参数信息不正确")
	}
	//校验数据 1、键值不为空
	key := strings[0]
	err := DataCheck(key)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据格式错误:%s", err.Error()))
	}

	mainKey := constructKey(key)
	SetLogger(fmt.Sprintf("查找键值为:【%s】 的数据", mainKey))

	//校验数据 2、键值存在
	result, err := stubInterface.GetState(mainKey)
	if err != nil {
		return shim.Error(fmt.Sprintf("获取主键失败:%s", err.Error()))
	}
	if len(result) == 0 {
		SetLogger(fmt.Sprintf("key值为【%s】的数据信息不存在", mainKey))
		return shim.Error("信息不存在")
	}

	var dbBaseModel models.DBBaseModel
	err = json.Unmarshal(result, &dbBaseModel)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据格式错误:%s", err.Error()))
	}

	//数据库数据转换为需要返回的数据
	dtoModel := models.Db2DTOBase(dbBaseModel)
	SetLogger(fmt.Sprintf("key值为【%s】的数据信息查询完成", mainKey))

	SetLogger("获取数据结束")
	return shim.Success([]byte(dtoModel.BaseValue))
}

// 查询历史数据
func GetHistory(stubInterface shim.ChaincodeStubInterface, strings []string) peer.Response {
	SetLogger("获取历史数据开始......")
	defer SetLogger("获取历史数据结束......")
	if len(strings) != 1 {
		return shim.Error("参数信息不正确")
	}
	//校验数据 1、键值不为空
	key := strings[0]
	err := DataCheck(key)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据格式错误:%s", err.Error()))
	}

	mainKey := constructKey(key)
	SetLogger(fmt.Sprintf("查找历史信息：键值为:【%s】 的数据", mainKey))

	resultsIterator, err := stubInterface.GetHistoryForKey(mainKey)
	if err != nil {
		SetLogger(fmt.Sprintf("获取历史信息失败:%s", err.Error()))
		return shim.Error(fmt.Sprintf("获取历史信息失败:%s", err.Error()))
	}

	res := []models.DTOHistoryModel{}

	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			SetLogger(fmt.Sprintf("获取历史信息失败:%s", err.Error()))
			return shim.Error(fmt.Sprintf("获取历史信息失败:%s", err.Error()))
		}
		txTimestamp := queryResponse.GetTimestamp()
		txTime := ""

		if txTimestamp != nil {
			txTime = time.Unix(txTimestamp.Seconds, 0).Format("2006-01-02 15:04:05")
		}

		temp := models.DTOHistoryModel{
			TxId:      queryResponse.TxId,
			IsDelete:  queryResponse.IsDelete,
			Value:     string(queryResponse.Value),
			Timestamp: txTime,
		}

		res = append(res, temp)

	}
	resultByte, _ := json.Marshal(res)

	SetLogger("查询历史记录结束......")

	return shim.Success(resultByte)
}
