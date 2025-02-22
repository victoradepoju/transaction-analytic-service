package com.victor.transaction_analytic.controller;

import com.victor.transaction_analytic.service.FolderTransactionAnalyticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transaction-analysis")
public class FolderTransactionAnalyticController {

    private final FolderTransactionAnalyticService folderAnalyticService;

    public FolderTransactionAnalyticController(FolderTransactionAnalyticService folderAnalyticService) {
        this.folderAnalyticService = folderAnalyticService;
    }

    @PostMapping("/analyze-folder")
    public ResponseEntity<BigDecimal> analyzeFolder(@RequestParam String folderPath) {
        BigDecimal highestSales = folderAnalyticService.analyzeFolder(folderPath);
        return ResponseEntity.ok(highestSales);
    }

    @PostMapping("/highest-sales-volume")
    public ResponseEntity<BigDecimal> highestSalesVolumeInADay(@RequestParam String folderPath) {
        BigDecimal highestSalesVolume = folderAnalyticService.highestSalesVolumeInADay(folderPath);
        return ResponseEntity.ok(highestSalesVolume);
    }

    @PostMapping("/most-sold-product")
    public ResponseEntity<String> mostSoldProductByVolume(@RequestParam String folderPath) {
        String mostSoldProduct = folderAnalyticService.mostSoldProductByVolume(folderPath);
        return ResponseEntity.ok(mostSoldProduct);
    }

    @PostMapping("/highest-hour-by-average-volume")
    public ResponseEntity<Integer> highestHourByAverageTransactionVolume(@RequestParam String folderPath) {
        int highestHour = folderAnalyticService.highestHourByAverageTransactionVolume(folderPath);
        return ResponseEntity.ok(highestHour);
    }
}
