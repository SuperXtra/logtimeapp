package config

case class AuthConfig(
                       secretKey: String,
                       algorithm: String,
                       tokenExpiryPeriodInMinutes: Int
                     )
