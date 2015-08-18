package services

import com.google.inject.ImplementedBy

/**
 * Created by kentaro.maeda on 2015/08/17.
 */
@ImplementedBy(classOf[DefaultPasswordEncrypter])
trait PasswordEncrypter {
  def encrypt(raw:String):String
}
