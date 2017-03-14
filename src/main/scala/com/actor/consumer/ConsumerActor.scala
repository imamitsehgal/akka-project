package com.actor.consumer

import java.util.Properties

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import com.actor.job.launcher.{CommitRecords, JobLauncher}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConversions


/**
  * Created by asehg6 on 3/11/2017.
  */
class ConsumerActor extends Actor{
  val props = new Properties();
  props.put("bootstrap.servers", "localhost:9092");
  props.put("group.id", "scheduler-group");
  props.put("key.deserializer", classOf[StringDeserializer].getName);
  props.put("value.deserializer", classOf[StringDeserializer].getName);
  props.put("enable.auto.commit", "false");
  props.put("auto.offset.reset", "earliest");
  val consumer = new KafkaConsumer[String,String](props)

  consumer.subscribe(JavaConversions.seqAsJavaList(Seq("event-topic")))

  override def receive: Receive = {
    case "POLL" => {
      val records = consumer.poll(5000)
      println("DATA IN RECORDS"+records.count())
      if (!records.isEmpty) {
        val jobLauncher = this.context.actorOf(Props.create(classOf[JobLauncher]), s"JobLauncherActor${System.currentTimeMillis()}")
        jobLauncher ! records
      }
    }
    case s :CommitRecords =>{
      import JavaConversions._
      consumer.commitSync(JavaConversions.mapAsJavaMap(s.partitionOffsets))
    }

    case x => println(s"Unknown message received ${x}")
  }
}
