package session

import (
	// "log"
	"encoding/gob"
	"net/http"
	"net/url"

	"github.com/gorilla/sessions"
	// "gopkg.in/boj/redistore.v1"

	"did-server/config"
)

var store *sessions.CookieStore

func Setup() {
	gob.Register(url.Values{})

	store = sessions.NewCookieStore([]byte(config.Get().Session.SecretKey))
	store.Options = &sessions.Options{
		Path: "/",
		// session 有效期
		// 单位秒
		MaxAge:   config.Get().Session.MaxAge,
		HttpOnly: true,
	}
}

func Get(r *http.Request, name string) (val interface{}, err error) {
	// Get a session.
	session, err := store.Get(r, config.Get().Session.Name)
	if err != nil {
		return
	}

	val = session.Values[name]

	return
}

func Set(w http.ResponseWriter, r *http.Request, name string, val interface{}) (err error) {
	// Get a session.
	session, err := store.Get(r, config.Get().Session.Name)
	if err != nil {
		return
	}

	session.Values[name] = val
	err = session.Save(r, w)

	return
}

func Delete(w http.ResponseWriter, r *http.Request, name string) (err error) {
	// Get a session.
	session, err := store.Get(r, config.Get().Session.Name)
	if err != nil {
		return
	}

	delete(session.Values, name)
	err = session.Save(r, w)

	return
}
