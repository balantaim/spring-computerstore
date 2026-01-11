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

package com.martinatanasov.computerstore.services;

import com.martinatanasov.computerstore.entities.User;
import com.martinatanasov.computerstore.model.UserFailedAttempts;
import com.martinatanasov.computerstore.repositories.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Service("bruteForceProtectionService")
public class BruteForceProtectionServiceImpl implements BruteForceProtectionService {

    @Value("${security.max.login.attempts}")
    private Byte maxFailedLogins;
    private final UserDao userDao;

    @Override
    public void registerLoginFailure(final String username) {
        User user = userDao.findByUserName(username);
        if (user != null) {
            boolean enabled = true;
            Byte count = user.getAttempts();
            if (count < maxFailedLogins) {
                count++;
            }
            if (count >= maxFailedLogins) {
                enabled = false;
            }
            userDao.setLoginFailedAttempt(username, new UserFailedAttempts(count,
                    enabled,
                    new Timestamp(System.currentTimeMillis())));
        }
    }

    @Override
    public void resetLoginFailureCounter(final String username) {
        userDao.setLoginFailedAttempt(username, new UserFailedAttempts((byte) 0,
                Boolean.TRUE,
                new Timestamp(System.currentTimeMillis())));
    }

}
