package service.auth

import java.util.concurrent.TimeUnit
import cats.implicits._
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{complete, optionalHeaderValueByName, provide}
import authentikat.jwt._
import config.AuthConfig
import error._
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._


trait Auth {
  def apply: Directive1[Map[String, Any]]
  def token(uuid: String): String
}

object Auth {
  def apply(authConfig: AuthConfig): Auth = new Auth {

    private val secretKey = authConfig.secretKey
    private val header = JwtHeader(authConfig.algorithm)
    private val tokenExpiryPeriodInMinutes = authConfig.tokenExpiryPeriodInMinutes



    override def apply: Directive1[Map[String, Any]] = {
        optionalHeaderValueByName("Authorization").flatMap {
          case Some(token) =>
            val arrayFromToken = token.split(" ")
            arrayFromToken.length match {
              case 2 => arrayFromToken(1) match {  // 2 represents length of an array with Bearer prefix
                case token if isTokenExpired(token) => complete(MapToErrorResponse.auth(AuthenticationNotSuccessful))
                case token if JsonWebToken.validate(token, secretKey) => provide(getClaims(token))
                case _ => complete(MapToErrorResponse.auth(AuthenticationNotSuccessful ))
              }
              case _ => complete(MapToErrorResponse.auth(AuthenticationNotSuccessfulWithoutBearer ))
            }
          case None => complete(MapToErrorResponse.auth(AuthenticationNotSuccessful ))
        }
    }

    override def token(uuid: String): String = {
      val claims = JwtClaimsSet(
        Map(
          "uuid" -> uuid,
          "expiredAt" -> (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenExpiryPeriodInMinutes))
        )
      )
      JsonWebToken(header, claims, secretKey)
    }

    private def isTokenExpired(jwt: String): Boolean =
      getClaims(jwt).get("expiredAt").exists(_.toLong < System.currentTimeMillis())

    private def getClaims(jwt: String): Map[String, String] =
      JsonWebToken.unapply(jwt) match {
        case Some((_, jwtClaimsSetJValue, _)) => jwtClaimsSetJValue.asSimpleMap.getOrElse(Map.empty[String, String])
        case None => Map.empty[String, String]

      }
  }
}
