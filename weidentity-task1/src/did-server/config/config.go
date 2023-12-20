package config

import (
	"github.com/spf13/viper"
	"os"
	"sync"
)

var (
	once sync.Once
	conf *App
)

func Setup() {
	once.Do(func() {
		conf = new(App)
	})

	var err error

	conf.path = os.Getenv("CONF_PATH")
	if len(conf.path) == 0 {
		conf.path, err = os.Getwd()
		if err != nil {
			panic(err)
		}
	}

	viper.AddConfigPath(conf.path)
	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	err = viper.ReadInConfig()
	if err != nil {
		panic(err)
	}

	err = viper.Unmarshal(conf)
	if err != nil {
		panic(err)
	}
}

func Instance() *App {
	return conf
}

func DIdServerIp() string {
	return conf.DidServer.Ip
}

func DIdServerPort() string {
	return conf.DidServer.Port
}
