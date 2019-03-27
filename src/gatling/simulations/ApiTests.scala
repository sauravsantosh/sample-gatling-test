import _root_.io.gatling.core.scenario.Simulation
import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._

class ApiTests extends Simulation {

  // Get the baseURL from application.conf based on the command-line 'environment' variable.
  val baseUrl = ConfigFactory.load("application.conf").getString(System.getProperty("environment"))
  System.out.println("Base URL:" + baseUrl)


  //Get the users and rampup parameters from configuration file, these will be pickedup if not passed via command line
  val noOfUsers: Int = ConfigFactory.load("application.conf").getInt("noOfUsers")
  val rampUp: Int = ConfigFactory.load("application.conf").getInt("rampUp")

  // Define the http configuration.
  val proxyHost = ConfigFactory.load().getString("https.proxyHost")
  val proxyPort: Int = ConfigFactory.load().getInt("https.proxyPort")

  val httpConf = http.baseUrl(baseUrl)

  // Define the scenarios

  // Basic scenario defination with PageObject pattern
  val scenario1 = scenario("Scenario Description 1")
    // repeat provides the ability to run the request several times at specified intervals
    .repeat(1) {
    exec(
      http("get/bbcPage")
        .get("news")
        .check(status.is(200), responseTimeInMillis.lt(4000)))
      .pause(1 seconds)
  }

  // This format is inline with PageObject pattern with Selenium which can be reused
  object loadPage {

    val loadLoginPage = scenario("Scenario Description 2")
      // repeat provides the ability to run the request several times at specified intervals
      .repeat(1) {
      exec(
        http("load/bbcPage")
          .get("news")
          .check(status.is(200), responseTimeInMillis.lt(4000)))
        .pause(1 seconds)
    }
  }


  val users = scenario("Users").exec(scenario1).exec(loadPage.loadLoginPage)

  // Simulating Scenario execution injecting users

  setUp(
    users.inject(
      rampUsers(Integer.getInteger("users", noOfUsers)) during (Integer
        .getInteger("ramp", rampUp) seconds))
  ).assertions(
    global.responseTime.max.lt(4000),
    global.successfulRequests.percent.gt(95),
    global.failedRequests.count.is(0)
  )
    .protocols(httpConf)
}