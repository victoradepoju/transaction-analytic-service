package com.victor.transaction_analytic.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY) // null values are excluded
public class ExceptionResponse {
    private String error;
    private Set<String> validationErrors;
    private Map<String, String> errors;
}
