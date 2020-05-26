val idsList =  List("1", "2", "3", "4")

idsList.map(ids => s"AND p.name IN (${ids.mkString(",")}")

val query2 = idsList match {
  case Nil => ""
  case _ :: _ => "AND p.name IN (" + idsList.map(id => id.mkString(", ")) + ")"
  }

val query3 = idsList.foldLeft("AND p.name IN (")((a,b) => a.concat(", "+ b) )