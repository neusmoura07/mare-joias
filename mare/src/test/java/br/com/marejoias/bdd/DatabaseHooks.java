package br.com.marejoias.bdd;

import br.com.marejoias.customer.repository.AddressRepository;
import br.com.marejoias.identity.repository.UserRepository;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseHooks {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;


    @Before
    public void cleanDatabase() {
        // A ordem aqui é muito importante por causa da Chave Estrangeira (Foreign Key)!
        // Primeiro deletamos os filhos (endereços)...
        addressRepository.deleteAll();
        // ...e depois deletamos os pais (usuários/clientes)
        userRepository.deleteAll();
    }
}