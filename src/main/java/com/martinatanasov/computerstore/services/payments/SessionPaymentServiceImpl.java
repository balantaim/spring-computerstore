/*
 * Copyright 2025 Martin Atanasov.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.martinatanasov.computerstore.services.payments;

import com.martinatanasov.computerstore.entities.*;
import com.martinatanasov.computerstore.model.OrderStatus;
import com.martinatanasov.computerstore.repositories.OrderRepository;
import com.martinatanasov.computerstore.utils.converter.PaymentPriceConverter;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionExpireParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Service
public class SessionPaymentServiceImpl implements SessionPaymentService {

    private final String APP_DOMAIN_NAME;
    private final String PAYMENT_CURRENCY;
    private final PaymentPriceConverter paymentPriceConverter;
    private final OrderRepository orderRepository;

    public SessionPaymentServiceImpl(@Value("${application.domain}") String appDomainName,
                                     @Value("${application.payment.currency}") String paymentCurrency,
                                     PaymentPriceConverter paymentPriceConverter,
                                     OrderRepository orderRepository) {
        this.APP_DOMAIN_NAME = appDomainName;
        this.PAYMENT_CURRENCY = paymentCurrency;
        this.paymentPriceConverter = paymentPriceConverter;
        this.orderRepository = orderRepository;
        log.trace("\n\tPayment currency: {}", PAYMENT_CURRENCY);
    }

    @Override
    public Session createCheckoutSession(final User user, final Long orderId) throws StripeException {
        Optional<Order> order = orderRepository.getOrderById(orderId);
        if (user != null && order.isPresent()) {
            if (order.get().getStatus() != OrderStatus.ORDER_ABORTED) {
                SessionCreateParams params = SessionCreateParams.builder()
                        //Set Carrier information
                        .putAllMetadata(
                                getCarrierMetadata(order.get().getShipment())
                        )
                        //Set OrderId to the session
                        .setClientReferenceId(String.valueOf(orderId))
                        //Add payment method type
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        //Set payment mode
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        //Set customer ID
                        .setCustomer(user.getCustomerId())
                        //Set Locale Language
                        .setLocale(getUserCountryLanguage(user.getCountry()))
                        //Success redirect
                        .setSuccessUrl(APP_DOMAIN_NAME + "/Checkout/step-3")
                        //Cancel redirect
                        .setCancelUrl(APP_DOMAIN_NAME + "/Checkout/step-3-cancel/" + order.get().getId())
                        //Add multiple items
                        .addAllLineItem(convertOrderItemsToLineItems(order.get().getOrderItems()))
                        /*
                        //Add single item
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice("price_1MotwRLkdIwHu7ixYcPLm5uZ")
                                        .setQuantity(2L)
                                        .build()
                        )
                        */
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .build();

                final Session session = Session.create(params);
                if (session != null) {
                    //Set session ID to payment object
                    Payment payment = order.get().getPayment();
                    if (payment != null) {
                        payment.setPaymentSessionId(session.getId());
                        order.get().setPayment(payment);
                        orderRepository.save(order.get());
                    }

                    printExpirationTime(session.getExpiresAt());
                    log.info("\n\tSession ID: {}", session.getId());
                    log.trace("\n\tCreated timestamp: {}", session.getCreated());
                    log.info("\n\tCreated URL: {}", session.getUrl());
                }
                return session;
            }
        }
        return null;
    }

    @Override
    public Session retrieveCheckoutSession(final String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }

    @Override
    public Session expireCheckoutSession(final String sessionId) throws StripeException {
        Session resource = Session.retrieve(sessionId);
        log.info("\n\tSession status: {}", resource.getStatus());
        printExpirationTime(resource.getExpiresAt());

        SessionExpireParams params = SessionExpireParams.builder().build();
        return resource.expire(params);
    }

    private void printExpirationTime(final long expiresAt) {
        final String formattedTime = Instant.ofEpochSecond(expiresAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("\n\tSession expires at: {}", formattedTime);
    }

    private List<SessionCreateParams.LineItem> convertOrderItemsToLineItems(final Set<OrderItem> items) {
        List<SessionCreateParams.LineItem> products = new ArrayList<>();
        for (OrderItem index : items) {
            products.add(
                    createLineItem(index.getProduct().getProductName(),
                            index.getProduct().getDescription(),
                            Long.valueOf(index.getQuantity()),
                            paymentPriceConverter.convertPriceToLong(index.getPricePerUnit())
                    )
            );
        }
        return products;
    }

    private SessionCreateParams.LineItem createLineItem(final String itemName,
                                                        final String itemDescription,
                                                        final long itemQuantity,
                                                        final long itemPrice) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(PAYMENT_CURRENCY)
                                .setUnitAmount(itemPrice)
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(itemName)
                                                .setDescription(itemDescription)
                                                .build()
                                )
                                .build()
                )
                .setQuantity(itemQuantity)
                .build();
    }

    private Map<String, String> getCarrierMetadata(final Shipment shipment) {
        Map<String, String> metadata = new HashMap<>();
        if(shipment != null) {
            metadata.put("carrier", shipment.getCarrier().name());
            metadata.put("tracking_number", shipment.getTrackingNumber());
        }
        return metadata;
    }

    private SessionCreateParams.Locale getUserCountryLanguage(final String country) {
        return switch (country) {
            case "USA", "England" -> SessionCreateParams.Locale.EN;
            default -> SessionCreateParams.Locale.BG;
        };
    }

}
