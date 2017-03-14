package edu.sample

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

/**
  * Created by asehg6 on 2/25/2017.
  */
object MainClass extends App{

  implicit val system = ActorSystem("MySystem")
  val myActor = ActorSystem("MySystem").actorOf(Props(classOf[MyActor]))

  myActor! "Ping"

  implicit val materializre = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route :Route={
    path("health"){
      get{
        complete(StatusCodes.OK,"Everything is great")
      }
    }
  }
  val host = "localhost"
  val port = 8090
  val serverFuture = Http().bindAndHandle(interface =host ,port=port,handler = route)
  println(s"Waiting for requests at http://$host:$port/...\nHit RETURN to terminate")
  Console.readLine()

  //Shutdown
  serverFuture.flatMap(_.unbind())
  system.shutdown()
}
