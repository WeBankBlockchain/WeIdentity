package logic

import (
	"bytes"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"github.com/ethereum/go-ethereum/crypto/secp256k1"
	"github.com/pkg/errors"
	"golang.org/x/crypto/sha3"
	"hash/maphash"
	"io"
	"math/big"
	"math/rand"
	"net/http"
	"strconv"
)

// CreateWeId 传入公钥和私钥，创建weid
func CreateWeId(serverIp, port string, pubBytes []byte, privBytes []byte) (string, error) {
	funcName := "createWeId"
	nonce := generateNonce()
	encodeResponseStr, err := consumeCreateWeIdEncodeRestApi(serverIp, port, getPublicBigIntByBytes(pubBytes), nonce, funcName)
	if err != nil {
		fmt.Printf("create weid has some err %v\n", err)
		return "", err
	}

	transactResponseStr, err := processEncodeResponse(encodeResponseStr, serverIp, port, funcName, nonce, privBytes)
	if err != nil {
		fmt.Printf("processEncodeResponse has some err %v\n", err)
		return "", err
	}

	transactResponse, err := convertJsonToTransactResponseStruct(transactResponseStr)
	if err != nil {
		fmt.Printf("convertJsonToTransactResponseStruct has some err %v\n", err)
		return "", err
	}
	if transactResponse.ErrorCode != 0 {
		fmt.Printf("%#v", transactResponse)
		return "", errors.New(transactResponse.ErrorMessage)
	}

	return transactResponse.RespBody, nil
}

func consumeCreateWeIdEncodeRestApi(restServerIp string, restServerPort string, publicKeyBigInt *big.Int, nonce string, funcName string) (string, error) {
	url := getUrl(restServerIp, restServerPort, "encode")

	jsonB := bytes.Buffer{}
	jsonB.WriteString(`{"functionArg":{"publicKey":"`)
	jsonB.WriteString(publicKeyBigInt.String())
	jsonB.WriteString(`"},"functionName":"`)
	jsonB.WriteString(funcName)
	jsonB.WriteString(`","transactionArg": {"nonce": "`)
	jsonB.WriteString(nonce)
	jsonB.WriteString(`"},"v":"1.0.0"}`)
	//fmt.Println("create weid encode request =", json.String())

	response, err := consumeRestApi(url, jsonB.String())
	return response, err
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

func processEncodeResponse(encodeResponseStr string, restServerIp string, restServerPort string, funcName string, nonce string, privateKeyBytes []byte) (string, error) {
	encodeResponse, err := convertJsonToEncodeResponseStruct(encodeResponseStr)
	if err != nil {
		return "", err
	}
	if encodeResponse.ErrorCode != 0 {
		return "", errors.New(encodeResponse.ErrorMessage)
	}
	transaction, err := base64.StdEncoding.DecodeString(encodeResponse.RespBody.EncodedTransaction)
	if err != nil {
		return "", err
	}
	hashedMsg := Hash(transaction)
	signatureBytes, err := signSignature(hashedMsg, privateKeyBytes)
	if err != nil {
		return "", err
	}

	signatureBase64Str := base64.StdEncoding.EncodeToString(signatureBytes)
	transactResponseStr, err := consumeTransactRestApi(restServerIp, restServerPort, signatureBase64Str, encodeResponse.RespBody.Data, nonce, encodeResponse.RespBody.BlockLimit, "2", funcName)
	if err != nil {
		return "", err
	}
	return transactResponseStr, nil
}

func consumeTransactRestApi(restServerIp string, restServerPort string, signature string, data string, nonce, blockLimit, signType string, funcName string) (string, error) {
	url := getUrl(restServerIp, restServerPort, "transact")

	jsonB := bytes.Buffer{}
	jsonB.WriteString(`{"functionArg":{},"functionName":"`)
	jsonB.WriteString(funcName)
	jsonB.WriteString(`","transactionArg": {"nonce": "`)
	jsonB.WriteString(nonce)
	jsonB.WriteString(`","data": "`)
	jsonB.WriteString(data)
	jsonB.WriteString(`","blockLimit": "`)
	jsonB.WriteString(blockLimit)
	jsonB.WriteString(`","signType": "`)
	jsonB.WriteString(signType)
	jsonB.WriteString(`","signedMessage": "`)
	jsonB.WriteString(signature)
	jsonB.WriteString(`"},"v":"1.0.0"}`)

	response, err := consumeRestApi(url, jsonB.String())
	if err != nil {
		fmt.Printf("post transact has some err %v\n", err)
		return "", err
	}
	return response, err
}

func convertJsonToEncodeResponseStruct(jsonStr string) (EncodeResponse, error) {
	jsonBytes := []byte(jsonStr)
	response := EncodeResponse{}
	err := json.Unmarshal(jsonBytes, &response)
	if err != nil {
		return response, err
	}
	return response, nil
}

func convertJsonToTransactResponseStruct(jsonStr string) (TransactResponse, error) {
	jsonBytes := []byte(jsonStr)
	response := TransactResponse{}
	err := json.Unmarshal(jsonBytes, &response)
	if err != nil {
		return response, err
	}
	return response, nil
}

func Hash(msg []byte) []byte {
	h := sha3.NewLegacyKeccak256()
	h.Write(msg)
	return h.Sum(nil)
}

func signSignature(hashedMsg []byte, privateKeyBytes []byte) ([]byte, error) {
	signature, err := secp256k1.Sign(hashedMsg, privateKeyBytes)
	return signature, err
}

func generateNonce() string {
	r := rand.New(rand.NewSource(int64(new(maphash.Hash).Sum64())))
	nonce := strconv.Itoa(r.Int())
	return nonce
}

func getPublicBigIntByBytes(pub []byte) *big.Int {
	return new(big.Int).SetBytes(pub[1:])
}
