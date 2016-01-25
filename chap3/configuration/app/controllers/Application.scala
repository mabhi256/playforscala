package controllers

import play.api._
import play.api.mvc._

/**
  * Configuration API—programmatic access
  */

class Application extends Controller {

  import play.api.Play.current      // Import implicit current application instance for access to configuration
  current.configuration.getString("db.default.url").map {
    databaseUrl => Logger.info(databaseUrl)       // databaseUrl is the value of theOption that getString returns
  }

  /*
   * How to check a Boolean configuration property.
   */
  current.configuration.getBoolean("db.default.logStatements").foreach {
    if (_) Logger.info("Logging SQL statements...")
  }

  /**
    * Accessing a subconfiguration
    */
  current.configuration.getConfig("db.default").map {
    databaseConfiguration =>
      databaseConfiguration.getString("driver").map(Logger.info(_))
      databaseConfiguration.getString("url").map(Logger.info(_))
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}
