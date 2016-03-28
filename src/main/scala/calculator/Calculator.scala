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
	
    // Gets service settings from application.conf
  	val myPort = Properties.envOrElse("PORT", servicePort.toString()).toInt
  	val myHost = Properties.envOrElse("HOST", serviceHost)    
  	val restService = system.actorOf(Props[CalculusServiceActor], "calculus-service")

  	// Starts HTTP server with calcuator service actor as a handler
	IO(Http) ! Http.Bind(restService, myHost, myPort)  
}

