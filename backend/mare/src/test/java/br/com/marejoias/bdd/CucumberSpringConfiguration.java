package br.com.marejoias.bdd;

import br.com.marejoias.catalog.service.ImageStorageService;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    // Esta classe serve apenas para carregar o contexto do Spring para todos os testes BDD.

    @MockBean
    private ImageStorageService imageStorageService;
}