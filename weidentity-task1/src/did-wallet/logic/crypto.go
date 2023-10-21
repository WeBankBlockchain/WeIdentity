package logic

import (
	"crypto/ecdsa"
	"crypto/elliptic"
	"crypto/rand"
	"github.com/ethereum/go-ethereum/crypto/secp256k1"
	"golang.org/x/crypto/sha3"
	"math/big"
)

func GenerateKeyPair() (publicKeyBytes []byte, privateKeyBytes []byte, publicKeyBigInt *big.Int, privateKeyBigInt *big.Int) {
	key, err := ecdsa.GenerateKey(secp256k1.S256(), rand.Reader)
	if err != nil {
		panic(err)
	}

	publicKeyBytes = elliptic.MarshalCompressed(secp256k1.S256(), key.X, key.Y)
	publicKeyBigInt = new(big.Int).SetBytes(publicKeyBytes[1:])
	privateKeyBytes = convertPrivateKeyBigIntToPrivateKeyBytes(key.D)
	return publicKeyBytes, privateKeyBytes, publicKeyBigInt, key.D
}

func convertPrivateKeyBigIntToPrivateKeyBytes(privateKeyBigInt *big.Int) []byte {
	privateKeyBytes := make([]byte, 32)
	blob := privateKeyBigInt.Bytes()
	copy(privateKeyBytes[32-len(blob):], blob)
	return privateKeyBytes
}

func SignSignature(hashedMsg []byte, privateKeyBytes []byte) ([]byte, error) {
	signature, err := secp256k1.Sign(hashedMsg, privateKeyBytes)
	return signature, err
}

// hash待签名信息
func Hash(msg []byte) []byte {
	h := sha3.NewLegacyKeccak256()
	h.Write(msg)
	return h.Sum(nil)
}
