package controllers

import com.google.inject.Inject
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.routing.JavaScriptReverseRouter
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import services.LoginService

case class LoginReq(id: String, password:String)

class Application @Inject()(loginService:LoginService) extends Controller {


  val loginForm: Form[LoginReq] = Form(
    mapping(
      "id" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginReq.apply)(LoginReq.unapply)
  )

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def doLogin = Action { implicit request =>
    loginForm.bindFromRequest.fold (
      formWithErrors => {
        BadRequest(views.html.login(formWithErrors))
      },
      data => {
        if (loginService.login(data.id, data.password)) {
          Redirect(routes.Application.welcome()).withSession("auser" -> data.id)
        } else {
          BadRequest(views.html.login(loginForm.bindFromRequest.withError("id", "invalid id or password")))
        }
      }
    )
  }

  def logout = Action{ implicit request =>
    Redirect(routes.Application.login()).withNewSession
  }

  def welcome = Action { implicit request =>
    val notes = List("This is play scala..")

    Ok(views.html.welcome(notes))
  }

  def tryLogin = Action(BodyParsers.parse.json) { implicit request =>
    loginForm.bind(request.body).fold(
      errorForm => {
        BadRequest(errorForm.errorsAsJson)
      },
      data => {
        if (loginService.login(data.id, data.password)) {
          Ok("OK. valid user")
        } else {
          BadRequest("NG. invalid user")
        }
      }
    )
  }

  def jsRoutes = Action {implicit request =>
      Ok(
        JavaScriptReverseRouter("jsRoutes")(
          routes.javascript.Application.tryLogin
        )
      ).as("text/javascript")
  }

}
