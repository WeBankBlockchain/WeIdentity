package server

import (
	"did-server/config"
	"did-server/did"
	"did-server/pkg/key"
	"did-server/pkg/session"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/pkg/errors"
	"html/template"
	"net/http"
	"net/url"
	"time"
)

const (
	signedMsgFileName = "signedMsg.txt"
)

var msgInfo string

// 验证client
func authorize(ctx *gin.Context) {
	w := ctx.Writer
	r := ctx.Request

	var form url.Values
	if v, _ := session.Get(r, "RequestForm"); v != nil {
		r.ParseForm()
		if len(r.Form.Get("client_id")) == 0 {
			form = v.(url.Values)
		}
	}
	r.Form = form

	if err := session.Delete(w, r, "RequestForm"); err != nil {
		errorHandler(w, err.Error(), http.StatusInternalServerError)
		return
	}

	if err := srv.HandleAuthorizeRequest(w, r); err != nil {

		fmt.Println(err)

		errorHandler(w, err.Error(), http.StatusBadRequest)
		return
	}
}

type TplData struct {
	Client config.OAuth2Client
	// 用户申请的合规scope
	Scope   []config.Scope
	Error   string
	SignMsg string `json:"signMsg"`
}

type GenSignMsgRequest struct {
	Did string `json:"did"`
}

// 登录
func login(ctx *gin.Context) {
	w := ctx.Writer
	r := ctx.Request
	form, _ := session.Get(r, "RequestForm")
	if form == nil {
		errorHandler(w, "无效的请求", http.StatusInternalServerError)
		return
	}
	clientID := form.(url.Values).Get("client_id")

	if r.Method != "POST" {
		// 页面数据
		data := TplData{
			Client:  config.GetOAuth2Client(clientID),
			SignMsg: genMsg(),
		}

		t, _ := template.ParseFiles("tpl/login.html")
		t.Execute(w, data)
	} else if r.Method == "POST" {
		var (
			weDid string
			err   error
		)

		if r.Form == nil {
			err = r.ParseForm()
			if err != nil {
				errorHandler(w, err.Error(), http.StatusInternalServerError)
				return
			}
		}

		weDid, err = Authentication(r.Form.Get("username"), r.Form.Get("password"))
		if err != nil {
			// 页面数据
			data := TplData{
				Client:  config.GetOAuth2Client(clientID),
				SignMsg: genMsg(),
				Error:   err.Error(),
			}
			data.Error = err.Error()
			t, _ := template.ParseFiles("tpl/login.html")
			t.Execute(w, data)
			return
		}

		err = session.Set(w, r, "LoggedInUserID", weDid)
		if err != nil {
			errorHandler(w, err.Error(), http.StatusInternalServerError)
			return
		}

		w.Header().Set("Location", "/authorize")
		w.WriteHeader(http.StatusFound)
		return
	}
}

// 生成每次登录验证的待签名信息
func genMsg() string {
	msgInfo = uuid.NewString()
	return msgInfo
}

// 验证用户登录信息
func Authentication(username string, signedMsg string) (string, error) {
	pubBytes, err := getPublicKeyBytesByDid(username)
	if err != nil {
		return "", err
	}

	signedMsgBytes, err := base64.StdEncoding.DecodeString(signedMsg)
	if err != nil {
		fmt.Printf("decode string has some err %v\n", err)
		return "", err
	}

	success := key.VerifySignature(key.Hash([]byte(msgInfo)), signedMsgBytes, pubBytes)
	if !success {
		return "", errors.New("user sign info has some err")
	}

	return username, nil
}

// 生成access token
func token(ctx *gin.Context) {
	w := ctx.Writer
	r := ctx.Request
	err := srv.HandleTokenRequest(w, r)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}

// 通过did获取公钥信息
func getPublicKeyBytesByDid(weid string) ([]byte, error) {
	resp, err := did.GetWeIdDocument(config.DIdServerIp(), config.DIdServerPort(), weid)
	if err != nil {
		return nil, err
	}

	weidDocJson := new(did.WeIdDocumentJson)
	err = json.Unmarshal([]byte(resp.RespBody.WeIdDocumentJson), weidDocJson)
	if err != nil {
		return nil, err
	}

	if len(weidDocJson.Authentication) != 1 {
		return nil, errors.New("user pub amount is not correct.")
	}

	pubBytes := key.ConvertPublicKeyBigIntToPublicBytes(weidDocJson.Authentication[0].PublicKey)

	return pubBytes, nil
}

// 校验access token
func verify(ctx *gin.Context) {
	w := ctx.Writer
	r := ctx.Request
	tokenInfo, err := srv.ValidationBearerToken(r)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}
	cli, err := mgr.GetClient(r.Context(), tokenInfo.GetClientID())
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	data := map[string]interface{}{
		"expires_in": int64(tokenInfo.GetAccessCreateAt().Add(tokenInfo.GetAccessExpiresIn()).Sub(time.Now()).Seconds()),
		"user_id":    tokenInfo.GetUserID(),
		"client_id":  tokenInfo.GetClientID(),
		"scope":      tokenInfo.GetScope(),
		"domain":     cli.GetDomain(),
	}
	e := json.NewEncoder(w)
	e.SetIndent("", "  ")
	e.Encode(data)
}
