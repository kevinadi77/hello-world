package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current

import anorm._
// import anorm.SqlParser._



case class Blah(key:String, value:String)

object Blah {

  val parse = {
    SqlParser.get[String]("KEY") ~
    SqlParser.get[String]("VALUE") map {
      case key ~ value => Blah(key,value)
    }
  }

  def findAll(): Seq[Blah] = {
    DB.withConnection { implicit conn =>
      SQL("SELECT * FROM TEST").as(Blah.parse *)
    }
  }

  def create(blah: Blah): Unit = {
    DB.withConnection { implicit conn =>
      SQL("INSERT INTO TEST (KEY,VALUE) VALUES ({key},{value})")
        .on('key -> blah.key, 'value -> blah.value)
        .executeUpdate()
    }
  }

}



class Database extends Controller {

  DB.withConnection { implicit conn =>
    val createTable = SQL(
      """
      DROP TABLE TEST;
      CREATE TABLE TEST (KEY TEXT, VALUE TEXT)
      """
    ).execute()
  }

  Blah.create(Blah("key1","val1"))
  Blah.create(Blah("key2","val2"))
  Blah.create(Blah("key3","val3"))
  Blah.create(Blah("key4","val4"))
  Blah.create(Blah("key5","val5"))
  Blah.create(Blah("key6","val6"))
  Blah.create(Blah("key7","val7"))


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


  def indexJdbc = Action {
    DB.withConnection { conn =>
      val stmt = conn.createStatement
      // val rs = stmt.executeQuery("SELECT 'DB IS ALIVE' AS COL")
      val rs = stmt.executeQuery("SELECT * FROM TEST")
      val numcols = rs.getMetaData().getColumnCount()
      System.out.println("NUMCOLS: " + numcols)
      val outstream = new Iterator[String] {
        def hasNext = rs.next()
        def next() = "Row:" + rs.getString(1)
      }.toStream
      Ok(outstream.mkString("\n"))
    } 
  }


  def indexAnorm = Action {
    val allBlah = Blah.findAll()
    Ok(allBlah.mkString("\n"))
  }


  def insert(key:String, value:String) = {
    DB.withConnection { implicit conn =>
      val res = SQL(
        """
        INSERT INTO TEST VALUES ({key},{val})
        """
      ).on('key -> key, 'val -> value).executeUpdate()
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