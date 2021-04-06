package com.example.demo;

import com.example.demo.dao.AccountRepository;
import com.example.demo.entity.Account;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {DemoApplication.class, H2TestProfileJPAConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
@UseAdviceWith
@ActiveProfiles("test")
public class KafkaRouteTest {

    @Autowired
    CamelContext camelContext;

    @Produce
    ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:stream:out")
    MockEndpoint finalSink;

    @Autowired
    AccountRepository repository;


    @Before
    public void setUp() throws Exception{

        //Here we swap the FROM component in the KafkaRoute.KAFKA_ROUTE_NAME with a direct component, direct:kafka-from
        camelContext.getRouteDefinition(KafkaRoute.KAFKA_ROUTE_NAME)
                .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:kafka-from");
//                        interceptSendToEndpoint("stream:out")
//                                .to("mock:stream:out");
                    }
                });
        camelContext.start();

    }

    @After
    public void tearDown() throws Exception {
        camelContext.stop();
    }

    @Test
    public void testKafkaRoute() throws Exception {

        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaConstants.TOPIC, "logs");

        String body = "{\"id\":102, \"name\":\"Micheal\", \"surname\":\"Keeton\"}";
        Exchange exchange = ExchangeBuilder.anExchange(camelContext).withBody(body).build();

        //Send mock message to the route
        producerTemplate.send("direct:kafka-from", exchange);

        Optional<Account> expectedAccount =  repository.findById(102L);
        assert(expectedAccount.isPresent());
        String expectedBody = expectedAccount.get().toString();

        //Assertions. You may do additional assertions with the likes of Mockito
        finalSink.setExpectedMessageCount(1);
        finalSink.message(0).body().isEqualTo(expectedBody);
        finalSink.assertIsSatisfied();

    }
}
