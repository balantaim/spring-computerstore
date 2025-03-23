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
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.repositories.OrderRepository;
import com.martinatanasov.computerstore.utils.converter.PaymentPriceConverter;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
public class SessionPaymentServiceImpl implements SessionPaymentService {

    private final String APP_DOMAIN_NAME;
    private final PaymentPriceConverter paymentPriceConverter;
    private final OrderRepository orderRepository;

    public SessionPaymentServiceImpl(@Value("${application.domain}") String appDomainName,
                                     PaymentPriceConverter paymentPriceConverter, OrderRepository orderRepository) {
        this.APP_DOMAIN_NAME = appDomainName;
        this.paymentPriceConverter = paymentPriceConverter;
        this.orderRepository = orderRepository;
    }

    @Override
    public String createCheckoutSession(final User user, final Long orderId) throws StripeException {
        final Optional<Order> order = orderRepository.getOrderById(orderId);
        if (user != null && order.isPresent()) {

            SessionCreateParams params = SessionCreateParams.builder()
                    //Add payment method type
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    //Set payment mode
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    //Set customer ID
                    .setCustomer(user.getCustomerId())
                    //Set language
                    .setLocale(SessionCreateParams.Locale.BG)
                    //Success redirect
                    .setSuccessUrl(APP_DOMAIN_NAME + "/Checkout/step-3")
                    //Failure redirect
                    .setCancelUrl(APP_DOMAIN_NAME + "/Checkout/step-3-cancel/" + order.get().getId())
                    //Add items
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


            Session session = Session.create(params);
            log.info("Session URL: {}", session.getUrl());
            return session.getUrl();
        }
        return null;
    }

    private List<SessionCreateParams.LineItem> convertOrderItemsToLineItems(Set<OrderItem> items) {
        List<SessionCreateParams.LineItem> products = new ArrayList<>();
        for (OrderItem index : items) {
            final long convertedPrice = paymentPriceConverter.convertPriceToLong(index.getPricePerUnit());
            products.add(
                    SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("bgn")
                                            .setUnitAmount(convertedPrice)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(index.getProduct().getProductName())
                                                            .setDescription(index.getProduct().getDescription())
                                                            .build()
                                            )
                                            .build()
                            )
                            .setQuantity(Long.valueOf(index.getQuantity()))
                            .build()
            );
        }
        log.info("\n\tStripe products: {}", products);

        //todo add shipping cost and calculate discount before

        return products;
    }

}
