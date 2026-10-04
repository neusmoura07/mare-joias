package br.com.marejoias.infra.stepdefs;

import br.com.marejoias.bdd.HttpResponseContext;
import br.com.marejoias.infra.ratelimit.RateLimitService;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityPoliciesStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HttpResponseContext httpResponse;

    @Autowired
    private RateLimitService rateLimitService;

    @Before
    public void restaurarPoliticasDeRateLimit() {
        rateLimitService.reset();
    }

    @Dado("que a API está configurada para aceitar a origem {string}")
    public void queAApiEstaConfiguradaParaAceitarAOrigem(String origem) {
    }

    @Quando("o navegador enviar uma requisição OPTIONS para {string} com o cabeçalho de origem {string}")
    public void oNavegadorEnviarUmaRequisicaoOptionsParaComOCabecalhoDeOrigem(String caminho, String origem) throws Exception {
        httpResponse.setResultActions(mockMvc.perform(options(caminho)
                .header("Origin", origem)
                .header("Access-Control-Request-Method", "GET")));
    }

    @Então("o sistema deve processar a requisição com sucesso")
    public void oSistemaDeveProcessarARequisicaoComSucesso() {
    }

    @E("o cabeçalho da resposta deve conter {string} igual a {string}")
    public void oCabecalhoDaRespostaDeveConterIgualA(String nomeCabecalho, String valorEsperado) throws Exception {
        httpResponse.getResultActions().andExpect(header().string(nomeCabecalho, valorEsperado));
    }

    @Dado("que o limite de taxa de requisições da API é configurado para {int} por minuto por IP")
    public void queOLimiteDeTaxaDaApiEConfiguradoPara(int requisicoesPorMinuto) {
        rateLimitService.setRequestsPerMinute(requisicoesPorMinuto);
    }

    @Dado("um determinado IP já enviou {int} requisições válidas no último minuto")
    public void umDeterminadoIpJaEnviouRequisicoesValidasNoUltimoMinuto(int quantidade) throws Exception {
        for (int i = 0; i < quantidade; i++) {
            mockMvc.perform(get("/api/v1/products")).andExpect(status().isOk());
        }
    }

    @Quando("esse mesmo IP enviar a 6ª requisição GET para {string}")
    public void esseMesmoIpEnviarA6Requisicao(String caminho) throws Exception {
        httpResponse.setResultActions(mockMvc.perform(get(caminho)));
    }

    @Então("o sistema deve interceptar e recusar a requisição")
    public void oSistemaDeveInterceptarERecusarARequisicao() {
    }

    @E("retornar o status HTTP {int} Too Many Requests")
    public void retornarOStatusHTTPTooManyRequests(int statusEsperado) throws Exception {
        httpResponse.getResultActions().andExpect(status().is(statusEsperado));
    }
}
