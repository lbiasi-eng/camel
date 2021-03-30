package com.example.demo.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.dao.AccountRepository;
import com.example.demo.entity.Account;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StoreProcessor implements Processor {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	AccountRepository repo;

	@Override
	public void process(Exchange exchange) throws Exception {

		String message = (String) exchange.getIn()
				.getBody();

		Account account = mapper.readValue(message, Account.class);

		repo.save(account);

	}

}
