package com.example.demo.processor;

import com.example.demo.exceptions.HystrixRecoverableException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class FallbackProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Exception ex = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        if(ex instanceof HystrixRecoverableException){
            throw ex;
        }else{
            String exType = ex.getClass().getName();
            System.out.println(exType);
        }
    }
}
