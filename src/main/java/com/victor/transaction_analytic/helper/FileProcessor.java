package com.victor.transaction_analytic.helper;

import com.victor.transaction_analytic.model.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FileProcessor {

    public static List<Transaction> readTransactionsFromFile(String filePath) throws IOException {
        return Files.lines(Path.of(filePath))
                .map(line -> {
                    String[] parts = line.split(",");
                    long transactionId = Long.parseLong(parts[0]);
                    LocalDateTime transactionTime = LocalDateTime.parse(parts[1]);
                    String items = parts[2];
                    BigDecimal saleAmount = new BigDecimal(parts[3]);
                    return new Transaction(transactionId, transactionTime, items, saleAmount);
                })
                .collect(Collectors.toList());
    }
}
