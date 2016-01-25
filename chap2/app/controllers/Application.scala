package controllers

import play.api.mvc.{Action, Controller}

class Application extends Controller {

  def index = Action {
    Redirect(routes.Products.list())
  }
}