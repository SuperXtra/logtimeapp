package config

case class DatabaseConfig(
                          driver: String,
                          url: String,
                          userName: String,
                          password: String
                         )