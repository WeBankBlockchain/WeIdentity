package server

import (
	"did-server/config"
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
)

// DidOAuthClient represents a did OAuth 2.0 client.
type DidOAuthClient struct {
	ClientID     string
	ClientSecret string
}

func Run() error {
	InitOauth()
	r := gin.Default()
	didR := r.Group("")
	{
		didR.GET("authorize", authorize)
		didR.Any("login", login)
		didR.POST("token", token)
		didR.GET("test", Test)
	}

	return r.Run(fmt.Sprintf(":%d", config.Instance().HttpPort))
}

func Test(ctx *gin.Context) {
	ctx.JSON(http.StatusOK, gin.H{
		"msg":  "ok",
		"code": 0,
	})
}
