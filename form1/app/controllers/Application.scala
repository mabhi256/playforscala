package controllers

import play.api._
import play.api.mvc._
import models.Customer
import play.api.data._
import play.api.data.Forms._
import javax.inject.Inject
import play.api.i18n._

class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def customerForm = Form(mapping("Customer Name" -> nonEmptyText,
    "Credit Limit" -> number)(Customer.apply)(Customer.unapply))

  def index = Action {
    Ok(views.html.index(customerForm))
  }

  def createCustomer = Action { implicit request =>
    customerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index(formWithErrors)),
      customer => Ok(s"Customer ${customer.name} created successfully"))
  }
}
