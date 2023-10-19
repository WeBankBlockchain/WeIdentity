package httpx

import (
	"did-client/config"
	"fmt"
	"github.com/gin-gonic/gin"
)

// DidOAuthClient represents a did OAuth 2.0 client.
type DidOAuthClient struct {
	ClientID     string
	ClientSecret string
}

func Run() error {
	r := gin.Default()
	didR := r.Group("v1")
	{
		didR.GET("loginWithDid", loginWithDid)
		didR.GET("callback", callback)
		didR.GET("loginWithCode", loginWithCode)
	}

	return r.Run(fmt.Sprintf(":%d", config.ConfigIns().HttpPort))
}
