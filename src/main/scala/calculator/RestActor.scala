package calculator

import java.lang.Character
import java.lang.String
import java.util.Base64
import java.nio.charset.StandardCharsets
import scala.collection.mutable._

//import play.api._

import akka.actor._
import spray.routing.{HttpService, RequestContext}
import spray.can.Http
import spray.httpx.marshalling.Marshaller
import spray.util._
import spray.http._
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
import CalculusJsonProtocol._

class CalculusServiceActor extends Actor with CalculusService
{
	def actorRefFactory = context
	def receive = runRoute(restRoute)
}

trait CalculusService extends HttpService {
	implicit def executionContext = actorRefFactory.dispatcher
	implicit val shuntingYard: ShuntingYard = new ShuntingYard

  // Uses spray's route directives to handle CRUD operations
  val restRoute = {
  		get
  		{
        // If url's path starts with "calculus" goes this route
  			path("calculus")
  			{
          // Gets parameters "query" and optional "plain" from the url to same named variables
  				parameters('query, 'plain.?) { (query, plain) =>
  				try 
  				{
            val result = handleCalculusQuery(query)
            complete { Ok("false", result) }
  				} catch {
  				  		case e: Exception => {
  				  			complete {
  				  				Error("true", e.getMessage())
  				  			}
  				  		}		 
  					}
  				}
  			} ~
        // Handles sprays relaxed raw queries
        // Used because given task query has sub-delims in query
        requestUri { uri =>
          try 
          {
            uri.path.toString() match {
              case "/calculus" => 
              {
                val query = uri.query.toString()
                if(query.length < 6 || query.indexOf('=') == -1)
                {
                  throw new Exception("Unknown parameter: " + query)
                }
                val parameter = query.substring(0, query.indexOf('='))
                parameter match {
                  case "query" => 
                  {
                    try { 
                      val value = query.substring(query.indexOf('=') + 1, query.length)
                      val result = handleCalculusQuery(value)
                      complete { Ok("false", result) }
                    } catch {
                      case e: Exception => {
                        complete {
                          Error("true", e.getMessage())
                        }
                      }
                    }
                  }
                  case _ => complete { Error("true", "Unknown query")}
                }
              }
              case _ => complete { Error("true", "Unknown path")}
            }
          } catch {
                case e: Exception => {
                  complete {
                    Error("true", e.getMessage())
                  }
                }    
            }
        } ~
        // All other oprations are disabled and returs JSON error messages
        get {
  					complete {
  						Error("true", "Unknown path")
  				  }
  			} ~ {
            complete {
              Error("true", "Unknown operation")
            }
        }
  		}
  	}

    // Handles given calculus query and returns calculated value as double
    private def handleCalculusQuery(query: String) : Double =
    {
      if(query.isEmpty)
      {
        throw new Exception("query parameter empty")
      }
      println("Encoded: " + query)
      // Decodes query and removes white space from decoded string
      val decoded = decodeString(query).replaceAll(" ", "")
      var resultStack = new Stack[Double]
      // Turns decoded infix statement to postfix statement
      val postfix = shuntingYard.toPostfix(decoded)
      // Calculates result using reverse polish notation
      doRPN(postfix, resultStack)
      resultStack.pop
    }

  	// Calculates result from give reverse polish notation stack
  	private def doRPN(postfix: Stack[String], stack: Stack[Double])
	  {
	    for (inputStr <- postfix) {
        // Checks if item is digit and pushes to stack
	      if (isAllDigits(inputStr)) {
	        parseDouble(inputStr) match {
	          case Some(i) => stack.push(i)
	          case None => throw new Exception("Parse double failed") 
	        }
	      }
        // Otherwise item should be an operator. Does the given operation for two topmost numbers in RPN-stack 
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

    // Calculates given operation to given numbers and pushes result to given stack
  	private def calculate(stack: Stack[Double], operatiton:(Double, Double) => Double) = 
  	{
      // First in stack is the latter number
    	val latter = stack.pop
    	stack.push(operatiton(stack.pop, latter))
  	}

    // Tries to parse given char to double
  	private def parseDouble(c: Char):Option[Double] = try { Some((c.toString.toDouble)) } 
                                   catch {case e:NumberFormatException => None}
    // Tries to parse given string to double
  	private def parseDouble(s: String):Option[Double] = try { Some((s.toDouble)) } 
                                   catch {case e:NumberFormatException => None}
    // Checs if given string contains only digits                               
  	private def isAllDigits(s: String) = s forall Character.isDigit

    // Decodes given string with Base64 and UTF-8 encoding and returns the result
  	private def decodeString(in: String): String = new String(Base64.getUrlDecoder.decode(in), "UTF-8")

    // Encodes given string with Base64 and UTF-8 encoding and returns the result
  	private def encodeString(in: String): String = Base64.getUrlEncoder.encodeToString(in.getBytes("UTF-8"))

}

