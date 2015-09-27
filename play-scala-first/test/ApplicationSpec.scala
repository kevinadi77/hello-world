import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Your new application is ready.")
    }

    "create new div with default content using REST" in new WithApplication {
      val newdiv = route(FakeRequest(GET,"/div")).get
      status(newdiv) must equalTo(OK)
      contentType(newdiv) must beSome.which(_ == "text/html")
      contentAsString(newdiv) must contain ("div default message")
    }

    "create new div with specific message using REST" in new WithApplication {
      val newdiv = route(FakeRequest(GET,"/div?msg=specificmessage")).get
      status(newdiv) must equalTo(OK)
      contentType(newdiv) must beSome.which(_ == "text/html")
      contentAsString(newdiv) must contain ("specificmessage")
    }

    "output test database value using REST" in new WithApplication {
      val dbpage = route(FakeRequest(GET,"/db")).get
      status(dbpage) must equalTo(OK)
      contentType(dbpage) must beSome.which(_ == "text/plain")
      contentAsString(dbpage) must contain ("DB IS ALIVE")
    }
  }
}
