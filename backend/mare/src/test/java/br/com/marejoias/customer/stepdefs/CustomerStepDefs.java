package br.com.marejoias.customer.stepdefs;

import br.com.marejoias.customer.controller.dto.CustomerUpdateDTO;
import br.com.marejoias.customer.domain.entity.Address;
import br.com.marejoias.customer.domain.entity.Customer;
import br.com.marejoias.customer.repository.AddressRepository;
import br.com.marejoias.customer.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
public class CustomerStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    private ResultActions resultActions;
    private UUID currentCustomerId;


    @Dado("que existe um cliente com ID válido e nome {string}")
    public void que_existe_um_cliente_com_id_valido_e_nome(String nome) {

        Customer customer = Customer.builder()
                .name(nome)
                .email("atualizacao@teste.com")
                .cpf("11122233344")
                .passwordHash("senha123")
                .role("CUSTOMER")
                .build();

        customer = customerRepository.save(customer);
        this.currentCustomerId = customer.getId();
    }

    @Quando("eu enviar um pedido PUT para alterar o nome para {string}")
    public void eu_enviar_um_pedido_put_para_alterar_o_nome_para(String novoNome) throws Exception {
        CustomerUpdateDTO dto = new CustomerUpdateDTO(novoNome);

        resultActions = mockMvc.perform(put("/api/v1/customers/" + currentCustomerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(user("atualizacao@teste.com").roles("CUSTOMER")));
    }

    @Então("o sistema deve atualizar o cliente e retornar o status {int}")
    public void o_sistema_deve_atualizar_o_cliente_e_retornar_o_status(int statusEsperado) throws Exception {
        resultActions.andExpect(status().is(statusEsperado));
    }


    @Dado("que o cliente {string} já possui {int} endereços cadastrados")
    public void que_o_cliente_ja_possui_enderecos_cadastrados(String nomeCliente, Integer qtdeEnderecos) {
        customerRepository.findByCpf("99999999999").ifPresent(customerRepository::delete);

        Customer customer = Customer.builder()
                .name(nomeCliente)
                .email("maria@teste.com")
                .cpf("99999999999")
                .passwordHash("senha123")
                .role("CUSTOMER")
                .build();

        for (int i = 0; i < qtdeEnderecos; i++) {
            Address address = Address.builder()
                    .zipCode("00000-00" + i)
                    .street("Rua " + i)
                    .build();
            customer.addAddress(address);
        }

        customer = customerRepository.save(customer);
        this.currentCustomerId = customer.getId();
    }

    @Quando("o sistema tentar adicionar um quarto endereço com CEP {string}")
    public void o_sistema_tentar_adicionar_um_quarto_endereco_com_cep(String cep) throws Exception {
        String jsonPayload = String.format("{\"zipCode\": \"%s\", \"street\": \"Rua Nova Teste\"}", cep);

        resultActions = mockMvc.perform(post("/api/v1/customers/" + currentCustomerId + "/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .with(user("maria@teste.com").roles("CUSTOMER")));
    }

    @Então("o sistema deve recusar a adição do endereço")
    public void o_sistema_deve_recusar_a_adicao_do_endereco() throws Exception {
        resultActions.andExpect(status().isUnprocessableEntity());
    }

    @Então("retornar uma mensagem de erro informando limite máximo de endereços")
    public void retornar_uma_mensagem_de_erro_informando_limite_maximo_de_enderecos() throws Exception {
        resultActions.andExpect(jsonPath("$.message").value("Limite máximo de endereços atingido"));
    }
}