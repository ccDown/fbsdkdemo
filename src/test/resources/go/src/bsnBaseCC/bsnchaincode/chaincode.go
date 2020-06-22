package bsnchaincode

import (
	"bsnBaseCC/models"
	"bsnBaseCC/utils"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	strings2 "strings"
)

type BsnChainCode struct {
}

// 设置日志
func SetLogger(logInfo ...interface{}) {
	utils.SetLogger(logInfo)
}

// 数据校验
func DataCheck(model string) error {
	if strings2.TrimSpace(model) == "" {
		return errors.New("baseKey不能为空")
	}
	return nil
}

func (t *BsnChainCode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	SetLogger("ChainCode Init start......")
	defer SetLogger("ChainCode Init end......")
	dbBaseModel := models.DBBaseModel{BaseKey: "cc_key_", BaseInfo: "Welcome to use ChainCode "}
	reqJsonValue, err := json.Marshal(&dbBaseModel)
	if err != nil {
		return shim.Error(fmt.Sprintf("数据转换失败:%s", err.Error()))
	}
	err = stub.PutState(dbBaseModel.BaseKey, reqJsonValue)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

func (t *BsnChainCode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	function, args := stub.GetFunctionAndParameters()
	switch function {
	case "set": // 保存
		return Set(stub, args)
	case "update": // 修改
		return Update(stub, args)
	case "delete": // 删除
		return Delete(stub, args)
	case "get": // 获取
		return Get(stub, args)
	case "getHistory": // 获取历史信息
		return GetHistory(stub, args)
	default:
		SetLogger("无效的方法")
		break
	}
	return shim.Error("无效的请求")
}
