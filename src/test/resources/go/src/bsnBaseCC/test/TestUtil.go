/**
 * @description：校验链码
 *
 * 使用方法如下---------------------------------------
 * baseChainCode := new(bcccode)
 * stub := shim.NewMockStub("baseChainCode", baseChainCode)
 * test.CheckInit(t, stub, [][]byte{})
 * test.CheckInvoke(t, stub, "set", []string{"{\"baseKey\":\"str\",\"baseValue\":\"哈哈哈\"}"})
 * test.CheckQuery(t, stub, "get", []string{"str"})
 * ----------------------------------------------------
 * 进入相应的目录下，通过以下命令测试响应的方法
 * ----------------------------------------------------
 * go test -v xxx_test.go xxx.go
 * ----------------------------------------------------
 *
 * @author bsn
 * @created by 2019-06-26 10:09:34
 */
package test

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"testing"
)

/**
 * @description：测试init方法
 * @author 时跃堂
 * @created by 2019-06-26 09:57:33
 */
func CheckInit(t *testing.T, stub *shim.MockStub, args [][]byte) {
	res := stub.MockInit("1", args)
	if res.Status != shim.OK {
		fmt.Println("init failed:", string(res.Message))
		t.FailNow()
	}
}

/**
 * @description：测试查询方法
 * @author 时跃堂
 * @created by 2019-06-26 09:57:48
 */
func CheckQuery(t *testing.T, stub *shim.MockStub, fn string, params []string) {
	checkHandle(t, stub, fn, params)
}

/**
 * @description：测试invoker方法
 * @author 时跃堂
 * @created by 2019-06-26 09:58:03
 */
func CheckInvoke(t *testing.T, stub *shim.MockStub, fn string, params []string) {
	checkHandle(t, stub, fn, params)
}

/**
 * @description：测试处理
 * @author 时跃堂
 * @created by 2019-06-26 09:58:19
 */
func checkHandle(t *testing.T, stub *shim.MockStub, fn string, params []string) {
	paramsValue := [][]byte{}
	paramsValue = append(paramsValue, []byte(fn))
	if len(params) > 0 {
		for _, v := range params {
			vv := []byte(v)
			paramsValue = append(paramsValue, vv)
		}
	}

	res := stub.MockInvoke("1", paramsValue)

	fmt.Printf("【%s】 Response Status:【%d】\n", fn, res.Status)

	if res.Status != shim.OK {
		fmt.Printf("【%s】 failed:%s\n", fn, string(res.Message))
		t.FailNow()
	}
	fmt.Printf("【%s】 Response Value:【%s】\n", fn, string(res.Payload))
}
