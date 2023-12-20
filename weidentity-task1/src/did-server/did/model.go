package did

type WeIdDocumentInvokeResponse struct {
	RespBody     *WeIdDocumentBody `json:"respBody"`
	ErrorCode    int               `json:"errorCode"`
	ErrorMessage string            `json:"errorMessage"`
}

type WeIdDocumentBody struct {
	ResolutionMetadata   ResolutionMetadata   `json:"resolutionMetadata"`
	WeIdDocumentJson     string               `json:"weIdDocumentJson"`
	WeIdDocumentMetadata WeIdDocumentMetadata `json:"weIdDocumentMetadata"`
}

type WeIdDocumentMetadata struct {
	Created     string `json:"created"`
	Updated     string `json:"updated"`
	Deactivated bool   `json:"deactivated"`
	VersionId   int    `json:"versionId"`
}

type ResolutionMetadata struct {
	ContentType string `json:"contentType"`
	Error       string `json:"error"`
}

type WeIdDocumentJson struct {
	Context        string                 // doc 上下文
	Id             string                 `json:"id"`             // weid doc的唯一id
	Authentication []AuthenticationStruct `json:"authentication"` // 断言结构
	Service        []ServiceStruct        `json:"service"`        // 服务端点
}

type AuthenticationStruct struct {
	Id                 string
	Type               string
	Controller         string
	PublicKeyMultibase string
	PublicKey          string
}

type ServiceStruct struct {
	Id              string `json:"id"`
	Type            string `json:"type"`
	ServiceEndpoint string `json:"serviceEndpoint"`
}
