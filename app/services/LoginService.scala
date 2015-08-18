package services

import com.google.inject.{Inject, ImplementedBy}
import models.UserDao
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
/**
 * Created by kentaro.maeda on 2015/08/17.
 */
@ImplementedBy(classOf[DBLoginService])
trait LoginService {
  def login(id:String, rawPassword:String):Boolean
}

class  DBLoginService @Inject() (encrypter: PasswordEncrypter, userDao: UserDao) extends LoginService {
  override def login(id: String, rawPassword: String): Boolean = {
    val user = Await.result(userDao.find(id).map(seq =>{println("map;" + seq.size); seq.headOption}), 5 seconds)
    user.exists(_.password == encrypter.encrypt(rawPassword))

  }
}