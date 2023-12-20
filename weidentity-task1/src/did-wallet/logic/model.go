package logic

type TransactResponse struct {
	RespBody     string
	ErrorCode    int
	ErrorMessage string
}

type EncodeResponse struct {
	RespBody     *RespBody
	ErrorCode    int
	ErrorMessage string
}

type RespBody struct {
	BlockLimit         string `json:"blockLimit"`
	Data               string `json:"data"`
	EncodedTransaction string `json:"encodedTransaction"`
}
