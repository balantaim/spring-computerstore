/*
 * Copyright 2024 Martin Atanasov.
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

package com.martinatanasov.computerstore.repositories;

import com.martinatanasov.computerstore.entities.Cart;
import com.martinatanasov.computerstore.entities.Payment;
import com.martinatanasov.computerstore.entities.Shipment;
import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.UserFailedAttempts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private final EntityManager entityManager;

    @Autowired
    UserDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User findByUserName(String email) {
        User user = null;
        if (email == null || email.length() < 3 || email.length() > 50){
            return user;
        }
        // retrieve/read from database using username
        TypedQuery<User> theQuery = entityManager.createQuery("FROM User WHERE email=:userEmail AND enabled=true", User.class);
        theQuery.setParameter("userEmail", email);

        try {
            user = theQuery.getSingleResult();
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    @Override
    public List<Shipment> findShipmentsByUserId(Long id) {
        TypedQuery<Shipment> query = entityManager.createQuery("FROM Shipment WHERE user_id=:userId", Shipment.class);
        query.setParameter("userId", id);
        return query.getResultList();
    }

    @Override
    public List<Cart> findCartsByUserId(Long id) {
        TypedQuery<Cart> query = entityManager.createQuery("FROM Cart WHERE user_id=:userId", Cart.class);
        query.setParameter("userId", id);
        return query.getResultList();
    }

    @Override
    public List<Payment> findPaymentsByUserId(Long id) {
        TypedQuery<Payment> query = entityManager.createQuery("FROM Payment WHERE user_id=:userId", Payment.class);
        query.setParameter("userId", id);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void save(User user) {
        // create the user
        entityManager.merge(user);
    }

    @Override
    @Transactional
    public void updateUserShippingDetails(User user) {
//        int rowsUpdated = entityManager.createQuery("UPDATE User WHERE id=`"
//                        + user.getId() +
//                        "` SET first_name=`" +
//                user.getFirstName() +
//                "`, last_name=`" +
//                user.getLastName() +
//                "`, phone_number=`" +
//                user.getPhoneNumber() +
//                "`, country=`" +
//                user.getCounty() +
//                "`")
//                .executeUpdate();

        entityManager.merge(user);
    }

    @Override
    public Object[] getUserLoginAttempts(final String email) {
        if(email == null || email.length() < 3 || email.length() > 50){
            return null;
        }
        Query query = entityManager.createQuery(
                "SELECT attempts, enabled, lockDate FROM User u WHERE u.email=:email"
        );
        query.setParameter("email", email);
        return (Object[]) query.getSingleResult();
    }

    @Override
    @Transactional
    public void setLoginFailedAttempt(final String email, UserFailedAttempts info) throws EmptyResultDataAccessException {
        if(email == null || email.length() < 3 || email.length() > 50){
            return;
        }
        User user = findByUserName(email);
        if(user != null){
            Query query = entityManager.createNativeQuery(
                    "UPDATE users SET attempts = :attempts, enabled = :enabled, lock_date = :lock_date WHERE email = :email"
            );
            query.setParameter("attempts", info.attempts());
            query.setParameter("enabled", info.enabled());
            query.setParameter("lock_date", info.lockDate());
            query.setParameter("email", email);
            // Execute the update to insert the record
            query.executeUpdate();
        }
    }

}
