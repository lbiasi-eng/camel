package com.example.demo.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.bean.TestBean;

@Component
public class TestRoute extends RouteBuilder {

	@Autowired
	TestBean myBean;

	@Override
	public void configure() throws Exception {

		from("timer:hello?period={{myPeriod}}").routeId("hello")
				// and call the bean
				.bean(myBean, "saySomething")
				// and print it to system out via stream component
				.to("stream:out");

		String kafkaSource = "kafka:{{producer.topic}}?brokers={{kafka.bootstrap.url}}&keyDeserializer=org.apache.kafka.common.serialization.StringDeserializer&valueDeserializer=org.apache.kafka.common.serialization.StringDeserializer";
		from(kafkaSource).to("stream:out");
	}

}
