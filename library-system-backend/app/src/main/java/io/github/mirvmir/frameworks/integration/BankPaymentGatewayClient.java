package io.github.mirvmir.frameworks.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mirvmir.exception.integration.PaymentException;
import io.github.mirvmir.exception.integration.PaymentGatewayUnavailableException;
import io.github.mirvmir.exception.integration.PaymentUnavailableException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.frameworks.integration.outputs.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class BankPaymentGatewayClient {

    private final Config config;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BankPaymentGatewayClient(Config config, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void checkAvailability() {
        try {
            String bankBaseUrl = config.getBankUrl();
            restTemplate.getForEntity(bankBaseUrl + "/health", String.class);
        } catch (RestClientException e) {
            throw new PaymentUnavailableException("Оплата временно недоступна.");
        }
    }

    public BankBindCardOutput bindCard(BankBindCardRq request) {
        try {
            String bankBaseUrl = config.getBankUrl();

            ResponseEntity<BankBindCardOutput> response =
                    restTemplate.postForEntity(
                            bankBaseUrl + "/payment-gateway/cards/bind",
                            request,
                            BankBindCardOutput.class
                    );

            BankBindCardOutput body = response.getBody();

            if (body == null) {
                throw new PaymentUnavailableException("Оплата временно недоступна.");
            }

            return body;

        } catch (HttpClientErrorException e) {
            try {
                BankErrorOutput error =
                        objectMapper.readValue(
                                e.getResponseBodyAsString(),
                                BankErrorOutput.class
                        );

                throw new PaymentException(
                        error.detail()
                );

            } catch (JsonProcessingException ex) {
                throw new PaymentException(
                        "Card bind error."
                );
            }

        } catch (RestClientException e) {
            throw new PaymentUnavailableException("Оплата временно недоступна.");
        }
    }

    public BankPayOutput pay(BankPayRq request) {
        try {
            String bankBaseUrl = config.getBankUrl();

            ResponseEntity<BankPayOutput> response =
                    restTemplate.postForEntity(
                            bankBaseUrl + "/payment-gateway/pay-by-token",
                            request,
                            BankPayOutput.class
                    );

            BankPayOutput body = response.getBody();

            if (body == null) {
                throw new PaymentGatewayUnavailableException("Оплата временно недоступна.");
            }

            return body;

        } catch (HttpClientErrorException e) {
            try {
                BankErrorOutput error =
                        objectMapper.readValue(
                                e.getResponseBodyAsString(),
                                BankErrorOutput.class
                        );

                throw new PaymentException(
                        error.detail()
                );

            } catch (JsonProcessingException ex) {
                throw new PaymentException(
                        "Payment error."
                );
            }

        } catch (RestClientException e) {
            throw new PaymentGatewayUnavailableException("Оплата временно недоступна.");
        }
    }

    public BankPayoutOutput payout(BankPayoutRq request) {
        try {
            String bankBaseUrl = config.getBankUrl();

            ResponseEntity<BankPayoutOutput> response =
                    restTemplate.postForEntity(
                            bankBaseUrl + "/payment-gateway/payout-by-token",
                            request,
                            BankPayoutOutput.class
                    );

            BankPayoutOutput body = response.getBody();

            if (body == null) {
                throw new PaymentGatewayUnavailableException(
                        "Вывод средств временно недоступен."
                );
            }

            return body;

        } catch (HttpClientErrorException e) {
            try {
                BankErrorOutput error =
                        objectMapper.readValue(
                                e.getResponseBodyAsString(),
                                BankErrorOutput.class
                        );

                throw new PaymentException(
                        error.detail()
                );

            } catch (JsonProcessingException ex) {
                throw new PaymentException(
                        "Payout error."
                );
            }

        } catch (RestClientException e) {
            throw new PaymentGatewayUnavailableException(
                    "Вывод средств временно недоступен."
            );
        }
    }

    public BankRefundOutput refund(BankRefundRq request) {
        try {
            String bankBaseUrl = config.getBankUrl();

            ResponseEntity<BankRefundOutput> response =
                    restTemplate.postForEntity(
                            bankBaseUrl + "/payment-gateway/refund",
                            request,
                            BankRefundOutput.class
                    );

            BankRefundOutput body = response.getBody();

            if (body == null) {
                throw new PaymentGatewayUnavailableException(
                        "Возврат временно недоступен."
                );
            }

            return body;

        } catch (HttpClientErrorException e) {
            try {
                BankErrorOutput error =
                        objectMapper.readValue(
                                e.getResponseBodyAsString(),
                                BankErrorOutput.class
                        );

                throw new PaymentException(
                        error.detail()
                );

            } catch (JsonProcessingException ex) {
                throw new PaymentException(
                        "Refund error."
                );
            }

        } catch (RestClientException e) {
            throw new PaymentGatewayUnavailableException(
                    "Возврат временно недоступен."
            );
        }
    }
}