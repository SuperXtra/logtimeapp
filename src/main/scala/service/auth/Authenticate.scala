package service.auth

import java.util.concurrent.TimeUnit

import cats.implicits._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{complete, optionalHeaderValueByName, provide}
import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.typesafe.config.ConfigFactory
import config.AuthConfig
import error.AuthenticationNotSuccessful
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._

class Authenticate() {

  //TODO configuration params

  val config = ConfigFactory.load("auth-configuration.conf")
  val authConfig: AuthConfig = ConfigSource.fromConfig(config).loadOrThrow[AuthConfig]


  private val secretKey = authConfig.secretKey
  private val header = JwtHeader(authConfig.algorithm)
  private val tokenExpiryPeriodInMinutes = authConfig.tokenExpiryPeriodInMinutes

  def generateToken(uuid: String): String = {
    val claims = JwtClaimsSet(
      Map(
        "uuid" -> uuid,
        "expiredAt" -> (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenExpiryPeriodInMinutes))
      )
    )
    JsonWebToken(header, claims, secretKey)
  }

  def authenticated: Directive1[Map[String, Any]] = {

    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) =>
        token.split(" ")(0) match {
          case token if isTokenExpired(token) => complete(StatusCodes.Unauthorized -> AuthenticationNotSuccessful(detailErrorMessage = "Session expired"))

          case token if JsonWebToken.validate(token, secretKey) => provide(getClaims(token))
          case _ => complete(StatusCodes.Unauthorized -> AuthenticationNotSuccessful(detailErrorMessage = "Invalid Token"))
        }
      case None => complete(StatusCodes.Unauthorized -> AuthenticationNotSuccessful(detailErrorMessage = "Token not Provided"))
    }
  }

  private def isTokenExpired(jwt: String): Boolean =
    getClaims(jwt).get("expiredAt").exists(_.toLong < System.currentTimeMillis())

  private def getClaims(jwt: String): Map[String, String] =
    JsonWebToken.unapply(jwt) match {
      case Some(value) => value._2.asSimpleMap.getOrElse(Map.empty[String, String])
      case None => Map.empty[String, String]

    }

  //TODO
}
