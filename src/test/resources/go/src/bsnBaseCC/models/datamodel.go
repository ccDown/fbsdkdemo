package models

// 数据
type DBBaseModel struct {
	BaseKey  string
	BaseInfo string
}

// 数据传递
type DTOBaseModel struct {
	BaseKey   string `json:"baseKey"`
	BaseValue string `json:"baseValue"`
}

// 历史信息
type DTOHistoryModel struct {
	TxId      string `json:"txId"`
	Value     string `json:"dataInfo"`
	Timestamp string `json:"txTime"`
	IsDelete  bool   `json:"isDelete"`
}

func DTOBase2Db(dtoModel DTOBaseModel) DBBaseModel {
	return DBBaseModel{BaseKey: dtoModel.BaseKey, BaseInfo: dtoModel.BaseValue}
}

func Db2DTOBase(model DBBaseModel) DTOBaseModel {
	return DTOBaseModel{BaseKey: model.BaseKey, BaseValue: model.BaseInfo}
}
