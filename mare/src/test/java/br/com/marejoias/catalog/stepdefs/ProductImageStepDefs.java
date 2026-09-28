package br.com.marejoias.catalog.stepdefs;

import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.catalog.service.ImageStorageService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductImageStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    private ResultActions resultActions;
    private UUID currentProductId;
    private RequestPostProcessor securityUser;

    @Dado("que estou autenticado na API como um Administrador")
    public void que_estou_autenticado_na_api_como_um_administrador() {
        // Simula um token JWT válido com a role ADMIN
        this.securityUser = user("admin@marejoias.com").roles("ADMIN");
    }

    @Dado("que estou autenticado na API como um Cliente Comum")
    public void que_estou_autenticado_na_api_como_um_cliente_comum() {
        // Simula um token JWT válido com a role CUSTOMER
        this.securityUser = user("cliente@teste.com").roles("CUSTOMER");
    }

    @Dado("existe um produto {string} cadastrado sem imagem")
    public void existe_um_produto_cadastrado_sem_imagem(String nomeProduto) {
        Product produto = Product.builder()
                .name(nomeProduto)
                .description("Descrição genérica")
                .priceCents(5000)
                .sku("SKU-" + System.currentTimeMillis())
                .slug("produto-teste-" + System.currentTimeMillis())
                .stockQuantity(10)
                .isActive(true)
                // imageUrl fica null por padrão
                .build();

        produto = productRepository.save(produto);
        this.currentProductId = produto.getId();
    }

    @Dado("não existe nenhum produto com o ID informado")
    public void que_nao_existe_nenhum_produto_com_o_id_informado() {
        this.currentProductId = UUID.randomUUID();
    }

    @Quando("eu enviar um arquivo de imagem válido para a rota de upload deste produto")
    public void eu_enviar_um_arquivo_de_imagem_valido_para_a_rota_de_upload_deste_produto() throws Exception {
        // 1. Criamos um arquivo falso na memória para simular o upload do frontend
        MockMultipartFile fakeImage = new MockMultipartFile(
                "file", // Nome do parâmetro que o Controller espera (@RequestParam("file"))
                "foto.jpg",
                "image/jpeg",
                "conteudo_falso_da_imagem".getBytes()
        );

        // 2. Ensinamos o Mockito a retornar uma URL falsa quando o serviço for chamado
        when(imageStorageService.uploadImage(any())).thenReturn("https://res.cloudinary.com/demo/image/upload/v1234/colar.jpg");

        // 3. Disparamos a requisição HTTP do tipo MULTIPART (que é usada para envio de arquivos)
        resultActions = mockMvc.perform(multipart("/api/v1/products/" + currentProductId + "/image")
                .file(fakeImage)
                .with(securityUser)); // Injeta a autenticação configurada no @Dado
    }

    @Quando("eu tentar enviar um arquivo de imagem para a rota de upload deste produto")
    public void eu_tentar_enviar_um_arquivo_de_imagem_para_a_rota_de_upload_deste_produto() throws Exception {
        eu_enviar_um_arquivo_de_imagem_valido_para_a_rota_de_upload_deste_produto();
    }

    @Quando("eu enviar um arquivo de imagem válido para a rota de upload de um produto inexistente")
    public void eu_enviar_um_arquivo_de_imagem_valido_para_a_rota_de_upload_de_um_produto_inexistente() throws Exception {
        eu_enviar_um_arquivo_de_imagem_valido_para_a_rota_de_upload_deste_produto();
    }

    @Então("a API de catálogo deve retornar o status HTTP {int}")
    public void a_api_de_catalogo_deve_retornar_o_status_http(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @Então("o produto na base de dados deve ser atualizado com a URL {string}")
    public void o_produto_na_base_de_dados_deve_ser_atualizado_com_a_url(String urlEsperada) {
        Product produtoAtualizado = productRepository.findById(currentProductId).orElseThrow();
        assertEquals(urlEsperada, produtoAtualizado.getImageUrl());
    }

    @Então("a API de catálogo deve recusar a operação")
    public void a_api_de_catalogo_deve_recusar_a_operacao() {
        // A recusa (403) é validada no passo do status HTTP
    }

    @Então("a API de catálogo deve retornar uma mensagem de erro informando {string}")
    public void a_api_de_catalogo_deve_retornar_uma_mensagem_de_erro_informando(String mensagemEsperada) throws Exception {
        resultActions.andExpect(jsonPath("$.message").value(mensagemEsperada));
    }
}