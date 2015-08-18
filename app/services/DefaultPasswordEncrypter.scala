package services

import java.security.MessageDigest

import com.google.inject.Singleton


/**
 * Created by kentaro.maeda on 2015/08/17.
 */
@Singleton
class DefaultPasswordEncrypter extends PasswordEncrypter {

  override def encrypt(raw:String):String = {
    val md = MessageDigest.getInstance("SHA-256")
    md.digest(raw.getBytes).map(b => "%02x".format(b & 0xff)).mkString
  }

}
