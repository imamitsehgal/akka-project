package com.actor.job.launcher

import java.io.File
import java.util.concurrent.CountDownLatch

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.launcher.{SparkAppHandle, SparkLauncher}


/**
  * Created by asehg6 on 3/11/2017.
  */
object Launcher {
  def launchSparkJob() :Boolean={
    import scala.collection.JavaConversions._
    val env = Map(
      "HADOOP_CONF_DIR" -> "C:\\hadoop",
      "YARN_CONF_DIR" -> ""
    )
    val latch = new CountDownLatch(1)
    class Listener(appID:String,latch:CountDownLatch) extends SparkAppHandle.Listener{
      override def infoChanged(handle: SparkAppHandle): Unit = {
        println(s"${appID} INFO:${handle.getState}")
        println(s"${appID} INFO:${handle.getAppId}")
      }

      override def stateChanged(handle: SparkAppHandle): Unit = {
        println(s"${appID} STATE:${handle.getState}")
        println(s"${appID} STATE:${handle.getAppId}")
        if(handle.getState.isFinal){
          latch.countDown()
        }
      }
    }

    val conf = ConfigFactory.parseFile(new File(getClass.getResource("Job.conf").getFile))
    val handler = new SparkLauncher(mapAsJavaMap(env))
      .setSparkHome(conf.getString("workflow.name.jobs.args.SPARK_HOME"))
      .setAppResource(conf.getString("workflow.name.jobs.args.APP_RESOURCE"))
      .setMainClass(conf.getString("workflow.name.jobs.args.mainclass"))
      .setMaster(conf.getString("workflow.name.jobs.args.master"))
      .setConf("spark.app.id", "ID1")
      .setConf("spark.driver.memory", "2g")
      //.setConf("spark.akka.frameSize", "200")
      .setConf("spark.executor.memory", "2g")
      .setConf("spark.executor.instances", "1")
      .setConf("spark.executor.cores", "2")
      .setConf("spark.default.parallelism", "100")
      .setConf("spark.driver.allowMultipleContexts","true")
      .setVerbose(true)
      .startApplication(new Listener(s"ID1-${System.currentTimeMillis()}",latch))
    println(s"Job Launched ${handler.getAppId}")
    latch.await()
    println(s"Job Over${handler.getAppId}")
    true
  }
}
