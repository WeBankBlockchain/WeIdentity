package config

import (
	// "fmt"
	"strings"
)

func Get() *App {
	return conf
}

func GetOAuth2Client(clientID string) (cli OAuth2Client) {
	for _, v := range conf.OAuth2.Client {
		if v.ID == clientID {
			cli = v
		}
	}

	return
}

func ScopeJoin(scope []Scope) string {
	var s []string
	for _, sc := range scope {
		s = append(s, sc.ID)
	}
	return strings.Join(s, ",")
}

func ScopeFilter(clientID string, scope string) (s []Scope) {
	cli := GetOAuth2Client(clientID)
	sl := strings.Split(scope, ",")
	for _, str := range sl {
		for _, sc := range cli.Scope {
			if str == sc.ID {
				s = append(s, sc)
			}
		}
	}

	return
}
