/**
 * @功能描述：
 * @author 时跃堂
 * @created by 2019-11-06 17:01
 */
package models

//分页信息
type PageInfo struct {
	PageIndex int    `json:"pageIndex"` //当前页面
	PageSize  int    `json:"pageSize"`  //每页条数
	Bookmark  string `json:"bookmark"`  // 书签
}

type PageListResult struct {
	TotalCount int32       `json:"totalCount"` // 总记录数
	List       interface{} `json:"list"`       // 响应信息
}
