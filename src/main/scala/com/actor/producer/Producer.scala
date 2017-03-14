package com.actor.producer

import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.collection.JavaConversions

/**
  * Created by asehg6 on 3/13/2017.
  */
object Producer extends App{
  val props1 = new Properties();
  props1.put("bootstrap.servers", "localhost:9092");
  props1.put("group.id", "scheduler-group1");
  props1.put("key.deserializer", classOf[StringDeserializer].getName);
  props1.put("value.deserializer", classOf[StringDeserializer].getName);
  props1.put("enable.auto.commit", "false");
  props1.put("auto.offset.reset", "earliest");
  val consumer = new KafkaConsumer[String,String](props1)

  consumer.subscribe(JavaConversions.seqAsJavaList(Seq("event-topic")))

  val props = new Properties();
  props.put("bootstrap.servers", "localhost:9092");
//  props.put("group.id", "scheduler-group");
  props.put("key.serializer", classOf[StringSerializer].getName);
  props.put("value.serializer", classOf[StringSerializer].getName);

  val producer = new KafkaProducer[String,String](props)

  producer.send(new ProducerRecord[String,String]("event-topic","A","Amit"))
  producer.flush()
  producer.close()
  val records = consumer.poll(5000)
  println("DATA IN RECORDS"+records.isEmpty)

}
