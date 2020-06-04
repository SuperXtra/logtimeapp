package config

final case class DatabaseConfig(
                          driver: String,
                          url: String,
                          userName: String,
                          password: String
                         )