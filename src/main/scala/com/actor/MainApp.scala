package com.actor


import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import com.actor.consumer.ConsumerActor
import com.actor.job.launcher.{JobLauncher, Launcher}

import scala.concurrent.duration.Duration

/**
  * Created by asehg6 on 3/11/2017.
  */
object MainApp extends App{

  val system = ActorSystem("JobLauncherSystem")
  val consumerActor = system.actorOf(Props.create(classOf[ConsumerActor]),"ConsumerActor")
  import system.dispatcher
  system.scheduler.schedule(Duration.Zero,Duration.create(1,TimeUnit.MINUTES),consumerActor,"POLL")



}
