package main

import (
	"did-wallet/logic"
	"encoding/base64"
	"fmt"
	"github.com/spf13/cobra"
	"os"
)

var (
	privateKeyPath string
	publicKeyPath  string
	didServiceIp   string
	didServicePort string
)

var rootCmd = &cobra.Command{
	Use:   "weid-tool",
	Short: "weidentity simple tool",
	Long:  `简易的weid工具，该工具提供weid、公私钥生成、签名等功能`,
}

// 生成公私钥信息
var keyCmd = &cobra.Command{
	Use:   "key",
	Short: "生成公私钥对、weid",
	Long:  `生成公私钥对、weid，并将生成公钥和私钥对应的文件（默认路径：./pub.key，./priv.key）`,
	Run: func(cmd *cobra.Command, args []string) {
		pub, pri, _, _ := logic.GenerateKeyPair()
		err := os.WriteFile("priv.key", pri, 0666)
		if err != nil {
			fmt.Printf("generate private key has some err %v\n", err)
			os.Exit(1)
		}
		err = os.WriteFile("pub.key", pub, 0666)
		if err != nil {
			fmt.Printf("generate public key has some err %v\n", err)
			os.Exit(1)
		}

		fmt.Println("generate private key in ./priv.key")
		fmt.Println("generate public key in ./pub.key")
	},
}

// 签名
var signCmd = &cobra.Command{
	Use:   "sign [message]",
	Short: "将传入的message信息进行签名",
	Long:  `使用私钥将传入的message信息进行签名，并生成base64编码后的签名信息`,
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		message := args[0]
		privateKeyBytes, err := os.ReadFile(privateKeyPath)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		messageHash := logic.Hash([]byte(message))
		signatureBytes, err := logic.SignSignature(messageHash, privateKeyBytes)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		signature := base64.StdEncoding.EncodeToString(signatureBytes)

		fmt.Printf("Message: %s\n", message)
		fmt.Printf("Signature: %s\n", signature)
	},
}

// 生成weid信息
var genWeIdCmd = &cobra.Command{
	Use:   "gendid",
	Short: "通过读取公钥和私钥信息，创建did",
	Long:  "公钥用于生成did，私钥用于创建上链（did链）的签名信息",
	Run: func(cmd *cobra.Command, args []string) {
		privateKeyBytes, err := os.ReadFile(privateKeyPath)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		publicKeyBytes, err := os.ReadFile(publicKeyPath)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		weid, err := logic.CreateWeId(didServiceIp, didServicePort, publicKeyBytes, privateKeyBytes)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		fmt.Printf("generate weid success: %s\n", weid)
	},
}

func init() {
	rootCmd.AddCommand(keyCmd)
	signCmd.PersistentFlags().StringVarP(&privateKeyPath, "private key path", "p", "", "用户私钥文件的路径")
	rootCmd.AddCommand(signCmd)
	genWeIdCmd.Flags().StringVarP(&publicKeyPath, "public key path", "p", "", "用户公钥文件地址")
	genWeIdCmd.Flags().StringVarP(&privateKeyPath, "private key path", "s", "", "用户私钥文件地址")
	genWeIdCmd.Flags().StringVarP(&didServiceIp, "did service ip", "i", "", "did服务ip地址")
	genWeIdCmd.Flags().StringVarP(&didServicePort, "did service port", "t", "", "did服务port地址")
	rootCmd.AddCommand(genWeIdCmd)
}

func main() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
}
