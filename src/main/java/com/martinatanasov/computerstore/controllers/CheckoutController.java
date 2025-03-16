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

import com.martinatanasov.computerstore.entities.Cart;
import com.martinatanasov.computerstore.entities.Order;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.Carrier;
import com.martinatanasov.computerstore.model.OrderStatus;
import com.martinatanasov.computerstore.repositories.UserDaoImpl;
import com.martinatanasov.computerstore.services.CartService;
import com.martinatanasov.computerstore.services.DeliveryService;
import com.martinatanasov.computerstore.services.OrderService;
import com.martinatanasov.computerstore.services.payments.PaymentCustomerServiceImpl;
import com.martinatanasov.computerstore.utils.converter.AddressConverter;
import com.stripe.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;

import static com.martinatanasov.computerstore.controllers.CustomErrorController.GLOBAL_ERROR_PAGE;
import static com.martinatanasov.computerstore.controllers.CustomErrorController.NOT_FOUND_PAGE;

@Slf4j
@Controller
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/Checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final UserDaoImpl userDao;
    private final AddressConverter addressConverter;
    private final PaymentCustomerServiceImpl paymentCustomerService;
    private final OrderService orderService;
    private final CartService cartService;
    private final DeliveryService deliveryService;

    @PostMapping("/step-1")
    public String initiateCheckoutInformation(Model model) {
        User user = userDao.findByUserName(getUserName());
        if (user != null) {
            //Verify user's customerId
            if (user.getCustomerId() == null || user.getCustomerId().isEmpty()) {
                Customer customer = paymentCustomerService.createCustomer(user);
                if (customer == null) {
                    throw new RuntimeException("Customer is not created!");
                } else {
                    user.setCustomerId(customer.getId());
                    userDao.save(user);
                    log.info("Customer ID is set to the user. Customer ID: {}", customer.getId());
                }
            }
            //Create Order and its OrderItems
            final Optional<Order> order = getInitialOrder(user.getId());
            //Create a new Order
            if (order.isEmpty()) {
                //Get cart items
                Iterable<Cart> cartItems = cartService.getAllItems(user.getEmail());
                if (cartItems == null) {
                    return NOT_FOUND_PAGE;
                }
                //Create Initial Order
                boolean isOrderCreated = orderService.createNewOrder(user, cartItems);
                log.info("\n\t Order from controller: ---");
                if (!isOrderCreated) {
                    return GLOBAL_ERROR_PAGE;
                }
            }

            model.addAttribute("address", addressConverter.userAddressToDTO(user));
            return "Checkout/checkout-step-1";
        }
        return GLOBAL_ERROR_PAGE;
    }

    @GetMapping("/step-1")
    public String refreshCheckoutInformation(Model model) {
        final User user = userDao.findByUserName(getUserName());
        //Check if user has a new order
        if (user != null) {
            Optional<Order> order = getInitialOrder(user.getId());
            if (order.isPresent()) {

                model.addAttribute("address", addressConverter.userAddressToDTO(user));
                return "Checkout/checkout-step-1";
            }
        }
        return GLOBAL_ERROR_PAGE;
    }

    @PostMapping("/step-2")
    public String initiateCheckoutPayment(@RequestParam("carrier") String carrier,
                                          Model model) {
        final Carrier carrierName = Objects.equals(carrier, Carrier.ECONT.name()) ? Carrier.ECONT : Carrier.SPEEDY;
        final User user = userDao.findByUserName(getUserName());
        final Optional<Order> order = getInitialOrder(user.getId());
        if(order.isPresent()) {
            final String trackingNumber = deliveryService.createDelivery().toString();
            final Order updatedOrder = orderService.updateOrderAndEntities(user, order.get(), carrierName, trackingNumber);
            if(updatedOrder != null) {
                //Clear user's cart
                cartService.deleteAllItems(user.getEmail());

                final DecimalFormat formatter = new DecimalFormat("#0.00");
                model.addAttribute("carrier", carrierName);
                model.addAttribute("finalPrice", formatter.format(updatedOrder.getTotalAmount()));
                model.addAttribute("orderCreated", true);

                return "Checkout/checkout-step-2";
            }
        }
        return GLOBAL_ERROR_PAGE;
    }

    @GetMapping("/step-3")
    public String getCheckoutConfirmation() {
        return "Checkout/checkout-step-3";
    }

    @GetMapping("/step-3-failure")
    public String getFailureCheckoutConfirmation(Model model,
                                                 @RequestParam("payment_intent_id") String paymentIntendId) {
        log.info("\n\t ------> Error: {}", paymentIntendId);
        model.addAttribute("error", paymentIntendId);
        return "Checkout/checkout-step-3-failure";
    }

    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private Optional<Order> getInitialOrder(Long userId) {
        return orderService.getFirstByStatusAndUserId(userId, OrderStatus.NEW_ORDER);
    }

//    private BigDecimal getTotalAmount(Set<OrderItem> orderItems) {
//        BigDecimal orderTotal = BigDecimal.ZERO;
//        for(OrderItem i: orderItems) {
//            orderTotal = orderTotal.add(i.getProduct().getPrice());
//        }
//        //Add shipping tax
//        orderTotal = orderTotal.add(shippingEstimate);
//        return orderTotal;
//    }

}
