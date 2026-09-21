package br.com.marejoias.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest
public class CucumberSpringConfiguration {
    // Esta classe serve apenas para carregar o contexto do Spring para todos os testes BDD.
}