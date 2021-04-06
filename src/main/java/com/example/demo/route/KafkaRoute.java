package com.example.demo.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.processor.ReadProcessor;
import com.example.demo.processor.StoreProcessor;

@Component
public class KafkaRoute extends RouteBuilder {

	public static final String KAFKA_ROUTE_NAME = "kafka-router";
	final String kafkaSource = "kafka:{{producer.topic}}?brokers={{kafka.bootstrap.url}}&keyDeserializer=org.apache.kafka.common.serialization.StringDeserializer&valueDeserializer=org.apache.kafka.common.serialization.StringDeserializer";

	@Autowired
	StoreProcessor store;

	@Autowired
	ReadProcessor read;

	@Override
	public void configure() throws Exception {

		from(kafkaSource)
				.routeId(KAFKA_ROUTE_NAME)
				.process(store)
				.process(read)
				.to("stream:out");
				//.to("stream:out");
	}

}
