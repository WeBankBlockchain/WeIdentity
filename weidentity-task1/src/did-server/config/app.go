package config

type App struct {
	path     string `yaml:"path"`
	HttpPort int    `yaml:"httpPort"`
	Session  struct {
		Name      string `yaml:"name"`
		SecretKey string `yaml:"secret_key"`
		MaxAge    int    `yaml:"max_age"`
	} `yaml:"session"`

	OAuth2 struct {
		AccessTokenExp int            `yaml:"access_token_exp"`
		JWTSignedKey   string         `yaml:"jwt_signed_key"`
		Client         []OAuth2Client `yaml:"client"`
	} `yaml:"oauth2"`
	DidServer DidServer `yaml:"didServer"`
}

type OAuth2Client struct {
	ID     string  `yaml:"id"`
	Secret string  `yaml:"secret"`
	Name   string  `yaml:"name"`
	Domain string  `yaml:"domain"`
	Scope  []Scope `yaml:"scope"`
}

type Scope struct {
	ID    string `yaml:"id"`
	Title string `yaml:"title"`
}

type DidServer struct {
	Ip   string `json:"ip"`
	Port string `json:"port"`
}
