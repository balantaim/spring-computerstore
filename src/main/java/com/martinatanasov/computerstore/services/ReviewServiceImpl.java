/*
 * Copyright 2024-2025 Martin Atanasov.
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

package com.martinatanasov.computerstore.services;

import com.martinatanasov.computerstore.entities.Product;
import com.martinatanasov.computerstore.entities.Review;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.ProductReviewsDTO;
import com.martinatanasov.computerstore.model.ReviewDTO;
import com.martinatanasov.computerstore.repositories.ProductRepository;
import com.martinatanasov.computerstore.repositories.ReviewRepository;
import com.martinatanasov.computerstore.repositories.UserDaoImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserDaoImpl userRepository;

    @Override
    public ProductReviewsDTO getProductAverageRating(final String email, final Product product) {
        User user;
        //Find user by email
        if (email != null && !email.isBlank()) {
            user = userRepository.findByUserName(email);
        } else {
            user = null;
        }

        //Get the reviews by Product object
        Set<Review> reviews = reviewRepository.findByProduct(product);
        if (reviews == null) {
            reviews = new HashSet<>();
        }

        //Check if the current user already voted for this product
        AtomicBoolean isAlreadyVoted = new AtomicBoolean(false);
        if (user != null && !reviews.isEmpty()) {
            reviews.forEach(item -> {
                if (Objects.equals(item.getUser().getId(), user.getId())) {
                    isAlreadyVoted.set(true);
                }
            });
        }

        //Get average vote rating if there are reviews or set 0.0
        final Double averageVote = formatAverageVote(reviews);

        return new ProductReviewsDTO(averageVote, reviews.size(), isAlreadyVoted.get());
    }

    @Override
    public ProductReviewsDTO getProductAverageRating(final Product product) {
        Set<Review> reviews = reviewRepository.findByProduct(product);

        final Double averageVote = formatAverageVote(reviews);

        return new ProductReviewsDTO(averageVote, reviews.size(), false);
    }

    @Override
    public boolean voteForProduct(final String username, final Integer productId, final Double vote) {
        User user = userRepository.findByUserName(username);
        Optional<Product> product = productRepository.findProductById(productId);
        if (user != null && product.isPresent() && vote != null) {
            //Check if user is already voted
            Optional<Review> getUserVote = reviewRepository.findByUserAndProduct(user, product.get());
            if (getUserVote.isEmpty()) {
                Review review = Review.builder()
                        .product(product.get())
                        .user(user)
                        .vote(vote)
                        .build();
                //Save the new review
                reviewRepository.save(review);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ReviewDTO> findReviewsByUser(final String email) {
        final User user = userRepository.findByUserName(email);
        if (user.getId() != null) {
            List<Review> data = reviewRepository.findByUserId(user.getId());
            return data.stream()
                    .map(i -> new ReviewDTO(
                            i.getVote(),
                            i.getCreationDate(),
                            i.getProduct().getProductName()
                    )).collect(Collectors.toList());
        }
        return null;
    }

    private double formatAverageVote(Set<Review> reviews) {
        double vote = reviews.stream()
                .mapToDouble(Review::getVote)
                .average()
                .orElse(0.0);
        return Math.floor(vote * 100) / 100;
    }

}
