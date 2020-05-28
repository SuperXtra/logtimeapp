package repository.report

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import error.ReportCouldNotBeGenerated
import models.request.ReportRequest
import models.responses.FinalReport
import repository.queries.Report

class Report[F[_]: Sync](tx: Transactor[F]) {

  def apply(projectQuert: ReportRequest, page: Int, pageCount: Int): F[Either[ReportCouldNotBeGenerated.type, List[FinalReport]]] = {
    Report(projectQuert, page, pageCount).transact(tx).attemptSomeSqlState{
      _ => ReportCouldNotBeGenerated
    }
  }

}
