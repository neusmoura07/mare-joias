package br.com.marejoias.payment.service;

import br.com.marejoias.checkout.domain.entity.Order;
import br.com.marejoias.checkout.domain.entity.OrderItem;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MercadoPagoPaymentService {

    @Value("${mercadopago.back-urls.success}")
    private String successUrl;

    @Value("${mercadopago.back-urls.pending}")
    private String pendingUrl;

    @Value("${mercadopago.back-urls.failure}")
    private String failureUrl;

    public String createPaymentPreference(Order order, List<OrderItem> items) {
        List<PreferenceItemRequest> preferenceItems = items.stream().map(item -> {
            BigDecimal precoUnitario = new BigDecimal(item.getUnitPriceCentsSnapshot())
                    .divide(new BigDecimal(100));

            return PreferenceItemRequest.builder()
                    .id(item.getId().toString())
                    .title(item.getProductNameSnapshot() + " (Tam: " + item.getSize() + ")")
                    .quantity(item.getQuantity())
                    .unitPrice(precoUnitario)
                    .currencyId("BRL")
                    .build();
        }).toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(successUrl)
                .pending(pendingUrl)
                .failure(failureUrl)
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(preferenceItems)
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(order.getId().toString())
                .build();

        try {
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            return preference.getInitPoint();

        } catch (com.mercadopago.exceptions.MPApiException e) {
            // ISSO VAI IMPRIMIR O ERRO EXATO DO MERCADO PAGO NO CONSOLE!
            System.out.println("========== ERRO MERCADO PAGO ==========");
            System.out.println(e.getApiResponse().getContent());
            System.out.println("=======================================");
            throw new RuntimeException("Erro ao comunicar com o Mercado Pago", e);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao comunicar com o Mercado Pago", e);
        }
    }
}