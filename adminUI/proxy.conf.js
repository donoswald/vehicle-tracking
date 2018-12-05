const PROXY_CONFIG = [
  {
    context: [
      "/vehicles/*",
      "/vehicles*"
    ]
    ,
    target: "http://localhost:9090",
    secure: false
  }
]

module.exports = PROXY_CONFIG
