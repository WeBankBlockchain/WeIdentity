package logic

import (
	"crypto/ecdsa"
	"crypto/elliptic"
	"crypto/rand"
	"github.com/ethereum/go-ethereum/crypto/secp256k1"
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
