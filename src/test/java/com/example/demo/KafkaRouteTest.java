package com.example.demo;

import com.example.demo.route.KafkaRoute;
import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.AdviceWithTasks;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {DemoApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints("direct:*")
@UseAdviceWith
@ActiveProfiles("test")
public class KafkaRouteTest {
    @Autowired
    CamelContext camelContext;

    @Produce
    ProducerTemplate mockKafkaProducer;

    @EndpointInject(uri = "mock:stream:out")
    MockEndpoint finalSink;

    @Before
    public void setUp() throws Exception{

        //Here we swap the FROM component in the KafkaRoute.KAFKA_ROUTE_NAME with a direct component, direct:kafka-from
        camelContext.getRouteDefinition(KafkaRoute.KAFKA_ROUTE_NAME)
                .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:kafka-from");
                    }
                });
    }
    @Test
    public void testKafkaRoute() throws Exception {

        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaConstants.TOPIC, "logs");

        String body = "{\"id\":102, \"name\":\"Micheal\", \"surname\":\"Keeton\"}";
        Exchange exchange = ExchangeBuilder.anExchange(camelContext).withBody(body).build();

        //Send mock message to the route
        mockKafkaProducer.sendBodyAndHeaders("direct:kafka-from", exchange, headers);


        //Assertions. You may do additional assertions with the likes of Mockito
        finalSink.expectedBodiesReceived(body);
        finalSink.expectedHeaderReceived(KafkaConstants.TOPIC, "logs");
        finalSink.assertIsSatisfied();

    }
}
