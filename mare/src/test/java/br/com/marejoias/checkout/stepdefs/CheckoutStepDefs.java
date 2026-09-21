package br.com.marejoias.checkout.stepdefs;

import br.com.marejoias.catalog.domain.entity.Product;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.checkout.service.CheckoutService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CheckoutStepDefs {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CheckoutService checkoutService;

    private List<CheckoutService.ItemRequest> carrinho;
    private Integer freteAtual;
    private Order ultimoPedidoCriado;
    private Exception ultimaExcecaoCapturada;

    @Dado("que o cliente possui um carrinho com {string} custando R$ {double} e quantidade {int}")
    public void queOClientePossuiUmCarrinhoComProduto(String nomeProduto, Double precoReais, Integer qtd) {
        if (carrinho == null) {
            carrinho = new ArrayList<>();
            productRepository.deleteAll();
            orderRepository.deleteAll();
        }

        int precoCents = (int) (precoReais * 100);

        Product produto = Product.builder()
                .name(nomeProduto)
                .sku("SKU-" + nomeProduto.hashCode())
                .slug(nomeProduto.toLowerCase().replace(" ", "-"))
                .priceCents(precoCents)
                .stockQuantity(10) // Estoque inicial folgado para o teste de cálculo
                .isActive(true)
                .build();
        
        Product salvo = productRepository.save(produto);
        carrinho.add(new CheckoutService.ItemRequest(salvo.getId(), qtd));
    }

    @E("possui {string} custando R$ {double} e quantidade {int}")
    public void possuiOutroProdutoNoCarrinho(String nomeProduto, Double precoReais, Integer qtd) {
        queOClientePossuiUmCarrinhoComProduto(nomeProduto, precoReais, qtd);
    }

    @Dado("que o produto {string} possui apenas {int} unidade disponível em estoque")
    public void queOProdutoPossuiApenasUnidadeDisponivel(String nomeProduto, Integer estoqueQtd) {
        productRepository.deleteAll();
        orderRepository.deleteAll();

        Product produto = Product.builder()
                .name(nomeProduto)
                .sku("SKU-LIMIT")
                .slug("produto-limitado")
                .priceCents(5000)
                .stockQuantity(estoqueQtd)
                .isActive(true)
                .build();
        
        Product salvo = productRepository.save(produto);
        carrinho = new ArrayList<>();
        carrinho.add(new CheckoutService.ItemRequest(salvo.getId(), estoqueQtd + 1)); // Tenta pedir mais do que tem
    }

    @Quando("o sistema processar a criação do pedido com taxa de frete de R$ {double}")
    public void oSistemaProcessarACriacaoDoPedidoComFrete(Double freteReais) {
        this.freteAtual = (int) (freteReais * 100);
        try {
            this.ultimoPedidoCriado = checkoutService.processCheckout(carrinho, freteAtual, "Rua das Joias, 100");
        } catch (Exception e) {
            this.ultimaExcecaoCapturada = e;
        }
    }

    @Quando("o cliente tentar fechar o pedido solicitando {int} unidades deste produto")
    public void oClienteTentarFecharOPedidoSolicitandoUnidades(Integer qtdDesejada) {
        try {
            // Pega o id do produto que salvamos no passo anterior
            UUID productId = productRepository.findAll().get(0).getId();
            List<CheckoutService.ItemRequest> pedidoInvalido = List.of(new CheckoutService.ItemRequest(productId, qtdDesejada));
            
            checkoutService.processCheckout(pedidoInvalido, 0, "Endereço Teste");
        } catch (Exception e) {
            this.ultimaExcecaoCapturada = e;
        }
    }

    @Então("o valor total do pedido deve ser R$ {double}")
    public void oValorTotalDoPedidoDeveSer(Double totalEsperadoReais) {
        int totalEsperadoCents = (int) (totalEsperadoReais * 100);
        assertNotNull(ultimoPedidoCriado);
        assertEquals(totalEsperadoCents, ultimoPedidoCriado.getTotalAmountCents());
    }

    @E("o status do pedido deve ser iniciado como {string}")
    public void oStatusDoPedidoDeveSerIniciadoComo(String statusEsperado) {
        assertNotNull(ultimoPedidoCriado);
        assertEquals(statusEsperado, ultimoPedidoCriado.getStatus());
    }

    @Então("o sistema deve recusar o checkout")
    public void oSistemaDeveRecusarOCheckout() {
        assertNotNull(ultimaExcecaoCapturada, "Esperava-se uma exceção de estoque insuficiente, mas o checkout passou!");
    }

    @E("retornar uma mensagem de erro informando que o estoque é insuficiente")
    public void retornarUmaMensagemDeErroInformandoQueOEstoqueEInsuficiente() {
        assertTrue(ultimaExcecaoCapturada.getMessage().contains("Estoque insuficiente"));
    }
}