package com.example.demo;

import com.example.demo.dao.AccountRepository;
import com.example.demo.entity.Account;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplication.class, H2TestProfileJPAConfig.class})
@ActiveProfiles("test")
public class AccountRepositoryTest {

    @Autowired
    AccountRepository repository;

    @Test
    public void testFindByName(){
        repository.deleteAll();

        Account account = new Account();
        account.setId(1001L);
        account.setName("Robin");
        account.setSurname("Hood");

        repository.save(account);

        Account account2 = new Account();
        account2.setId(1002L);
        account2.setName("Mary");
        account2.setSurname("Marien");

        repository.save(account2);

        List<Account> accounts = repository.findByName("Robin");
        assert(accounts.size() == 1);
        assert(accounts.get(0).getName().equals("Robin"));

    }

}
