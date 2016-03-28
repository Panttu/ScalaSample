package calculator

import akka.io.IO
import akka.actor.ActorSystem
import akka.actor.Props
import spray.can.Http
import scala.io.StdIn
import util.Properties
import config.Configuration

object Boot extends App with Configuration{

	implicit val system = ActorSystem("calculus")
    // for Heroku compatibility
  	val myPort = Properties.envOrElse("PORT", "8081").toInt
  	val myHost = Properties.envOrElse("HOST", "0.0.0.0")  
  	println("config: " + myHost + " port " + myPort)
  
  	val restService = system.actorOf(Props[CalculusServiceActor], "calculus-service")

  	// Starts HTTP server with calcuator service actor as a handler
	IO(Http) ! Http.Bind(restService, myHost, myPort)  
}

