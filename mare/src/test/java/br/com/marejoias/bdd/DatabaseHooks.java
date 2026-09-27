package br.com.marejoias.bdd;

import br.com.marejoias.catalog.repository.CategoryRepository;
import br.com.marejoias.catalog.repository.ProductRepository;
import br.com.marejoias.catalog.repository.ProductSizeRepository;
import br.com.marejoias.checkout.repository.OrderItemRepository;
import br.com.marejoias.checkout.repository.OrderRepository;
import br.com.marejoias.customer.repository.AddressRepository;
import br.com.marejoias.identity.repository.UserRepository;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseHooks {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    // 1. Injetar os novos repositórios do seu módulo
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Before // ou @After
    public void cleanDatabase() {
        // 2. Apagar PRIMEIRO as tabelas filhas do Checkout
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();

        // 3. Depois apagar as tabelas de Catálogo
        productSizeRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // 4. Por fim, apagar Endereços e Utilizadores
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }
}