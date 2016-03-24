package calculator

import spray.json._

object CalculusJsonProtocol extends DefaultJsonProtocol {
  implicit val okFormat = jsonFormat2(Ok)
  implicit val errorFormat = jsonFormat2(Error)
}

// Case classes for making JSON response to OK and Error messages
case class Ok(error: String, result: Double)
case class Error(error: String, message: String)