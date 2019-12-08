package tao

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.kafka.common.serialization.StringDeserializer

object KafkaConsumer {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val actorMaterializer: Materializer = ActorMaterializer()

    val config = actorSystem.settings.config.getConfig("akka.kafka.consumer")

    val myTopic = config.getString("myTopic")
    val SASL_JAAS_CONFIG = config.getString("sasl.jaas.config")
    val SASL_MECHANISM = config.getString("sasl.mechanism")
    val SECURITY_PROTOCOL = config.getString("security.protocol")
    val SSL_PROTOCOL = config.getString("ssl.protocol")

    println(myTopic)


    val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(actorSystem, new StringDeserializer, new StringDeserializer)
      .withProperties(Map[String, String] (
        "sasl.jaas.config" -> SASL_JAAS_CONFIG,
        "sasl.mechanism" -> SASL_MECHANISM,
        "security.protocol" -> SECURITY_PROTOCOL,
        "ssl.protocol" -> SSL_PROTOCOL
      ))


  val done = Consumer
    .plainSource(consumerSettings, Subscriptions.topics(myTopic))
    //.plainSource(kafkaConsumerSettings, Subscriptions.topics("test"))
    //.asSourceWithContext(_.committableOffset)
    .map(_.value())
    .log("received message")
    //.run()
    //.log("value is:", _.value())
    //.mapConcat(_.value().split(",").toIterator)
    //.map()
    //.map(_.value())
    //.log("value is:", _.toString)
    //.mapConcat(x=> x.value().split(","))
    //.flatMapConcat(_.split(","))
    .runWith(Sink.foreach(println)) // just print each message for debugging
  }

}
