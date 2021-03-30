package com.example.demo.processor;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.dao.AccountRepository;
import com.example.demo.entity.Account;

@Component
public class ReadProcessor implements Processor {

	@Autowired
	AccountRepository repo;

	@Override
	public void process(Exchange exchange) throws Exception {
		List<Account> accounts = repo.findAll();

		String data = accounts.stream()
				.map(a -> a.toString())
				.collect(Collectors.joining("\n"));

		exchange.getOut()
				.setBody(data);
	}

}
