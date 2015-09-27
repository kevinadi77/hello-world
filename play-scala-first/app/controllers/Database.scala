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

  def sudoku = Action {
    val querystr = """
    WITH 
    input(sud) AS (
       VALUES('53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79') --Medium
    )
    select * from input;
    """
    DB.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(querystr)
      val outstream = new Iterator[String] {
        def hasNext = rs.next()
        def next() = rs.getString(1)
      }.toStream
      Ok(outstream.mkString)
    } 
  }

}