package com.example.demo.processor;

import com.example.demo.dao.AccountRepository;
import com.example.demo.exceptions.HystrixRecoverableException;
import com.example.demo.exceptions.HystrixUnrecoverableException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteProcessor implements Processor {

    @Autowired
    AccountRepository repo;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = EmptyResultDataAccessException.class)
    public void process(Exchange exchange) throws Exception {
        try {
            repo.deleteById(1000L);
        }catch(EmptyResultDataAccessException e){
            //do nothing
        }catch(DataAccessResourceFailureException e){
            throw new HystrixRecoverableException();
        }catch (Exception e){
            throw new HystrixUnrecoverableException();
        }
    }
}
