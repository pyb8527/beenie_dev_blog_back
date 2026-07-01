package com.beenie.backend.web.admin;

import com.beenie.backend.application.admin.DashboardService;
import com.beenie.backend.support.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ApiResponse<DashboardResponse> dashboard() {
        return ApiResponse.success(DashboardResponse.from(dashboardService.getDashboard()));
    }
}
