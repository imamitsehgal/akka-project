package com.actor.job.launcher

import akka.actor.Actor
import akka.actor.Actor.Receive
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition
import org.apache.spark.launcher.SparkLauncher

import scala.collection.mutable.ListBuffer

/**
  * Created by asehg6 on 3/11/2017.
  */
class JobLauncher extends Actor{
  override def receive: Receive = {

    case records:ConsumerRecords[String,String] =>
      var map =  scala.collection.mutable.Map[TopicPartition,OffsetAndMetadata]()
        import scala.collection.JavaConversions._
        for ( partition:TopicPartition <- asScalaSet(records.partitions)){
          val partitionRecords = records.records(partition)
          val lastOffsetRead = partitionRecords.get(partitionRecords.size()-1).offset()+1
          //Ignoring last offset purposefully for fault tolerance.
          map += partition -> new OffsetAndMetadata(lastOffsetRead)
        }

      Launcher.launchSparkJob()
      println("Job Launched")
        sender ! CommitRecords(Map(map.toSeq:_*))
    case x => println(s"Unknown message received ${x}")
  }


}

case class CommitRecords(partitionOffsets : Map[TopicPartition,OffsetAndMetadata])