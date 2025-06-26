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

import com.martinatanasov.computerstore.entities.Order;
import com.martinatanasov.computerstore.entities.OrderItem;
import com.martinatanasov.computerstore.entities.Shipment;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.repositories.OrderRepository;
import com.martinatanasov.computerstore.utils.converter.PaymentPriceConverter;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionExpireParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.martinatanasov.computerstore.services.OrderServiceImpl.shippingEstimate;


@Slf4j
@Service
public class SessionPaymentServiceImpl implements SessionPaymentService {

    private final String APP_DOMAIN_NAME;
    private final PaymentPriceConverter paymentPriceConverter;
    private final OrderRepository orderRepository;

    public SessionPaymentServiceImpl(@Value("${application.domain}") String appDomainName,
                                     PaymentPriceConverter paymentPriceConverter,
                                     OrderRepository orderRepository) {
        this.APP_DOMAIN_NAME = appDomainName;
        this.paymentPriceConverter = paymentPriceConverter;
        this.orderRepository = orderRepository;
    }

    @Override
    public Session createCheckoutSession(final User user, final Long orderId) throws StripeException {
        Optional<Order> order = orderRepository.getOrderById(orderId);
        if (user != null && order.isPresent()) {
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
                log.info("\n\tSession ID: {}", session.getId());
                log.info("\n\tCreated timestamp: {}", session.getCreated());
                log.info("\n\tCreated URL: {}", session.getUrl());
            }
            return session;
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
        SessionExpireParams params = SessionExpireParams.builder().build();
        return resource.expire(params);
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
        //todo calculate discount before

        //Add Delivery fee
        products.add(
                createLineItem("Delivery",
                        "Fee for delivery provider",
                        1L,
                        paymentPriceConverter.convertPriceToLong(shippingEstimate)
                )
        );
        return products;
    }

    private SessionCreateParams.LineItem createLineItem(final String itemName,
                                                        final String itemDescription,
                                                        final long itemQuantity,
                                                        final long itemPrice) {
        final String PAYMENT_CURRENCY = "bgn";
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
