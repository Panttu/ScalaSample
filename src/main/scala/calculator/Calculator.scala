package calculator

import akka.io.IO
import akka.actor.ActorSystem
import akka.actor.Props
import spray.can.Http
import scala.io.StdIn
import util.Properties
import config.Configuration

object Boot extends App with Configuration{

	implicit val system = ActorSystem("calculator")
    // for Heroku compatibility
  val myPort = Properties.envOrElse("PORT", "8081").toInt 
  println("config: " + serviceHost + " port " + myPort)
  
  val restService = system.actorOf(Props[CalculatorServiceActor], "calculator-service")
  println("service: " + restService)

  // Starts HTTP server with calcuator service actor as a handler
	IO(Http) ! Http.Bind(restService, "0.0.0.0", myPort)  
}

