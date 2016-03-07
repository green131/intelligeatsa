package server.app.exceptions;

import play.mvc.Result;

public class ServerResultException extends Exception{
  
  public Result errorResult;
  
  public ServerResultException(Result result){
    errorResult = result;
  }
  
}
