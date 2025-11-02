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

package com.martinatanasov.computerstore.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.martinatanasov.computerstore.services.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/Status")
public class WebhookController {

    private final String STRIPE_WEBHOOK_SECRET;
    private final OrderService orderService;

    public WebhookController(@Value("${stripe.webhook.secret}") String webhookSecret, OrderService orderService) {
        if (webhookSecret == null) {
            throw new RuntimeException("\n\tStripe webhook secret is not initialized!");
        }
        this.STRIPE_WEBHOOK_SECRET = webhookSecret;
        this.orderService = orderService;
    }

    //Add stripe webhook IPs as origins https://docs.stripe.com/ips
    @CrossOrigin(origins = {
            "3.18.12.63", "3.130.192.231", "13.235.14.237",
            "13.235.122.149", "18.211.135.69", "35.154.171.200",
            "52.15.183.38", "54.88.130.119", "54.88.130.237",
            "54.187.174.169", "54.187.205.235", "54.187.216.72"})
    @PostMapping("/payment-complete")
    public ResponseEntity<String> updatePaymentStatus(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) throws JsonProcessingException {
        Event event = null;
        try {
            log.trace("\n\tPayload: {}", payload);
            log.trace("\n\tsigHeader: {}", sigHeader);
            event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);
            log.trace("\n\tEvent: {}", event);
        } catch (SignatureVerificationException e) {
            log.error("\n\tFailed signature verification");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        log.info("\n\tevent getType: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                updateOrderAndPayment(dataObjectDeserializer);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
            case "checkout.session.expired" -> {
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
            case "payment_intent.succeeded",
                 "payment_intent.payment_failed",
                 "payment_intent.created",
                 "charge.succeeded",
                 "charge.updated",
                 "issuer_declined",
                 "charge.failed",
                 "card_error" -> {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            default -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
    }

    private void updateOrderAndPayment(final EventDataObjectDeserializer dataObjectDeserializer) throws JsonProcessingException {
        if (dataObjectDeserializer.getObject().isPresent()) {
            StripeObject stripeObject = null;
            stripeObject = dataObjectDeserializer.getObject().get();
            log.trace("\n\tStripeObject: {}", stripeObject);

            final String rawJson = stripeObject.toJson();
            //Update Order and Payment
            if (rawJson != null) {
                boolean isUpdated = orderService.updateOrderAndPaymentAfterPaymentComplete(rawJson);
                log.trace("\n\tOrder status updated from webhook: {}", isUpdated);
            }
        } else {
            log.error("\n\tStripeObject cannot be extracted");
            /*
              Deserialization failed, probably due to an API version mismatch.
              Refer to the Javadoc documentation on `EventDataObjectDeserializer` for instructions on how to handle this case, or return an error here.
             */
        }
    }

}
