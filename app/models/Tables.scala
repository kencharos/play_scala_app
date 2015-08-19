package models

import com.google.inject.{Singleton, Inject}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future


case class User(id:String, password:String)

/**
 * Slickによる DAOの定義
 *
 * Slickではテーブル定義用にTableを継承したクラスと、それを元にした TableQueryを作るのが基本。
 * 作り方は色々あり、必ずしもDAOにする必要は無く、TableQueryを直接公開するのもあり。
 * play 2.4のslickに関するドキュメントは、slickの知識がないとよくわからない。
 * play-slickのサンプルコードの方が充実しているので、そちらを参照した方がいい。
 */
@Singleton()
class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  // クエリ組み立て部品
  private val Users = TableQuery[UsersTable]

  def find(id:String):Future[Option[User]] = {
    // tablequery はモナド的にクエリの組み立てが可能。
    val q = for(u <- Users; if u.id === id)yield(u)
    // resultでクエリをDB操作に変換し、db.rubで実行するが、基本的にFutureを返すことに注意。
    db.run(q.result.headOption)
  }
  // テーブル定義。必須。
  private class UsersTable(tag: Tag) extends Table[User](tag, "T_USER") {
    // IDは大文字である必要がある。
    def id = column[String]("ID", O.PrimaryKey)

    def password = column[String]("PASSWORD")
    // SQL取得結果とcase classへのマッピング
    def * = (id, password) <>(User.tupled, User.unapply _)
  }
}

