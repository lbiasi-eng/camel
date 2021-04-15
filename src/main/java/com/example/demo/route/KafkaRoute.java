package com.example.demo.route;
import com.example.demo.beans.Account;
import com.example.demo.exceptions.HystrixRecoverableException;
import com.example.demo.processor.DeleteProcessor;
import com.example.demo.processor.FallbackProcessor;
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

	@Autowired
	DeleteProcessor delete;

	@Autowired
	FallbackProcessor fallbackProcessor;

	@Override
	public void configure() throws Exception {

		from("timer://foo?period=1000000000")
				.setBody(constant("Hi This is Avro example"))
				.routeId(KAFKA_ROUTE_NAME)
				.onException(Exception.class)
					.handled(true)
					.log("Reject message")
				.end()
				.onException(HystrixRecoverableException.class)
					.handled(true)
					.maximumRedeliveries(-1)
					.redeliveryDelay(1000)
				.end()

				.hystrix()
					.inheritErrorHandler(true)
					.hystrixConfiguration()
						.executionIsolationStrategy("SEMAPHORE")
						.executionTimeoutEnabled(true)
						.circuitBreakerSleepWindowInMilliseconds(10000)
						.circuitBreakerErrorThresholdPercentage(50)
						.circuitBreakerRequestVolumeThreshold(10)
					.end()

					.log("Execution inside the hystrix")
					.process(delete)
					.onFallback().process(fallbackProcessor)
				.end()
				.log("Out of hystrix");

	}

}
