package com.victor.transaction_analytic.service;

import com.victor.transaction_analytic.dto.TransactionAnalyticResponseDto;
import com.victor.transaction_analytic.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.*;

@Service
public class FolderTransactionAnalyticService {

    private static final Logger logger = LoggerFactory.getLogger(FolderTransactionAnalyticService.class);

    private final SingleFileTransactionAnalyticService singleFileService;

    public FolderTransactionAnalyticService(SingleFileTransactionAnalyticService singleFileService) {
        this.singleFileService = singleFileService;
    }

    public TransactionAnalyticResponseDto analyzeTransactions(String folderPath) {
        try {
            List<Path> files = Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .toList();

            if (files.isEmpty()) {
                logger.warn("No files found in the folder: {}", folderPath);
                throw new FileNotFoundException("Folder not found");
            }

            BigDecimal volume = highestSalesVolumeInADay(folderPath, true, files);
            BigDecimal value = highestSaleValueInADay(folderPath, true, files);
            Map<YearMonth, String> staffs = highestSalesStaffByMonth(folderPath, true, files);
            int hour = highestHourByAverageTransactionVolume(folderPath, true, files);
            String product = mostSoldProductByVolume(folderPath, true, files);

            return new TransactionAnalyticResponseDto(volume, value, product, staffs, hour);


        } catch (IOException e) {
            logger.error("Error reading folder: {}", folderPath, e);
        }

        return null;
    }

    public BigDecimal highestSaleValueInADay(String folderPath, boolean isAll, List<Path> passedFiles) {
        try {
            BigDecimal zero = getSalesValue(folderPath, isAll, passedFiles);
            if (zero != null) return zero;

        } catch (IOException e) {
            logger.error("Error reading folder: {}", folderPath, e);
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getSalesValue(String folderPath, boolean isAll, List<Path> passedFiles) throws IOException {
        List<Path> files;
        if (!isAll) {
            files = Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .toList();
        } else {
            files = passedFiles;
        }

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
        return null;
    }

    public BigDecimal highestSalesVolumeInADay(String folderPath, boolean isAll, List<Path> passedFiles) {
        try {
            List<Path> files;

            if (!isAll) {
                files = Files.list(Paths.get(folderPath))
                        .filter(Files::isRegularFile)
                        .toList();
            } else {
                files = passedFiles;
            }

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

    public String mostSoldProductByVolume(String folderPath, boolean isAll, List<Path> passedFiles) {
        try {
            List<Path> files;

            if (!isAll) {
                files = Files.list(Paths.get(folderPath))
                        .filter(Files::isRegularFile)
                        .toList();
            } else {
                files = passedFiles;
            }

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

    public Map<YearMonth, String> highestSalesStaffByMonth(String folderPath, boolean isAll, List<Path> passedFiles) {
        try {
            List<Path> files;

            if (!isAll) {
                files = Files.list(Paths.get(folderPath))
                        .filter(Files::isRegularFile)
                        .toList();
            } else {
                files = passedFiles;
            }

            if (files.isEmpty()) {
                logger.warn("No files found in the folder: {}", folderPath);
                return Collections.emptyMap();
            }

            // Map to store total sales volume by staff ID for each month
            Map<YearMonth, Map<String, BigDecimal>> monthStaffSalesMap = new HashMap<>();

            // Process each file
            for (Path file : files) {
                try {
                    // Load transactions from the file
                    singleFileService.loadTransactions(file.toString());
                    // Update the month-staff sales map with data from this file
                    updateMonthStaffSalesMap(singleFileService.getTransactions(), monthStaffSalesMap);
                } catch (IOException e) {
                    logger.error("Error processing file: {}", file, e);
                }
            }

            // Find the highest sales staff ID for each month
            Map<YearMonth, String> highestSalesStaffMap = new HashMap<>();
            for (Map.Entry<YearMonth, Map<String, BigDecimal>> entry : monthStaffSalesMap.entrySet()) {
                YearMonth month = entry.getKey();
                Map<String, BigDecimal> staffSalesMap = entry.getValue();

                Optional<Map.Entry<String, BigDecimal>> maxStaffEntry = staffSalesMap.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue());

                if (maxStaffEntry.isPresent()) {
                    String staffId = maxStaffEntry.get().getKey();
                    BigDecimal totalSales = maxStaffEntry.get().getValue();
                    highestSalesStaffMap.put(month, staffId);
                    logger.info("Highest sales staff for {}: {}, Total sales: {}", month, staffId, totalSales);
                }
            }

            return highestSalesStaffMap;

        } catch (IOException e) {
            logger.error("Error reading folder: {}", folderPath, e);
        }

        return Collections.emptyMap();
    }

    private void updateMonthStaffSalesMap(List<Transaction> transactions, Map<YearMonth, Map<String, BigDecimal>> monthStaffSalesMap) {
        for (Transaction transaction : transactions) {
            YearMonth month = YearMonth.from(transaction.getTransactionTime());
            String staffId = String.valueOf(transaction.getTransactionId()); // Assuming transaction ID is the staff ID
            BigDecimal saleAmount = transaction.getSaleAmount();

            // Get or create the staff sales map for the month
            Map<String, BigDecimal> staffSalesMap = monthStaffSalesMap.computeIfAbsent(month, k -> new HashMap<>());

            // Update the total sales volume for the staff ID
            staffSalesMap.merge(staffId, saleAmount, BigDecimal::add);
        }
    }

    public int highestHourByAverageTransactionVolume(String folderPath, boolean isAll, List<Path> passedFiles) {
        try {
            List<Path> files;

            if (!isAll) {
                files = Files.list(Paths.get(folderPath))
                        .filter(Files::isRegularFile)
                        .toList();
            } else {
                files = passedFiles;
            }

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
