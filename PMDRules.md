PMD Rules to Ignore

  * OnlyOneReturn just don't deeply nest returns. Make control flow easily understandable
  * MethodArgumentCouldBeFinal unless you're worried about side effects
  * LocalVariableCouldBeFinal unless there is a legitimate reason it needs to be constant
  * AbstractNaming

And whatever else you feel like. PMD is good for cleanup though!