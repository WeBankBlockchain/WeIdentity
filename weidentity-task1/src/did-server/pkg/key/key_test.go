package key

import (
	"encoding/base64"
	"math/big"
	"os"
	"testing"
)

func TestConvertToPubAndVerify(t *testing.T) {
	//pub := "112028570508690591160769248348403126748000109039375280547090697478051580518976"
	//pub := "56324583500139157107793773182419696027958188324056843006630313992576092448359"
	pub := "18842764861699425946864317179718860321130262798130268203739550375195125305295"
	//18842764861699425946864317179718860321130262798130268203739550375195125305295

	publicKeyBytes := ConvertPublicKeyBigIntToPublicBytes(pub)

	//pubPath := "./pub.key"
	//publicKeyBytes, err := os.ReadFile(pubPath)
	//if err != nil {
	//	t.Error(err)
	//	return
	//}

	msg := "hello world"
	signMsg := "vWh1Yhu2ZwTOlsGMHbcPSwb2s2VMMRR8kWqU5UPGhyIlrbN7hSw23WQGq2IC1kWJ2qTV4A9Mi7x9SWuWEOgG0gE="

	signedMsgBytes, err := base64.StdEncoding.DecodeString(signMsg)
	if err != nil {
		t.Error(err)
		return
	}

	yes := VerifySignature(Hash([]byte(msg)), signedMsgBytes, publicKeyBytes)

	if yes {
		t.Log("verify success...")
	} else {
		t.Log("verify failed...")
	}
}

func TestHandlePublicKey(t *testing.T) {
	pubPath := "./pub.key"
	pubBytes, err := os.ReadFile(pubPath)
	if err != nil {
		t.Error(err)
		return
	}

	publicKeyBigInt := new(big.Int).SetBytes(pubBytes[1:])

	t.Log(publicKeyBigInt.String())
}
