/**
 * @功能描述：
 * @author 时跃堂
 * @created by 2019-11-01 13:33
 */
package test

import (
	"bsnBaseCC/bsnchaincode"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"testing"
)

func newMockStub() *shim.MockStub {
	bsnChainCode := new(bsnchaincode.BsnChainCode)
	return shim.NewMockStub("bsnChainCode", bsnChainCode)
}

func checkQuery(t *testing.T, fn string, params []string) {
	checkHandle(t, newMockStub(), fn, params)
}

func checkInvoke(t *testing.T, fn string, params []string) {
	checkHandle(t, newMockStub(), fn, params)
}

/**
 * @description：测试保存方法
 * @author 时跃堂
 * @created by 2019-11-01 13:46:14
 */
func TestSet(t *testing.T) {
	checkInvoke(t, "set", []string{"{\"baseKey\":\"str\",\"baseValue\":\"哈哈哈\"}"})
}

/**
 * @description：测试修改方法
 * @author 时跃堂
 * @created by 2019-11-01 13:46:14
 */
func TestUpdate(t *testing.T) {
	checkInvoke(t, "update", []string{"{\"baseKey\":\"str\",\"baseValue\":\"哈哈哈\"}"})
}

/**
 * @description：测试获取方法
 * @author 时跃堂
 * @created by 2019-11-01 13:46:14
 */
func TestGet(t *testing.T) {
	checkQuery(t, "get", []string{"str"})
}

/**
 * @description：测试删除方法
 * @author 时跃堂
 * @created by 2019-11-01 13:46:14
 */
func TestDelete(t *testing.T) {
	checkInvoke(t, "delete", []string{"str"})
}
