package utils

import (
	"fmt"
	"time"
)

// 定义时间格式
const TIME_FORMAT string = "2006-01-02 15:04:05.000"

// 获取时间
func getTime() string {
	return time.Now().Format(TIME_FORMAT)
}

// 写日志
func SetLogger(logInfo ...interface{}) {
	fmt.Printf("%s  ->  %s\n", getTime(), logInfo)
}
