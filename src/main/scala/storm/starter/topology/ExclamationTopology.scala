package storm.starter.topology

import backtype.storm.{ Config, LocalCluster, StormSubmitter }
import backtype.storm.testing.TestWordSpout
import backtype.storm.topology.TopologyBuilder
import backtype.storm.utils.Utils
import storm.kafka.{KafkaSpout, SpoutConfig, ZkHosts, StringScheme}
import backtype.storm.spout.SchemeAsMultiScheme

object ExclamationTopology {
  def main(args: Array[String]) {
    import storm.starter.bolt.ExclamationBolt

    val builder: TopologyBuilder = new TopologyBuilder()

    val topic = "kafkaStorm"
    val kafkaZkConnect = "127.0.0.1:2181"
    val zkHosts = new ZkHosts(kafkaZkConnect, "/brokers")
    val zkRoot = "/kafkastorm"
    val zkSpoutId = "kafka-spout"
    val kafkaConfig = new SpoutConfig(zkHosts, topic, zkRoot, zkSpoutId)
    kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
    val kafkaSpout = new KafkaSpout(kafkaConfig)
    val kafkaSpoutId = "word-spout"
    builder.setSpout(kafkaSpoutId, kafkaSpout)

    builder.setBolt("exclaim1", new ExclamationBolt(), 3).shuffleGrouping(kafkaSpoutId)
    builder.setBolt("exclaim2", new ExclamationBolt(), 2).shuffleGrouping("exclaim1")

    val config = new Config()
    config.setDebug(true)

    if (args != null && args.length > 0) {
      config.setNumWorkers(3)
      StormSubmitter.submitTopology(args(0), config, builder.createTopology())
    } else {
      val cluster: LocalCluster = new LocalCluster()
      cluster.submitTopology("ExclamationTopology", config, builder.createTopology())
      Utils.sleep(5000)
      cluster.killTopology("ExclamationTopology")
      cluster.shutdown()
    }
  }
}
