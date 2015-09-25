package controllers

import play.api._
import play.api.mvc._

object Fixed extends Controller {

    def fixedmessage: String = {
        (1 to 10) map (x => "fixed message object " + x + "\n") mkString
    }

}


class TestApp extends Controller {
  def fixedmessage = Action {
    // Ok(controllers.Fixed.fixedmessage)
    Ok((1 to 10) map (x => "fixed message class " + x + "\n") mkString)
  }
}