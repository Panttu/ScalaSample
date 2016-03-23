package calculator

import akka.io.IO
import akka.actor.ActorSystem
import akka.actor.Props
import spray.can.Http
import akka.util.Timeout
import scala.concurrent.duration._
import scala.collection.mutable._
import scala.io.StdIn
import java.lang.Character
import java.lang.String
import java.util.Base64
import java.nio.charset.StandardCharsets
import config.Configuration

object Boot extends App with Configuration{
  /*
	implicit val system = ActorSystem("calculatorService")
  println("config: " + serviceHost + " port " + servicePort)
	val restService = system.actorOf(Props[CalculatorServiceActor], "calculatorEndpoint")
  println("service: " + restService)
  restService ! "test"
  restService ! "test2"
  */
  // Starts HTTP server with calcuator service actor as a handler
	//IO(Http) ! Http.Bind(restService, serviceHost, servicePort)
  implicit val shuttingYard: ShuntingYard = new ShuntingYard
  var stack = new Stack[Double]
  val encoded = "MiAqICgyMy8oMyozKSktIDIzICogKDIqMyk="
  println("Encoded: " + encoded)
  println(decodeString(encoded))
  val decoded = decodeString(encoded)
  val trimmed = decoded.replaceAll(" ", "")
  val postfix = shuttingYard.toPostfix(trimmed)
  readCalculation(postfix)
  if(!stack.isEmpty)
  {
    println(stack + " result: " + stack.top)
  }
  //readCalculation("2 * (23/(3*3)) - 23 * (2*3)")
  println("Hit any key to exit.")
  val result = StdIn.readLine()
  println("Shutting down...")
  //system.shutdown()
  println("Shut down.")

  def readCalculation(postfix: Stack[String])
  {
    println("Postfix: " + postfix)
    for (inputStr <- postfix) {
      println("Token: " + inputStr) 
      if (isAllDigits(inputStr)) {
        parseDouble(inputStr) match {
          case Some(i) => stack.push(i)
          case None => println("Parse failed") 
        }
      } 
      else {
        println("calculate: " + stack) 
        inputStr match {
          case "+" => calculate(_ + _)
          case "-" => calculate(_ - _)
          case "*" => calculate(_ * _)
          case "/" => calculate(_ / _)
          case _ => println("Tuntematon: " + inputStr)

        }
        println("ResultStack: " + stack) 
        
        
      }
    }
  }

  private def calculate(operatiton:(Double, Double) => Double) = {
    //println("Calculation:" + stack.top + " " + operatiton)
    val last = stack.pop
    stack.push(operatiton(stack.pop, last))
  }

  private def parseDouble(c: Char):Option[Double] = try { Some((c.toString.toDouble)) } 
                                   catch {case e:NumberFormatException => None}
  private def parseDouble(s: String):Option[Double] = try { Some((s.toDouble)) } 
                                   catch {case e:NumberFormatException => None}
  private def isAllDigits(s: String) = s forall Character.isDigit

  private def decodeString(in: String): String = new String(Base64.getUrlDecoder.decode(in), "UTF-8")
  private def encodeString(in: String): String = Base64.getUrlEncoder.encodeToString(in.getBytes("UTF-8"))
}

