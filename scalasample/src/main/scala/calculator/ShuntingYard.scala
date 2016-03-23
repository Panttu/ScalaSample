package calculator

import scala.collection.mutable.Stack

class ShuntingYard() {
	
  var result = new Stack[String]
  var stack = new Stack[String]

  def toPostfix(input: String):Stack[String] =
  {
  	println("toPostfix: " + input)
    var isNumber : Boolean = false
    for(char <- input)
    {
    	println("Token: " + char)
      if(char.isDigit)
      {
      	// if this and last token was number, merges the last char and this to new number
        if(isNumber)
        {
          val temp = result.pop + char
          result.push(temp)
        }
        else
        {
          result.push(char.toString)
          isNumber = true
        }
      }
      else
      {
        char match {
          case '+' => precedence(1, "+")
          case '-' => precedence(1, "-")
          case '*' => precedence(2, "*")
          case '/' => precedence(2, "/")
          case '(' => stack.push(char.toString)
          case ')' => precedence(3, ")")
          case _ => None
        } 
        isNumber = false
      }
    }
    while(!stack.isEmpty) { result.push(stack.pop)}
    println(result)
    // Turns result around to right order
    while(!result.isEmpty) {stack.push(result.pop)}
    return stack
  }

  private def precedence(priority: Int, operator:String): Boolean = {
  	try { 

  	//println("precedence: " + operator)
  	if(!stack.isEmpty)
  	{
	  	while(priority == 1 && !stack.isEmpty && (stack.top == "+" || stack.top == "-" || stack.top == "*" || stack.top == "/"))
	  	{
	  		result.push(stack.pop)
	  	}
	  	while(priority == 2 && !stack.isEmpty && (stack.top == "*" || stack.top == "/"))
	  	{
	  		result.push(stack.pop)
	  	}
	  	while(priority == 3 && !stack.isEmpty && stack.top != "(")
	  	{
	  		result.push(stack.pop)
	  	}
	  	// Removes pharentehis from stack
	  	if(priority == 3)
	  	{ 
	  		stack.pop 
	  		return true
	  	}
  	}
  	stack.push(operator)
  	println("stack: " + stack)
  	println("result: " + result)
  	return true
  	  	  // ...
  	} catch {
  	  case e: Exception => {
  	  	println("Exception: " + e)
  	  	return false
  		}
  	}
  }


}