package main

import (
	"did-client/httpx"
	"fmt"
)

func main() {
	fmt.Printf("=== Welcome To DID Oauth 2.0 Client ===\n")
	err := httpx.Run()
	if err != nil {
		fmt.Printf("start oatuh client has some err %v\n", err)
		return
	}
}
