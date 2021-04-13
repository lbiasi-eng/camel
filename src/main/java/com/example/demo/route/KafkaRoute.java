package com.example.demo.route;
import com.example.demo.beans.Account;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.processor.ReadProcessor;
import com.example.demo.processor.StoreProcessor;

@Component
public class KafkaRoute extends RouteBuilder {

	public static final String KAFKA_ROUTE_NAME = "kafka-router";
	final String kafkaSource = "kafka:{{producer.topic}}?brokers={{kafka.bootstrap.url}}&keyDeserializer=org.apache.kafka.common.serialization.ByteArrayDeserializer&valueDeserializer=org.apache.kafka.common.serialization.ByteArrayDeserializer";

	@Autowired
	StoreProcessor store;

	@Autowired
	ReadProcessor read;

	@Override
	public void configure() throws Exception {

		String loggerStr = String.format("Received message:  ${in.body} with key: ${in.header.%s} " +
				"and partition: ${in.header.%s} and offset: ${in.header.%s}",
				KafkaConstants.KEY, KafkaConstants.PARTITION, KafkaConstants.OFFSET);

		from(kafkaSource)
				.routeId(KAFKA_ROUTE_NAME)
				.log(loggerStr)
				.unmarshal().avro(Account.class.getName())
				.to("stream:out");
	}

}
