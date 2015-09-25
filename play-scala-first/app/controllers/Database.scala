package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current

class Database extends Controller {

  def index = Action {
    var outstring = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT 1 AS COL")
      while (rs.next()) {
        outstring += rs.getString("COL")
      }
    } finally {
      conn.close
    }
    Ok(outstring)
  }

  def indexautoclose = Action {
    var outstring = ""
    DB.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT 'DB IS ALIVE' AS COL")
      val outstream = new Iterator[String] {
        def hasNext = rs.next()
        def next() = rs.getString(1)
      }.toStream
      Ok(outstream.mkString)
    }
    
  }

}