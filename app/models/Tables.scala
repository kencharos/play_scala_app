package models

import com.google.inject.{Singleton, Inject}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future


case class User(id:String, password:String)

@Singleton()
class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Users = TableQuery[UsersTable]

  def find(id:String):Future[Seq[User]] = {
    db.run(Users.filter(_.id === id).result)
  }

  private class UsersTable(tag: Tag) extends Table[User](tag, "T_USER") {
    def id = column[String]("ID", O.PrimaryKey)

    def password = column[String]("PASSWORD")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, password) <>(User.tupled, User.unapply _)
  }
}

