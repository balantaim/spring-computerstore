/*
 * Copyright 2024-2026 Martin Atanasov.
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

import com.martinatanasov.computerstore.entities.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    RoleDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Role findRoleByName(String roleName) {

        // retrieve/read from database using name
        TypedQuery<Role> theQuery = entityManager.createQuery("FROM Role WHERE authority=:roleName", Role.class);
        theQuery.setParameter("roleName", roleName);

        Role role = null;
        try {
            role = theQuery.getSingleResult();
        } catch (Exception e) {
            log.error("Current role is not found: {}", roleName);
        }
        return role;
    }

}