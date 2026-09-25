package br.com.marejoias.identity.stepdefs;

import br.com.marejoias.identity.controller.dto.AuthenticationDTO;
import br.com.marejoias.identity.controller.dto.RegisterDTO;
import br.com.marejoias.identity.domain.entity.User;
import br.com.marejoias.identity.domain.enums.Role;
import br.com.marejoias.identity.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ResultActions resultActions;
    private RegisterDTO registerData;

    @Dado("que eu tenho os seguintes dados de registo:")
    public void que_eu_tenho_os_seguintes_dados_de_registo(DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        registerData = new RegisterDTO(
                row.get("name"),
                row.get("email"),
                row.get("password"),
                row.get("cpf"),
                Role.valueOf(row.get("role"))
        );
    }

    @Dado("que já existe um utilizador com o email {string} na base de dados")
    public void que_ja_existe_um_utilizador_com_o_email_na_base_de_dados(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = User.builder()
                    .name("Utilizador Teste")
                    .email(email)
                    .passwordHash(passwordEncoder.encode("123456"))
                    .cpf("55555555555")
                    .role(Role.CUSTOMER)
                    .build();
            
            userRepository.save(user);
        }
    }

    @Dado("que já existe um utilizador com o email {string} e senha {string} na base de dados")
    public void que_ja_existe_um_utilizador_com_o_email_e_senha_na_base_de_dados(String email, String password) {
        
        User user = User.builder()
                .name("Login Teste")
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .cpf("44444444444")
                .role(Role.CUSTOMER)
                .build();
                
        userRepository.save(user);
    }

    @Quando("eu envio um pedido POST para {string} com esses dados")
    public void eu_envio_um_pedido_post_para_com_esses_dados(String url) throws Exception {
        resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerData)));
    }

    @Quando("eu envio um pedido POST para {string} com o email {string} e senha {string}")
    public void eu_envio_um_pedido_post_para_com_o_email_e_senha(String url, String email, String password) throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO(email, password);
        resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authDTO)));
    }

    @Então("a resposta deve ter o status {int}")
    public void a_resposta_deve_ter_o_status(int expectedStatus) throws Exception {
        resultActions.andExpect(status().is(expectedStatus));
    }

    @Então("o corpo da resposta deve conter o campo {string} preenchido")
    public void o_corpo_da_resposta_deve_conter_o_campo_preenchido(String field) throws Exception {
        resultActions.andExpect(jsonPath("$." + field).exists())
                     .andExpect(jsonPath("$." + field).isNotEmpty());
    }
}