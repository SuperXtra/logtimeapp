package config

final case class AuthConfig(
                       secretKey: String,
                       algorithm: String,
                       tokenExpiryPeriodInMinutes: Int
                     )
