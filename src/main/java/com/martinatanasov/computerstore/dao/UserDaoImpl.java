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

package com.martinatanasov.computerstore.dao;

import com.martinatanasov.computerstore.entity.Cart;
import com.martinatanasov.computerstore.entity.Payment;
import com.martinatanasov.computerstore.entity.Shipment;
import com.martinatanasov.computerstore.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private final EntityManager entityManager;

    @Autowired
    public UserDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User findByUserName(String email) {

        // retrieve/read from database using username
        TypedQuery<User> theQuery = entityManager.createQuery("FROM User WHERE email=:userEmail AND enabled=true", User.class);
        theQuery.setParameter("userEmail", email);

        User user = null;
        try {
            user = theQuery.getSingleResult();

//            System.out.println( "User email: " + user.getEmail());
//            List<Shipment> data = findShipmentsByUserId(user.getId());
//            System.out.println( "User shipments: " + data);
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

}
