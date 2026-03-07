/*
 * Copyright 2026 Martin Atanasov.
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

package com.martinatanasov.computerstore.repositories.persistentlogin;

import com.martinatanasov.computerstore.entities.PersistentLogin;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor
@Repository
public class CustomizedPersistentTokenRepository implements PersistentTokenRepository {

    private final PersistentLoginRepository repository;

    @Override
    @Transactional
    public void createNewToken(PersistentRememberMeToken token) {
        PersistentLogin entity = new PersistentLogin();
        entity.setSeries(token.getSeries());
        entity.setUsername(token.getUsername());
        entity.setToken(token.getTokenValue());
        entity.setLastUsed(token.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime());
        repository.save(entity);
    }

    @Override
    @Transactional
    public void updateToken(@NonNull String series, @NonNull String tokenValue, @NonNull Date lastUsed) {
        repository.findById(series).ifPresent(entity -> {
            entity.setToken(tokenValue);
            entity.setLastUsed(lastUsed.toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
            repository.save(entity);
        });
    }

    @Override
    @Nullable
    public PersistentRememberMeToken getTokenForSeries(@NonNull String seriesId) {
        return repository.findById(seriesId)
                .map(entity -> new PersistentRememberMeToken(
                        entity.getUsername(),
                        entity.getSeries(),
                        entity.getToken(),
                        Date.from(entity.getLastUsed()
                                .atZone(ZoneId.systemDefault()).toInstant())))
                .orElse(null);
    }

    @Override
    @Transactional
    public void removeUserTokens(@NonNull String username) {
        repository.deleteByUsername(username);
    }

}
