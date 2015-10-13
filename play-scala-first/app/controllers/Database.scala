package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.functional.syntax._

import anorm._
// import anorm.SqlParser._ /* commented to make the parser .get method explicit */


/*
Doc:
https://www.playframework.com/documentation/2.4.x/ScalaAnorm

Examples:
https://github.com/dustingetz/orm-deep-dive/blob/master/app/models/Environment.scala
*/


case class Blah(key:String, value:String, desc:String)

object Blah {

  /*
  Parse SQL query output into structure Blah
  */
  private val simple = {
    SqlParser.get[String]("KEY") ~
    SqlParser.get[String]("VALUE") ~
    SqlParser.get[String]("DESC") map {
      case key ~ value ~ desc => Blah(key,value,desc)
    }
  }

  /*
  Get all Blah from the database, returns Blah array
  */
  def findAll(): List[Blah] = {
    DB.withConnection { implicit conn =>
      SQL(
        """
        SELECT TEST.KEY,TEST.VALUE,DESC.DESC
        FROM TEST
        JOIN DESC ON DESC.KEY = TEST.KEY
        ORDER BY TEST.KEY
        """
      ).as(Blah.simple *)
    }
  }

  /*
  Get some Blah with a specific key
  */
  def find(key:String): List[Blah] = {
    DB.withConnection { implicit conn =>
      SQL(
        """
        SELECT TEST.KEY,TEST.VALUE,DESC.DESC
        FROM TEST
        JOIN DESC ON DESC.KEY = TEST.KEY
        WHERE TEST.KEY = {key}
        """
      ).on('key -> key).as(Blah.simple *)
    }
  }

  /*
  Insert a new Blah into the database
  */
  def create(blah: Blah): Unit = {
    DB.withTransaction { implicit conn =>

      SQL(
        """
        INSERT INTO TEST (KEY,VALUE) VALUES ({key},{value})
        """
      ).on('key -> blah.key, 'value -> blah.value)
       .executeUpdate()

      SQL(
        """
        INSERT INTO DESC (KEY,DESC) VALUES ({key},{desc})
        """
      ).on('key -> blah.key, 'desc -> blah.desc)
       .executeUpdate()
    }
  }

  /*
  Update existing Blah
  */
  def update(blah: Blah): Unit = {
    DB.withTransaction { implicit conn =>

      SQL(
        """
        UPDATE TEST SET VALUE = {value} WHERE KEY = {key}
        """
      ).on('key -> blah.key, 'value -> blah.value)
       .executeUpdate()

      SQL(
        """
        UPDATE DESC SET DESC = {desc} WHERE KEY = {key}
        """
      ).on('key -> blah.key, 'desc -> blah.desc)
       .executeUpdate()
    }
  }

  /*
  Implicit to change Blah into Json
  Usage: Json.toJson(blah) -> returns JsValue
         Json.toJson(blah).toString -> returns the Json string
  */
  implicit val blahWrites = new Writes[Blah] {
    def writes(blah: Blah) = Json.obj(
      "key" -> blah.key,
      "value" -> blah.value,
      "desc" -> blah.desc
    )
  }

  /*
  Implicit to change Json to Blah
  Usage:  blahjson.as[Blah] -> uses the implicit reader without validation
          blahjson.asOpt[Blah] -> cast to option Blah without validation
          blahjson.validate[Blah].get -> cast to Blah with validation
          (blahjson \ "key").as[String] -> cast into string without validation
          (blahjson \ "key").asOpt[String] -> cast into option string without validation
          (blahjson \ "key").validate[String] -> validates the key is a string
          (blahjson \ "key").validate[String].getOrElse("default_key") -> get the validated key
  */
  implicit val blahReads: Reads[Blah] = (
    (JsPath \ "key").read[String] and
    (JsPath \ "value").read[String] and
    (JsPath \ "desc").read[String]
  )(Blah.apply _)

}



class Database extends Controller {

  /*
  Constructor, create table & insert some values
  */
  DB.withConnection { implicit conn =>
    val createTable = SQL(
      """
      DROP TABLE IF EXISTS TEST;
      CREATE TABLE TEST (KEY TEXT, VALUE TEXT);
      DROP TABLE IF EXISTS DESC;
      CREATE TABLE DESC (KEY TEXT, DESC TEXT);
      """
    ).execute()
  }
  Blah.create(Blah("key1","val1","desc1"))
  Blah.create(Blah("key2","val2","desc2"))
  Blah.create(Blah("key3","val3","desc3"))
  Blah.create(Blah("key4","val4","desc4"))
  Blah.create(Blah("key5","val5","desc5"))
  Blah.create(Blah("key6","val6","desc6"))
  Blah.create(Blah("key7","val7","desc7"))

  /*
  Check: Jdbc functionality
  */
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

  /*
  Check: Jdbc query method
  */
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

  /*
  Using Anorm
  */
  def indexAnorm(key: String) = Action {

    val allBlah = Blah.findAll()
    val allBlahJson = Json.toJson(allBlah)

    val corruptJson = Json.parse("""
    [ {
        "key" : "keyX",
        "value" : "valX"
      }, {
        "key" : "keyY",
        "value" : "valY",
        "desc" : "descY"
      }, {
        "key" : "keyZ",
        "value" : "valZ",
        "desc" : "descZ"
      } ]
    """)

    Ok(
        "\n# Anorm SELECT * result as Blah case class:\n"
      + Blah.findAll.mkString("\n") + "\n"

      + "\n# List head as Json:\n"
      + Json.toJson(Blah.findAll.head) + "\n"

      + "\n# SELECT * result as Json:\n"
      + Json.prettyPrint(allBlahJson) + "\n"

      + "\n# From Json back into List[Blah]:\n"
      + allBlahJson.asOpt[List[Blah]] + "\n"

      + "\n# Corrupt Json:\n"
      + "Json      : " + corruptJson + "\n"
      + "All valid : " + corruptJson.validate[List[Blah]] + "\n"
      + "Head valid: " + corruptJson.head.validate[Blah] + "\n"
      + "Tail valid: " + corruptJson.tail.validate[List[Blah]] + "\n"
      + "All option: " + corruptJson.asOpt[List[Blah]] + "\n"
      + "Head opt  : " + corruptJson.head.asOpt[Blah] + "\n"
      + "Tail opt  : " + corruptJson.tail.asOpt[List[Blah]] + "\n"

      + "\n# Find a specific key ("+key+"):\n"
      + "Some key Anorm       : " + Blah.find(key) + "\n"
      + "Some key Json        : " + Json.toJson(Blah.find(key)) + "\n"
      + "Nonexistent key Anorm: " + Blah.find("XXX") + "\n"
      + "Nonexistent key Json : " + Json.toJson(Blah.find("XXX")) + "\n"

      + "\n#Update a specific key ("+key+"):\n"
      + "Before  : " + Blah.find(key) + "\n"
      + "Updating: " + Blah.update(Blah(key,"valnew","descnew")) + "\n"
      + "After   : " + Blah.find(key) + "\n"
    )
  }

  /*
  REST insert
  */
  def insertREST(key: String, value: String, desc: String) = Action {
    val data = Blah(key,value,desc)
    Blah.create(data)
    Ok("Inserted " + data)
  }

  /*
  REST update
  */
  def updateREST(key: String, value: String, desc: String) = Action {
    val olddata = Blah.find(key)
    val newdata = Blah(key,value,desc)
    Blah.update(newdata)
    Ok("Key: " + key + "  Old: " + olddata + "  New: " + newdata)
  }

  /*
  Not needed anymore, can use Blah.create() instead
  */
  def insert(key:String, value:String) = {
    DB.withConnection { implicit conn =>
      val res = SQL(
        """
        INSERT INTO TEST VALUES ({key},{val})
        """
      ).on('key -> key, 'val -> value).executeUpdate()
    }
  }

  /*
  Check SQL feature with recursive CTE
  */
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