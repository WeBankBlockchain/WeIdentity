package httpx

import (
	"did-client/config"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"io"
	"net/http"

	"github.com/gin-gonic/gin"
)

type TokenRequest struct {
	GrantType   string `json:"grant_type"`
	Code        string `json:"code"`
	RedirectUri string `json:"redirect_uri"`
}

type ThirdClientResp struct {
	AccessToken  string `json:"access_token"`
	ExpiresIn    int    `json:"expires_in"`
	RefreshToken string `json:"refresh_token"`
	Scope        string `json:"scope"`
	TokenType    string `json:"token_type"`
}

// 通过did的方式进行登录
func loginWithDid(ctx *gin.Context) {
	ctx.Redirect(http.StatusSeeOther, loginRedirectUri())
}

func loginRedirectUri() string {
	return fmt.Sprintf("http://%s:%d/authorize?client_id=%s&response_type=code&scope=all&state=xyz&redirect_uri=http://%s:%d/v1/callback",
		config.ConfigIns().DidServerInfo.Ip, config.ConfigIns().DidServerInfo.Port,
		config.ConfigIns().DidClientInfo.ClientId, config.ConfigIns().DidClientInfo.Ip, config.ConfigIns().DidClientInfo.Port)
}

func callback(ctx *gin.Context) {
	code := ctx.Query("code")

	fmt.Println(code)

	ctx.Redirect(http.StatusFound, fmt.Sprintf("http://%s:%d/oauth-result?code=%s", config.ConfigIns().RedirectFront.Ip, config.ConfigIns().RedirectFront.Port, code))
}

func loginWithCode(ctx *gin.Context) {
	code := ctx.Query("code")

	resp, err := getAccessToken(code)
	if err != nil {
		ctx.JSON(http.StatusOK, gin.H{
			"code": -1,
			"msg":  err.Error(),
		})
		ctx.Abort()
		return
	}

	ret := new(ThirdClientResp)

	err = json.Unmarshal(resp, ret)
	if err != nil {
		ctx.JSON(http.StatusOK, gin.H{
			"code": -1,
			"msg":  err.Error(),
		})
		ctx.Abort()
		return
	}

	ctx.JSON(http.StatusOK, gin.H{
		"code": 0,
		"msg":  "success",
		"data": ret,
	})
}

func getAccessToken(code string) ([]byte, error) {
	// 创建HTTP请求
	req, err := http.NewRequest("POST", getAccessTokenUri(code), nil)
	if err != nil {
		fmt.Printf("http request has some err %v\n", err)
		return nil, err
	}
	// http header添加auth认证信息
	req.Header.Add("Authorization", authInfo())

	// 发起http请求
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		fmt.Printf("client do request has some err %v\n", err)
		return nil, err
	}

	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		fmt.Printf("io read http body has some err %v\n", err)
		return nil, err
	}

	return respBody, err
}

// 获取access token
func getAccessTokenUri(code string) string {
	return fmt.Sprintf("http://%s:%d/token?grant_type=authorization_code&code=%s&redirect_uri=http://%s:%d/v1/callback", config.ConfigIns().DidServerInfo.Ip, config.ConfigIns().DidServerInfo.Port, code,
		config.ConfigIns().DidClientInfo.Ip, config.ConfigIns().HttpPort)
}

// 添加header认证信息
func authInfo() string {
	basicAuth := "Basic " + base64.StdEncoding.EncodeToString([]byte(fmt.Sprintf("%s:%s", config.ConfigIns().DidClientInfo.ClientId, config.ConfigIns().DidClientInfo.Secret)))
	return basicAuth
}
