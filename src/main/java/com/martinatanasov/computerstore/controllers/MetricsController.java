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

import com.sun.management.OperatingSystemMXBean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.management.ManagementFactory;


@PreAuthorize("hasRole('ADMIN')")
@Controller
public class MetricsController {

    @GetMapping("/Metrics")
    public String getMetrics(Model model) {
        model.addAttribute("maxRamLimit", calculateMaxRamLimit());
        model.addAttribute("ramUsage", calculateRamUsage());
        model.addAttribute("cpuUsage", calculateCpuUsage());

        return "Metrics/metrics";
    }

    private long calculateRamUsage() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        return (totalMemory - freeMemory) / (1024 * 1024);
    }

    private long calculateMaxRamLimit() {
        return Runtime.getRuntime().totalMemory() / (1024 * 1024);
    }

    private double calculateCpuUsage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osBean.getCpuLoad() * 100;
    }

}
