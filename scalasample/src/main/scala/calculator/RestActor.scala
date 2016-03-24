package calculator

import java.lang.Character
import java.lang.String
import java.util.Base64
import java.nio.charset.StandardCharsets

import scala.collection.mutable._

import akka.actor._
//import akka.event.slf4j.SLF4JLogging
//import akka.actor.Props

import spray.routing.{HttpService, RequestContext}
import spray.routing.directives.CachingDirectives
import spray.can.server.Stats
import spray.can.Http
import spray.httpx.marshalling.Marshaller
import spray.httpx.encoding.Gzip
import spray.util._
import spray.http._
import CalculusJsonProtocol._
//import HttpMethods._

import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller

class CalculatorServiceActor extends Actor with RestService
{
	def actorRefFactory = context
	def receive = runRoute(restRoute)
}

trait RestService extends HttpService {
	implicit def executionContext = actorRefFactory.dispatcher
	implicit val shuttingYard: ShuntingYard = new ShuntingYard
  	val restRoute = {
  		get
  		{
  			path("calculus")
  			{		
  				parameters('query, 'plain.?) { (query, plain) =>
  				try 
  				{
  					if(query.isEmpty)
  					{
  						throw new Exception("query parameter empty")
  					}
  					println("Encoded: " + query)
				  	val decoded = decodeString(query).replaceAll(" ", "")
				  	var resultStack = new Stack[Double]
				  	val postfix = shuttingYard.toPostfix(decoded)
				  	doRPN(postfix, resultStack)
				  	//println("Result: " + resultStack.top)
	  				complete { Ok("false", resultStack.top) }
  				} catch {
  				  		case e: Exception => {
  				  			complete {
  				  				Error("true", e.getMessage())
  				  			}
  				  		}		 
  					}
  				}
  			} ~ get {
  					complete {
  						Error("true", "Unknown path")
  				}
  			}
  		}
  	}

  	// Calculates result from give reverse polish notation stack
  	private def doRPN(postfix: Stack[String], stack: Stack[Double])
	  {
	    println("Postfix: " + postfix)
	    for (inputStr <- postfix) {
	      if (isAllDigits(inputStr)) {
	        parseDouble(inputStr) match {
	          case Some(i) => stack.push(i)
	          case None => throw new Exception("Parse double failed") 
	        }
	      } 
	      else {
	        inputStr match {
	          case "+" => calculate(stack, _ + _)
	          case "-" => calculate(stack, _ - _)
	          case "*" => calculate(stack, _ * _)
	          case "/" => calculate(stack, _ / _)
	          case _ => throw new Exception("Unknown postfix operator: " + inputStr)
	        }
	        
	      }
	    }
	  }

  	private def calculate(stack: Stack[Double], operatiton:(Double, Double) => Double) = 
  	{
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

