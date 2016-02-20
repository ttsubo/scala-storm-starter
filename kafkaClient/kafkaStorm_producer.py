from kafka.client import KafkaClient
from kafka.producer import SimpleProducer
from time import sleep
from datetime import datetime

kafka = KafkaClient("localhost:9092")

producer = SimpleProducer(kafka)

while 1:
  # "kafkaesque" is the name of our topic
  now = "It is " + str(datetime.now().time())
  print now
  producer.send_messages("kafkaStorm", now )
  sleep(1)
