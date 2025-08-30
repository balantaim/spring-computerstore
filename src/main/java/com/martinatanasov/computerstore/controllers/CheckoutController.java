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
import com.martinatanasov.computerstore.entities.Payment;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.Carrier;
import com.martinatanasov.computerstore.model.OrderStatus;
import com.martinatanasov.computerstore.model.PaymentStatus;
import com.martinatanasov.computerstore.repositories.UserDao;
import com.martinatanasov.computerstore.services.CartService;
import com.martinatanasov.computerstore.services.DeliveryService;
import com.martinatanasov.computerstore.services.OrderService;
import com.martinatanasov.computerstore.services.payments.PaymentCustomerService;
import com.martinatanasov.computerstore.services.payments.SessionPaymentService;
import com.martinatanasov.computerstore.utils.converter.AddressConverter;
import com.martinatanasov.computerstore.utils.converter.TestNotificationsState;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    private final UserDao userDao;
    private final AddressConverter addressConverter;
    private final PaymentCustomerService paymentCustomerService;
    private final SessionPaymentService sessionPaymentService;
    private final OrderService orderService;
    private final CartService cartService;
    private final DeliveryService deliveryService;
    private final TestNotificationsState testNotificationsState;

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
                if (!isOrderCreated) {
                    return GLOBAL_ERROR_PAGE;
                }
            }

            model.addAttribute("address", addressConverter.getDeliveryAddress(user));
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
                model.addAttribute("address", addressConverter.getDeliveryAddress(user));
                return "Checkout/checkout-step-1";
            }
        }
        return GLOBAL_ERROR_PAGE;
    }

    @PostMapping("/step-2")
    public String initiateCheckoutPayment(@RequestParam("carrier") String carrier,
                                          Model model,
                                          HttpSession session) {
        final Carrier carrierName = Objects.equals(carrier, Carrier.ECONT.name()) ? Carrier.ECONT : Carrier.SPEEDY;
        final User user = userDao.findByUserName(getUserName());
        final Optional<Order> order = getInitialOrder(user.getId());
        if (order.isPresent()) {
            final String trackingNumber = deliveryService.createDelivery().toString();
            final Order updatedOrder = orderService.updateOrderAndEntities(user, order.get(), carrierName, trackingNumber);
            if (updatedOrder != null) {
                //Clear user's cart
                cartService.deleteAllItems(user.getEmail());
                //Update Cart count
                session.setAttribute("cart-items-count", 0);
                //Update Order count
                session.setAttribute("orders-count", orderService.getUnfinishedOrdersCount(getUserName()));

                final DecimalFormat formatter = new DecimalFormat("#0.00");
                model.addAttribute("carrier", carrierName);
                model.addAttribute("finalPrice", formatter.format(updatedOrder.getTotalAmount()));
                model.addAttribute("orderId", updatedOrder.getId());
                model.addAttribute("orderCreated", true);
                if (testNotificationsState.isNotificationsActive()) {
                    model.addAttribute("testNotification", true);
                }

                return "Checkout/checkout-step-2";
            }
        }
        return GLOBAL_ERROR_PAGE;
    }

    @PostMapping("/initiate-payment")
    public String initiatePaymentSession(@RequestParam("orderId") Long orderId) throws StripeException {
        if (orderId != null) {
            final User user = userDao.findByUserName(getUserName());
            final Session session = sessionPaymentService.createCheckoutSession(user, orderId);
            if (session != null) {
                return "redirect:" + session.getUrl();
            }
        }
        return GLOBAL_ERROR_PAGE;
    }

    @GetMapping("/step-3")
    public String getCheckoutConfirmation(HttpSession session) {
        final User user = userDao.findByUserName(getUserName());
        //Update Cart count and Order count required in top navigation bar to the session
        final int cartCount = cartService.getCartItemsCount(user.getId());
        //Update Cart count
        session.setAttribute("cart-items-count", cartCount > 0 ? cartCount : null);
        //Update Order count
        session.setAttribute("orders-count", orderService.getUnfinishedOrdersCount(getUserName()));

        return "Checkout/checkout-step-3";
    }

    @GetMapping("/step-3-cancel/{orderId}")
    public String getFailureCheckoutConfirmation(Model model,
                                                 @PathVariable("orderId") Long orderId,
                                                 @RequestParam(value = "payment_intent_id", required = false) String paymentIntendId,
                                                 HttpSession session) {
        Optional<Order> order = orderService.getOrderById(orderId);
        if (order.isPresent()) {
            if (order.get().getStatus() == OrderStatus.PAYMENT_REQUIRED) {
                //Update order status
                order.get().setStatus(OrderStatus.ORDER_ABORTED);
                //Update payment status
                Payment payment = order.get().getPayment();
                payment.setPaymentStatus(PaymentStatus.ABORTED);
                order.get().setPayment(payment);
                //Save updated order
                orderService.abortOrder(order.get());

                session.setAttribute("orders-count", orderService.getUnfinishedOrdersCount(getUserName()));
            }
        }
        if (paymentIntendId != null) {
            log.error("\n\tError: payment intend ID = {}", paymentIntendId);
            model.addAttribute("error", paymentIntendId);
        }
        return "Checkout/checkout-step-3-cancel";
    }

    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private Optional<Order> getInitialOrder(Long userId) {
        return orderService.getFirstByStatusAndUserId(userId, OrderStatus.NEW_ORDER);
    }

}
