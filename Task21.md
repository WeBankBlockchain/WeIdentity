# **Task 21**

## **任务**

实现新的密钥或助记词备份机制，比如社交恢复，m of n共享。

## **设计**

### **设计思路**

使用Shamir's Secret Sharing算法来实现新的密钥或助记词备份机制。



这个算法可以将一个私钥拆分成多个部分，只有在满足m个部分的情况下才能还原出原始的私钥。例如，在社交恢复中，可以将私钥拆分成n份并分发给不同的好友，只有当有m个好友汇聚在一起时才能重新拥有这个私钥。



### **代码**

以下是用Go语言实现Shamir's Secret Sharing算法的代码示例：



```
package main

import (
	"crypto/rand"
	"fmt"
	"math/big"
)

type Point struct {
	x, y *big.Int
}

func (p1 Point) add(p2 Point) Point {
	return Point{new(big.Int).Add(p1.x, p2.x), new(big.Int).Add(p1.y, p2.y)}
}

func (p1 Point) mul(m *big.Int) Point {
	return Point{new(big.Int).Mul(p1.x, m), new(big.Int).Mul(p1.y, m)}
}

func evalPoly(poly []*big.Int, x *big.Int) *big.Int {
	res := big.NewInt(0)
	tmp := big.NewInt(1)
	for _, coef := range poly {
		res.Add(res, tmp.Mul(coef, tmp))
		tmp.Mul(tmp, x)
	}
	return res
}

func splitSecret(secret *big.Int, m, n int) ([]Point, error) {
	if m > n {
		return nil, fmt.Errorf("m should be less than or equal to n")
	}

	poly := make([]*big.Int, m)
	poly[0] = secret
	for i := 1; i < m; i++ {
		coef, err := rand.Int(rand.Reader, big.NewInt(256))
		if err != nil {
			return nil, fmt.Errorf("failed to generate random coefficient: %v", err)
		}
		poly[i] = coef
	}

	points := make([]Point, n)
	for i := 1; i <= n; i++ {
		x := big.NewInt(int64(i))
		y := evalPoly(poly, x)
		points[i-1] = Point{x, y}
	}
	return points, nil
}

func recoverSecret(points []Point) (*big.Int, error) {
	if len(points) == 0 {
		return nil, fmt.Errorf("points should not be empty")
	}

	var secret *big.Int
	for i, p1 := range points {
		numerator := big.NewInt(1)
		denominator := big.NewInt(1)
		for j, p2 := range points {
			if i == j {
				continue
			}
			numerator.Mul(numerator, p2.x)
			diff := new(big.Int).Sub(p2.x, p1.x)
			denominator.Mul(denominator, diff)
		}
		tmp := new(big.Int).Div(numerator, denominator)
		tmp.Mul(tmp, p1.y)
		if secret == nil {
			secret = tmp
		} else {
			secret.Add(secret, tmp)
		}
	}
	return secret, nil
}

func main() {
	// Example usage:
	secret := big.NewInt(20230520)
	m, n := 3, 5
	points, err := splitSecret(secret, m, n)
	if err != nil {
		fmt.Printf("failed to split secret: %v", err)
		return
	}
	fmt.Println("Points:")
	for _, p := range points {
		fmt.Printf("(%v, %v)\n", p.x, p.y)
	}

	recoveredSecret, err := recoverSecret(points[:m])
	if err != nil {
		fmt.Printf("failed to recover secret: %v", err)
		return
	}
	fmt.Println("Recovered secret:", recoveredSecret)
}
```

