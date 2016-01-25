/**
  * Created by abhishek on 21-01-2016.
  */

package controllers

import play.api.mvc.{Action, Controller}
/** A play.api.mvc.Action is basically a function(play.api.mvc.Request => play.api.mvc.Result)
  * where Result value represents the HTTP response to send to the web client;
  * unless specified Action values use a default 'Any content body parser'
  */
import models.Product
//import play.api.data._
//import play.api.data.Forms._
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.{I18nSupport, MessagesApi, Messages}

//a play.api.data.Form object to help us move data between the web browser and the server-side application

import play.api.mvc.Flash
/** Like the session scope, flash session to keep data, related to the client, outside of the context of a single
  * request. The difference is that the FLASH SCOPE IS KEPT FOR THE NEXT REQUEST ONLY, after which it’s removed and
  * Flash cookie is not signed, making it possible for the user to modify it. Flash scope should only be used to
  * transport success/error messages on simple non-Ajax applications
  * If the flash scope isn’t stored on the server, each one of a client’s requests can be handled by a different server,
  * without having to synchronize between servers.
  * Flash scope value in view(html) is retrieved by an implicit Flash parameter  based on the implicit request in Action
  */

import javax.inject._

class Products @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  /** A Controller is a singleton object that generates Action values
    * It coordinates the code that provides data (models.Product) and the template that renders this data as HTML
    * (list.scala.html and main.scala.html)
    */

  def list = Action { implicit request =>
    //implicit so it can be implicitly used by other APIs that need it:
    val products = Product.findAll
    Ok(views.html.products.list(products))        // /products
  }

  //return type of findByEan is Option[Product or 404error]; on success it is mapped to a page that shows details
  def show(ean: Long) = Action { implicit request =>
    Product.findByEan(ean).map { product =>
      Ok(views.html.products.details(product))    // /products/:ean
    }.getOrElse(NotFound)
  }

  /** The 'Forms' object defines the 'mapping' method which takes the names and constraints of the form, and also
    * takes two functions: an apply function (to create a Product model instance from the given parameters - contents
    * of the form ) and an unapply function (to extract out parameters  and fill the form from an existing Product).
    */
  private val productForm: Form[Product] = Form(
    mapping(
      "ean" -> longNumber.verifying(
        "validation.ean.duplicate", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  )

  def newProduct = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined)
      productForm.bind(request.flash.data)
    else
      productForm
    Ok(views.html.products.editProduct(form))
    //Once we have a form we need to make it available to the template engine by including the form as a
    // parameter to the view template (editProduct.scala.html)
  }

  def save = Action { implicit request =>
    val newProductForm = productForm.bindFromRequest()
    // When data comes in from a POST request, Play will look for formatted values and bind them to a Form object.
    // bindFromRequest method searches the request parameters for ones named after the form’s fields and uses them as
    // those fields’ values
    newProductForm.fold(
    // fold method, which takes two functions: the first (default value) is called if the binding fails, and the second
    // is called if the binding succeeds and transforms the form into the right kind of response i.e. folds multiple
    // possible values into a single value
      hasErrors = { form =>
        Redirect(routes.Products.newProduct()).flashing(Flash(form.data) + ("error" -> Messages("validation.errors")))
        // redirect the user back to the new-product page and Add form data (including the validation errors) to flash
        // scope with an informative message by calling the 'flashing' method in SimpleResult (which is the supertype of
        // what Redirect, Ok and NotFound return)
      },
      success = { newProduct =>
        Product.add(newProduct)
        val message = Messages("products.new.success", newProduct.name)
        Redirect(routes.Products.show(newProduct.ean)).flashing("success" -> message)
        //Redirect after POST -- to prevent duplication of posts is required when using flashing as new cookies will
        // only be available after the redirected HTTP request.
      }
    )
  }

}
