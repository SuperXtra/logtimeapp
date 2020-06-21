package utils

import slick.jdbc.{PositionedParameters, SQLActionBuilder, SetParameter}

trait SlickPlainSqlActionBuilderSyntax {
  implicit class SlickPlainSqlActionBuilderSyntax(a: SQLActionBuilder) {
    def ++(b: SQLActionBuilder): SQLActionBuilder = concat(a, b)

    def opt[T](o: Option[T])(f: T => SQLActionBuilder): SQLActionBuilder = o.fold(a)(f andThen ++)

    private def concat(a: SQLActionBuilder, b: SQLActionBuilder): SQLActionBuilder = {
      SQLActionBuilder(a.queryParts ++ b.queryParts, new SetParameter[Unit] {
        def apply(p: Unit, pp: PositionedParameters): Unit = {
          a.unitPConv.apply(p, pp)
          b.unitPConv.apply(p, pp)
        }
      })
    }
  }

}