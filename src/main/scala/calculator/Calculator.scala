package calculator

import akka.io.IO
import akka.actor.ActorSystem
import akka.actor.Props
import spray.can.Http
import scala.io.StdIn
import config.Configuration

object Boot extends App with Configuration{

	implicit val system = ActorSystem("calculator")
  println("config: " + serviceHost + " port " + servicePort)
	
  val restService = system.actorOf(Props[CalculatorServiceActor], "calculator-service")
  println("service: " + restService)

  // Starts HTTP server with calcuator service actor as a handler
	IO(Http) ! Http.Bind(restService, serviceHost, servicePort)

  println("Hit any key to exit.")
  val result = StdIn.readLine()
  println("Shutting down...")
  system.shutdown()
  println("Shut down.")
  }

