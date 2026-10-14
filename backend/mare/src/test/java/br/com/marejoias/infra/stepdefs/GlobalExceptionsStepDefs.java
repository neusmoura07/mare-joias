package br.com.marejoias.infra.stepdefs;

import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.domain.enums.Role;
import br.com.marejoias.identity.repository.UserRepository;
import br.com.marejoias.identity.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionsStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions resultActions;
    private String adminToken;

    @Dado("que a API está rodando perfeitamente")
    public void queAAPIEstaRodandoPerfeitamente() {
        // Passo apenas de contexto
    }

    @Quando("eu enviar uma requisição GET para {string}")
    public void euEnviarUmaRequisicaoGETPara(String url) throws Exception {
        resultActions = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON));
    }

    @Então("a API deve interceptar a exceção de entidade não encontrada")
    public void aAPIDeveInterceptarAExcecaoDeEntidadeNaoEncontrada() {
        // Passo validado na interceptação do status
    }

    @E("retornar o status HTTP {int} Not Found")
    public void retornarOStatusHTTPNotFound(int status) throws Exception {
        resultActions.andExpect(status().is(status));
    }

    @E("o corpo da resposta deve conter o campo {string} com {string}")
    public void oCorpoDaRespostaDeveConterOCampoCom(String campo, String valor) throws Exception {
        resultActions.andExpect(jsonPath("$." + campo).value(containsString(valor)));
    }

    @E("o corpo da resposta deve conter a data e hora do erro no campo {string}")
    public void oCorpoDaRespostaDeveConterADataEHoraDoErroNoCampo(String campo) throws Exception {
        resultActions.andExpect(jsonPath("$." + campo).value(notNullValue()));
    }


    @Dado("que estou autenticado como ADMIN")
    public void queEstouAutenticadoComoADMINNasRegrasDeExcecao() {
        User admin = userRepository.save(User.builder()
                .name("Admin Excecoes")
                .email("admin_excecoes_" + UUID.randomUUID() + "@marejoias.com")
                .passwordHash("senha123")
                .role(Role.ADMIN)
                .build());
        adminToken = tokenService.generateToken(admin);
    }

    @Quando("eu enviar um POST para {string} com o campo {string} em branco")
    public void euEnviarUmPOSTParaComOCampoEmBranco(String url, String campo) throws Exception {
        // Payload sem o nome preenchido para disparar o @Valid do Controller
        Map<String, Object> body = Map.of(
                "sku", "SKU-VALIDO-123",
                "priceCents", 15000,
                "name", "", // <-- Em branco
                "slug", "teste-valido",
                "stockQuantity", 5,
                "categoryId", UUID.randomUUID().toString()
        );

        resultActions = mockMvc.perform(post(url)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @Então("a API deve interceptar o erro de validação")
    public void aAPIDeveInterceptarOErroDeValidacao() {
        // Passo passivo
    }

    @E("retornar o status HTTP {int} Bad Request")
    public void retornarOStatusHTTPBadRequest(int status) throws Exception {
        resultActions.andExpect(status().is(status));
    }

    @E("a lista de erros deve indicar que o campo {string} é obrigatório")
    public void aListaDeErrosDeveIndicarQueOCampoEObrigatorio(String campo) throws Exception {
        resultActions.andExpect(jsonPath("$.message").value(containsString(campo)));
    }
}