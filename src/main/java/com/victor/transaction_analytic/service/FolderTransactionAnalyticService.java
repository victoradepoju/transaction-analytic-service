package com.victor.transaction_analytic.service;

import com.victor.transaction_analytic.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FolderTransactionAnalyticService {

    private static final Logger logger = LoggerFactory.getLogger(FolderTransactionAnalyticService.class);

    private final SingleFileTransactionAnalyticService singleFileService;

    public FolderTransactionAnalyticService(SingleFileTransactionAnalyticService singleFileService) {
        this.singleFileService = singleFileService;
    }

    public BigDecimal analyzeFolder(String folderPath) {
        try {
            List<Path> files = Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .toList();

            if (files.isEmpty()) {
                logger.warn("No files found in the folder: {}", folderPath);
                return BigDecimal.ZERO;
            }

            Optional<BigDecimal> maxSales = files.stream()
                    .map(file -> {
                        try {
                            singleFileService.loadTransactions(file.toString());
                            return singleFileService.highestSalesValueInADay();
                        } catch (IOException e) {
                            logger.error("Error processing file: {}", file, e);
                            return BigDecimal.ZERO;
                        }
                    })
                    .max(Comparator.naturalOrder());

            if (maxSales.isPresent()) {
                BigDecimal result = maxSales.get();
                logger.info("Highest sales value across all days: {}", result);
                return result;
            }

        } catch (IOException e) {
            logger.error("Error reading folder: {}", folderPath, e);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Analyzes all files in the folder and returns the highest sales volume (sum of sales) in a day.
     *
     * @param folderPath The path to the folder containing transaction files.
     * @return The highest sales volume in a day.
     */
    public BigDecimal highestSalesVolumeInADay(String folderPath) {
        try {
            List<Path> files = Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .toList();

            if (files.isEmpty()) {
                logger.warn("No files found in the folder: {}", folderPath);
                return BigDecimal.ZERO;
            }

            Optional<BigDecimal> maxSalesVolume = files.stream()
                    .map(file -> {
                        try {
                            singleFileService.loadTransactions(file.toString());
                            return singleFileService.totalSalesVolumeInADay();
                        } catch (IOException e) {
                            logger.error("Error processing file: {}", file, e);
                            return BigDecimal.ZERO;
                        }
                    })
                    .max(Comparator.naturalOrder());

            if (maxSalesVolume.isPresent()) {
                BigDecimal result = maxSalesVolume.get();
                logger.info("Highest sales volume in a day: {}", result);
                return result;
            }

        } catch (IOException e) {
            logger.error("Error reading folder: {}", folderPath, e);
        }

        return BigDecimal.ZERO;
    }

    public String mostSoldProductByVolume(String folderPath) {
        try {
            List<Path> files = Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .toList();

            if (files.isEmpty()) {
                logger.warn("No files found in the folder: {}", folderPath);
                return null;
            }

            Map<String, Integer> productVolumeMap = new HashMap<>();

            for (Path file : files) {
                try {
                    singleFileService.loadTransactions(file.toString());
                    Map<String, Integer> fileProductVolumeMap = singleFileService.productVolumeInADay();
                    fileProductVolumeMap.forEach((productId, quantity) ->
                            productVolumeMap.merge(productId, quantity, Integer::sum));
                } catch (IOException e) {
                    logger.error("Error processing file: {}", file, e);
                }
            }

            Optional<Map.Entry<String, Integer>> mostSoldProduct = productVolumeMap.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());

            if (mostSoldProduct.isPresent()) {
                String productId = mostSoldProduct.get().getKey();
                int totalQuantity = mostSoldProduct.get().getValue();
                logger.info("Most sold product ID: {}, Total quantity sold: {}", productId, totalQuantity);
                return productId;
            }

        } catch (IOException e) {
            logger.error("Error reading folder: {}", folderPath, e);
        }

        return null;
    }

    public int highestHourByAverageTransactionVolume(String folderPath) {
        try {
            // Get all files in the folder
            List<Path> files = Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .toList();

            if (files.isEmpty()) {
                logger.warn("No files found in the folder: {}", folderPath);
                return -1;
            }

            // Map to store hour and total transaction volume
            Map<Integer, BigDecimal> hourTotalVolumeMap = new HashMap<>();
            // Map to store hour and transaction count
            Map<Integer, Integer> hourTransactionCountMap = new HashMap<>();

            // Process each file
            for (Path file : files) {
                try {
                    // Load transactions from the file
                    singleFileService.loadTransactions(file.toString());
                    // Update the hour maps with data from this file
                    updateHourMaps(singleFileService.getTransactions(), hourTotalVolumeMap, hourTransactionCountMap);
                } catch (IOException e) {
                    logger.error("Error processing file: {}", file, e);
                }
            }

            // Calculate the average transaction volume for each hour
            Map<Integer, BigDecimal> hourAverageVolumeMap = new HashMap<>();
            for (Map.Entry<Integer, BigDecimal> entry : hourTotalVolumeMap.entrySet()) {
                int hour = entry.getKey();
                BigDecimal totalVolume = entry.getValue();
                int transactionCount = hourTransactionCountMap.get(hour);
                BigDecimal averageVolume = totalVolume.divide(BigDecimal.valueOf(transactionCount), 2, RoundingMode.HALF_UP);
                hourAverageVolumeMap.put(hour, averageVolume);
            }

            // Find the hour with the highest average transaction volume
            Optional<Map.Entry<Integer, BigDecimal>> maxHourEntry = hourAverageVolumeMap.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());

            if (maxHourEntry.isPresent()) {
                int hour = maxHourEntry.get().getKey();
                BigDecimal averageVolume = maxHourEntry.get().getValue();
                logger.info("Highest hour by average transaction volume: {}:00, Average volume: {}", hour, averageVolume);
                return hour;
            }

        } catch (IOException e) {
            logger.error("Error reading folder: {}", folderPath, e);
        }

        return -1;
    }

    private void updateHourMaps(List<Transaction> transactions, Map<Integer, BigDecimal> hourTotalVolumeMap, Map<Integer, Integer> hourTransactionCountMap) {
        for (Transaction transaction : transactions) {
            int hour = transaction.getTransactionTime().getHour();
            BigDecimal saleAmount = transaction.getSaleAmount();

            // Update total volume for the hour
            hourTotalVolumeMap.merge(hour, saleAmount, BigDecimal::add);
            // Update transaction count for the hour
            hourTransactionCountMap.merge(hour, 1, Integer::sum);
        }
    }
}
