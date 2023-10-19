package main

import (
	"did-server/config"
	"did-server/pkg/session"
	"did-server/server"
	"log"
)

func main() {
	// 配置文件初始化
	config.Setup()

	// session 初始化
	session.Setup()

	err := server.Run()
	if err != nil {
		log.Fatalln(err)
	}
}
