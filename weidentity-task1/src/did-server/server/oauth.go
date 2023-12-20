package server

import (
	"did-server/config"
	"did-server/pkg/session"
	"github.com/go-oauth2/oauth2/v4/errors"
	"github.com/go-oauth2/oauth2/v4/generates"
	"github.com/go-oauth2/oauth2/v4/manage"
	"github.com/go-oauth2/oauth2/v4/models"
	"github.com/go-oauth2/oauth2/v4/server"
	"github.com/go-oauth2/oauth2/v4/store"
	"github.com/golang-jwt/jwt"
	"html/template"
	"log"
	"net/http"
	"time"
)

var srv *server.Server
var mgr *manage.Manager

func InitOauth() {
	// manager config
	mgr = manage.NewDefaultManager()
	mgr.SetAuthorizeCodeTokenCfg(&manage.Config{
		AccessTokenExp:    time.Minute * 30,
		RefreshTokenExp:   time.Minute * 30,
		IsGenerateRefresh: true})
	// token store
	mgr.MustTokenStorage(store.NewMemoryTokenStore())

	// access token generate method: jwt
	mgr.MapAccessGenerate(generates.NewJWTAccessGenerate("", []byte(config.Get().OAuth2.JWTSignedKey), jwt.SigningMethodHS512))

	clientStore := store.NewClientStore()
	for _, v := range config.Get().OAuth2.Client {
		clientStore.Set(v.ID, &models.Client{
			ID:     v.ID,
			Secret: v.Secret,
			Domain: v.Domain,
		})
	}

	mgr.MapClientStorage(clientStore)
	// config oauth2 server
	srv = server.NewServer(server.NewConfig(), mgr)
	srv.SetPasswordAuthorizationHandler(passwordAuthorizationHandler)
	srv.SetUserAuthorizationHandler(userAuthorizeHandler)
	srv.SetAuthorizeScopeHandler(authorizeScopeHandler)
	srv.SetInternalErrorHandler(internalErrorHandler)
	srv.SetResponseErrorHandler(responseErrorHandler)
}

func passwordAuthorizationHandler(username, password string) (string, error) {
	return Authentication(username, password)
}

func userAuthorizeHandler(w http.ResponseWriter, r *http.Request) (string, error) {
	v, _ := session.Get(r, "LoggedInUserID")
	if v == nil {
		if r.Form == nil {
			r.ParseForm()
		}
		session.Set(w, r, "RequestForm", r.Form)

		// 登录页面
		// 最终会把userId写进session(LoggedInUserID)
		// 再跳回来
		w.Header().Set("Location", "/login")
		w.WriteHeader(http.StatusFound)

		return "", nil
	}
	// 不记住用户
	// store.Delete("LoggedInUserID")
	// store.Save()

	return v.(string), nil
}

// 场景:在登录页面勾选所要访问的资源范围
// 根据client注册的scope,过滤表单中非法scope
// HandleAuthorizeRequest中调用
// set scope for the access token
func authorizeScopeHandler(w http.ResponseWriter, r *http.Request) (scope string, err error) {
	if r.Form == nil {
		r.ParseForm()
	}
	s := config.ScopeFilter(r.Form.Get("client_id"), r.Form.Get("scope"))
	if s == nil {
		err = errors.New("无效的权限范围")
		return
	}
	scope = config.ScopeJoin(s)
	return
}

func internalErrorHandler(err error) (re *errors.Response) {
	log.Println("Internal Error:", err.Error())
	return
}

func responseErrorHandler(re *errors.Response) {
	log.Println("Response Error:", re.Error.Error())
}

// 错误显示页面
// 以网页的形式展示大于400的错误
func errorHandler(w http.ResponseWriter, message string, status int) {
	w.WriteHeader(status)
	if status >= 400 {
		t, _ := template.ParseFiles("tpl/error.html")
		body := struct {
			Status  int
			Message string
		}{Status: status, Message: message}
		t.Execute(w, body)
	}
}
