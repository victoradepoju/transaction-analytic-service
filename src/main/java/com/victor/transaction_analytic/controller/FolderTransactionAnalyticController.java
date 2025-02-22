package com.victor.transaction_analytic.controller;

import com.victor.transaction_analytic.dto.TransactionAnalyticResponseDto;
import com.victor.transaction_analytic.service.FolderTransactionAnalyticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction-analysis")
public class FolderTransactionAnalyticController {

    private final FolderTransactionAnalyticService folderAnalyticService;

    public FolderTransactionAnalyticController(FolderTransactionAnalyticService folderAnalyticService) {
        this.folderAnalyticService = folderAnalyticService;
    }

    @PostMapping("analyze-transactions")
    public ResponseEntity<TransactionAnalyticResponseDto> analyzeTransactions(@RequestParam String folderPath) {
        return ResponseEntity.ok(folderAnalyticService.analyzeTransactions(folderPath));
    }

    @PostMapping("/highest-sales-volume")
    public ResponseEntity<BigDecimal> highestSalesVolumeInADay(@RequestParam String folderPath) {
        BigDecimal highestSalesVolume = folderAnalyticService.highestSalesVolumeInADay(folderPath, false, List.of());
        return ResponseEntity.ok(highestSalesVolume);
    }

    @PostMapping("/highest-sales-value")
    public ResponseEntity<BigDecimal> highestSaleValueInADay(@RequestParam String folderPath) {
        BigDecimal highestSales = folderAnalyticService.highestSaleValueInADay(folderPath, false, List.of());
        return ResponseEntity.ok(highestSales);
    }

    @PostMapping("/most-sold-product")
    public ResponseEntity<String> mostSoldProductByVolume(@RequestParam String folderPath) {
        String mostSoldProduct = folderAnalyticService.mostSoldProductByVolume(folderPath, false, List.of());
        return ResponseEntity.ok(mostSoldProduct);
    }

    @PostMapping("/highest-sales-staff-by-month")
    public ResponseEntity<Map<YearMonth, String>> highestSalesStaffByMonth(@RequestParam String folderPath) {
        Map<YearMonth, String> highestSalesStaffMap = folderAnalyticService.highestSalesStaffByMonth(folderPath, false, List.of());
        return ResponseEntity.ok(highestSalesStaffMap);
    }

    @PostMapping("/highest-hour-by-average-volume")
    public ResponseEntity<Integer> highestHourByAverageTransactionVolume(@RequestParam String folderPath) {
        int highestHour = folderAnalyticService.highestHourByAverageTransactionVolume(folderPath, false, List.of());
        return ResponseEntity.ok(highestHour);
    }
}
