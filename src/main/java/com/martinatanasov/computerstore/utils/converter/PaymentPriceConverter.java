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

package com.martinatanasov.computerstore.utils.converter;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentPriceConverter {

    public long convertPriceToLong(final BigDecimal oldPrice) {
        if (oldPrice != null) {
            final BigDecimal multipliedValue = oldPrice.multiply(new BigDecimal(100));
            return multipliedValue.longValue();
        }
        return 0L;
    }

}
