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

    "show all database values using REST" in new WithApplication {
      val dbpage = route(FakeRequest(GET,"/db")).get
      status(dbpage) must equalTo(OK)
      contentType(dbpage) must beSome.which(_ == "text/plain")
      contentAsString(dbpage) must contain ("Blah(key1,val1,desc1)")
      contentAsString(dbpage) must contain ("Blah(key2,val2,desc2)")
      contentAsString(dbpage) must contain ("Blah(key3,val3,desc3)")
    }

    "select a specific key using REST" in new WithApplication {
      val dbpage = route(FakeRequest(GET,"/db?key=key7")).get
      status(dbpage) must equalTo(OK)
      contentType(dbpage) must beSome.which(_ == "text/plain")
      contentAsString(dbpage) must contain ("List(Blah(key7,val7,desc7))")
      contentAsString(dbpage) must not contain ("List(Blah(key7,val8,desc8))")
      contentAsString(dbpage) must contain ("""[{"key":"key7","value":"val7","desc":"desc7"}]""")
    }

    "update a specific key" in new WithApplication {
      val dbpage = route(FakeRequest(GET,"/db?key=key7")).get
      status(dbpage) must equalTo(OK)
      contentType(dbpage) must beSome.which(_ == "text/plain")
      contentAsString(dbpage) must contain ("Blah(key7,valnew,descnew)")
    }
  }
}
