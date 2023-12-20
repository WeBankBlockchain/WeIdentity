package config

import (
	"os"
	"sync"

	"github.com/spf13/viper"
)

var (
	once sync.Once
	conf *Config
)

type Config struct {
	path          string
	HttpPort      int
	DidServerInfo DidServerInfo `yaml:"didServerInfo"`
	DidClientInfo DidClientInfo `yaml:"didClientInfo"`
	RedirectFront RedirectFront `yaml:"redirectFront"`
}

type DidServerInfo struct {
	Ip   string `yaml:"ip"`
	Port int    `yaml:"port"`
}

type RedirectFront struct {
	Ip   string `yaml:"ip"`
	Port int    `yaml:"port"`
}

type DidClientInfo struct {
	Ip       string `yaml:"ip"`
	Port     int    `yaml:"port"`
	ClientId string `yaml:"clientId"`
	Secret   string `yaml:"secret"`
}

func ConfigIns() *Config {
	return conf
}

func init() {
	once.Do(func() {
		conf = new(Config)
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
