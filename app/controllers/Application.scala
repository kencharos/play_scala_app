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

import scala.concurrent.Future

case class LoginReq(id: String, password:String)

// scala での DIはコンストラクタが推奨される。
class Application @Inject()(loginService:LoginService) extends Controller {

  // 認証処理
  case class NeedAuthenticate[A](action: Action[A]) extends Action[A] {

    def apply(request: Request[A]): Future[Result] = {
      import scala.concurrent.ExecutionContext.Implicits.global
      request.session.get("auser") match {
        case Some(_) => action(request)
        case _ => Future(Redirect(routes.Application.login()))
      }
    }

    lazy val parser = action.parser
  }

  // form定義
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
          // scala版ではセッションはresultに付与する形式
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
  // 認証が必要。Actionにネストさせるのが少しダサい。
  // filterやActionBuilderによるAction合成の方がいいかも。
  def welcome = NeedAuthenticate {
    Action { implicit request =>
      val notes = List("This is play scala..")

      Ok(views.html.welcome(notes))
    }
  }
  // AJAX リクエスト
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
