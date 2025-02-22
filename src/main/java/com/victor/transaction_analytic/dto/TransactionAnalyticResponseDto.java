package com.victor.transaction_analytic.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public record TransactionAnalyticResponseDto(
        BigDecimal highestSalesVolumeInADay,
        BigDecimal highestSalesValueInADay,
        String mostSoldProductByVolume,
        Map<YearMonth, String> highestSalesStaffByMonth,
        Integer highestHourByAverageTransactionVolume
) {
}
