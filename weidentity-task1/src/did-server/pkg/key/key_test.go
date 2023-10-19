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
	pub := "72907716505989393088186550107679567364185218721963761981761904328221802680748"

	publicKeyBytes := ConvertPublicKeyBigIntToPublicBytes(pub)

	//pubPath := "./pub.key"
	//publicKeyBytes, err := os.ReadFile(pubPath)
	//if err != nil {
	//	t.Error(err)
	//	return
	//}

	msg := "ccdab6fd-4dbd-4ab2-bb18-4ded35d888b8"
	signMsg := "JGYfdcYtOrTL15V4T0n5HKQ1+boAfGNPGxMBHyTz+fJ1c3YefTCK/1ELnh2bfpWvaApuDuqWhHKia7Fb/0AszQA="

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
