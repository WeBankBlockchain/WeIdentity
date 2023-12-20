package key

import (
	"crypto/ecdsa"
	"crypto/elliptic"
	"github.com/ethereum/go-ethereum/crypto/secp256k1"
	"golang.org/x/crypto/sha3"
	"math/big"
)

// hash待签名信息
func Hash(msg []byte) []byte {
	h := sha3.NewLegacyKeccak256()
	h.Write(msg)
	return h.Sum(nil)
}

// 传入公钥，校验公钥对应私钥的签名信息
func VerifySignature(msg []byte, signedMsg []byte, publicKeyBytes []byte) bool {
	signatureNoRecoverID := signedMsg[:len(signedMsg)-1]
	return secp256k1.VerifySignature(publicKeyBytes, msg, signatureNoRecoverID)
}

// 通过公钥的X推出公钥的字节数组
func ConvertPublicKeyBigIntToPublicBytes(pubBigInt string) []byte {
	publicKeyInt := new(big.Int)
	publicKeyInt.SetString(pubBigInt, 10)
	curve := secp256k1.S256()

	pubKey := &ecdsa.PublicKey{
		Curve: curve,
		X:     publicKeyInt,
		Y:     deriveY(publicKeyInt, curve),
	}

	return elliptic.MarshalCompressed(secp256k1.S256(), pubKey.X, pubKey.Y)
}

// 通过椭圆曲线算法类型和X计算Y
func deriveY(x *big.Int, curve *secp256k1.BitCurve) *big.Int {
	// 计算 y^2 = x^3 + ax + b
	x3 := new(big.Int).Mul(x, x)
	x3.Mul(x3, x)
	a := curve.Params().P
	b := curve.Params().B
	y2 := new(big.Int).Add(x3, new(big.Int).Mul(a, x))
	y2.Add(y2, b)

	// 计算 y = sqrt(y^2)
	y := new(big.Int).ModSqrt(y2, curve.Params().P)

	// 如果 y 的奇偶性与公钥的标志位不一致，则需要取反 y
	if y.Bit(0) != x.Bit(0) {
		y.Sub(curve.Params().P, y)
	}

	return y
}
