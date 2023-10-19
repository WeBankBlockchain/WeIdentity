package did

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/pkg/errors"
	"io"
	"net/http"
)

// GetWeIdDocument 通过请求did获取did doc，进而获取公钥信息
func GetWeIdDocument(restServerIp string, restServerPort string, weid string) (WeIdDocumentInvokeResponse, error) {
	funcName := "getWeIdDocument"
	invokeResponseStr, err := consumeGetWeIdDocumentInvokeRestApi(restServerIp, restServerPort, funcName, weid)
	if err != nil {
		return WeIdDocumentInvokeResponse{}, err
	}

	weIdDocumentInvokeResponse, err := convertJsonToWeIdDocumentInvokeResponse(invokeResponseStr)
	if err != nil {
		return WeIdDocumentInvokeResponse{}, err
	}

	if weIdDocumentInvokeResponse.ErrorCode != 0 {
		return WeIdDocumentInvokeResponse{}, errors.New(weIdDocumentInvokeResponse.ErrorMessage)
	}
	return weIdDocumentInvokeResponse, nil
}

func consumeGetWeIdDocumentInvokeRestApi(restServerIp string, restServerPort string, funcName string, weid string) (string, error) {
	url := getUrl(restServerIp, restServerPort, "invoke")

	jsonB := bytes.Buffer{}
	jsonB.WriteString(`{"functionArg": {"weId": "`)
	jsonB.WriteString(weid)
	jsonB.WriteString(`"}, "transactionArg": {}, "functionName": "`)
	jsonB.WriteString(funcName)
	jsonB.WriteString(`","v": "1.0.0"}`)

	response, err := consumeRestApi(url, jsonB.String())
	return response, err
}

func consumeRestApi(url string, json string) (string, error) {
	jsonBytes := []byte(json)
	response, err := http.Post(url, "application/json", bytes.NewBuffer(jsonBytes))
	if err != nil {
		fmt.Printf("The Http request failed with error %s\n", err)
		return "", err
	}
	data, err := io.ReadAll(response.Body)
	if err != nil {
		return "", err
	}
	return string(data), nil
}

func getUrl(restServerIp string, restServerPort string, mappingType string) string {
	url := bytes.Buffer{}
	url.WriteString("http://")
	url.WriteString(restServerIp)
	url.WriteString(":")
	url.WriteString(restServerPort)
	url.WriteString("/weid/api/")
	url.WriteString(mappingType)
	fmt.Println(url.String())

	return url.String()
}

func convertJsonToWeIdDocumentInvokeResponse(jsonStr string) (WeIdDocumentInvokeResponse, error) {
	jsonBytes := []byte(jsonStr)
	response := WeIdDocumentInvokeResponse{}
	err := json.Unmarshal(jsonBytes, &response)
	if err != nil {
		return response, err
	}
	return response, nil
}
