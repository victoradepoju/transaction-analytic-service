package com.victor.transaction_analytic.service;

import com.victor.transaction_analytic.helper.FileProcessor;
import com.victor.transaction_analytic.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;

@Service
public class SingleFileTransactionAnalyticService {

    private static final Logger logger = LoggerFactory.getLogger(SingleFileTransactionAnalyticService.class);

    private List<Transaction> transactions;

    public void loadTransactions(String filePath) throws IOException {
        transactions = FileProcessor.readTransactionsFromFile(filePath);
        logger.info("Loaded {} transactions from file: {}", transactions.size(), Path.of(filePath).getFileName());
    }

    public BigDecimal highestSalesValueInADay() {
        if (transactions == null || transactions.isEmpty()) {
            logger.warn("No transactions loaded. Ensure a file is loaded first.");
            return BigDecimal.ZERO;
        }

        Optional<Transaction> maxTransaction = transactions.stream()
                .max(Comparator.comparing(Transaction::getSaleAmount));

        if (maxTransaction.isPresent()) {
            BigDecimal maxSales = maxTransaction.get().getSaleAmount();
            logger.info("Highest sales value: {} occurred on {}", maxSales, maxTransaction.get().getTransactionTime().toLocalDate());
            return maxSales;
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal totalSalesVolumeInADay() {
        if (transactions == null || transactions.isEmpty()) {
            logger.warn("No transactions loaded. Ensure a file is loaded first.");
            return BigDecimal.ZERO;
        }

        BigDecimal totalSalesVolume = transactions.stream()
                .map(Transaction::getSaleAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Total sales volume for the day: {}", totalSalesVolume);
        return totalSalesVolume;
    }

    public Map<String, Integer> productVolumeInADay() {
        if (transactions == null || transactions.isEmpty()) {
            logger.warn("No transactions loaded. Ensure a file is loaded first.");
            return Collections.emptyMap();
        }

        Map<String, Integer> productVolumeMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String items = transaction.getItems();
            String[] itemPairs = items.substring(1, items.length() - 1).split("\\|");
            for (String pair : itemPairs) {
                String[] parts = pair.split(":");
                String productId = parts[0];
                int quantity = Integer.parseInt(parts[1]);
                productVolumeMap.merge(productId, quantity, Integer::sum);
            }
        }

        logger.info("Product volume for the day: {}", productVolumeMap);
        return productVolumeMap;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
