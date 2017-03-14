package edu.sample

import akka.actor.{Actor, ActorSystem, Props, Status}
import akka.actor.Actor.Receive

/**
  * Created by asehg6 on 2/25/2017.
  */
class MyActor extends Actor{
  override def receive: Receive = {
    case "Ping" => {println("yelo")

    }
    case _ => sender() ! Status.Failure(new Exception("unknown message"))
  }

}
