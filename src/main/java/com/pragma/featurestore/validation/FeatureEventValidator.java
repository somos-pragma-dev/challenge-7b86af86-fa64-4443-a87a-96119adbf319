package com.pragma.featurestore.validation;

import com.pragma.featurestore.dto.FeatureEvent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FeatureEventValidator {
    
    private final Validator validator;
    private final Duration maxEventAge;
    private final Duration maxFutureDrift;
    
    public FeatureEventValidator() {
        this(Duration.ofHours(24), Duration.ofMinutes(5));
    }
    
    public FeatureEventValidator(Duration maxEventAge, Duration maxFutureDrift) {
        ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(false)
            .addProperty("hibernate.validator.fail_fast", "false")
            .buildValidatorFactory();
        
        this.validator = factory.getValidator();
        this.maxEventAge = maxEventAge;
        this.maxFutureDrift = maxFutureDrift;
        log.info("FeatureEventValidator initialized with maxEventAge={}, maxFutureDrift={}", 
                 maxEventAge, maxFutureDrift);
    }
    
    public ValidationResult validate(FeatureEvent event) {
        if (event == null) {
            return ValidationResult.invalid("FeatureEvent cannot be null", Collections.emptyList());
        }
        
        Set<ConstraintViolation<FeatureEvent>> violations = validator.validate(event);
        List<String> errors = violations.stream()
            .map(this::formatViolation)
            .collect(Collectors.toList());
        
        if (!errors.isEmpty()) {
            return ValidationResult.invalid("Bean validation failed", errors);
        }
        
        List<String> temporalErrors = validateTemporalConsistency(event);
        errors.addAll(temporalErrors);
        
        if (!errors.isEmpty()) {
            return ValidationResult.invalid("Temporal validation failed", errors);
        }
        
        List<String> semanticErrors = validateSemanticConsistency(event);
        errors.addAll(semanticErrors);
        
        if (!errors.isEmpty()) {
            return ValidationResult.invalid("Semantic validation failed", errors);
        }
        
        return ValidationResult.valid();
    }
    
    private List<String> validateTemporalConsistency(FeatureEvent event) {
        List<String> errors = new ArrayList<>();
        
        if (event.getTimestamp() == null) {
            errors.add("Event timestamp is required");
            return errors;
        }
        
        Instant now = Instant.now();
        Instant eventTime = event.getTimestamp();
        
        if (eventTime.isAfter(now.plus(maxFutureDrift))) {
            errors.add(String.format("Event timestamp %s is too far in the future (max drift: %s)",
                                    eventTime, maxFutureDrift));
        }
        
        if (eventTime.isBefore(now.minus(maxEventAge))) {
            errors.add(String.format("Event timestamp %s is too old (max age: %s)",
                                    eventTime, maxEventAge));
        }
        
        if (event.getProcessingTimestamp() != null) {
            if (event.getProcessingTimestamp().isBefore(eventTime)) {
                errors.add("Processing timestamp cannot be before event timestamp");
            }
            
            Duration processingDelay = Duration.between(eventTime, event.getProcessingTimestamp());
            if (processingDelay.toMinutes() > 60) {
                log.warn("High processing delay detected: {} minutes for event {}",
                        processingDelay.toMinutes(), event.getEventId());
            }
        }
        
        return errors;
    }
    
    private List<String> validateSemanticConsistency(FeatureEvent event) {
        List<String> errors = new ArrayList<>();
        
        if (event.getFeatureName() != null && event.getFeatureName().contains("..")) {
            errors.add("Feature name cannot contain consecutive dots");
        }
        
        if (event.getVersion() != null) {
            String version = event.getVersion();
            if (!version.matches("^\\d+\\.\\d+\\.\\d+$")) {
                errors.add(String.format("Invalid version format '%s' (expected MAJOR.MINOR.PATCH)", version));
            }
            
            String[] parts = version.split("\\.");
            try {
                int major = Integer.parseInt(parts[0]);
                if (major < 0) {
                    errors.add("Major version cannot be negative");
                }
                if (major >= 100) {
                    log.warn("Unusually high major version: {}", major);
                }
            } catch (NumberFormatException e) {
                errors.add("Invalid major version number");
            }
        }
        
        if (event.getFeatureValue() == null) {
            errors.add("Feature value cannot be null");
        } else if (event.getFeatureValue() instanceof Number) {
            Number numValue = (Number) event.getFeatureValue();
            if (numValue instanceof Double || numValue instanceof Float) {
                double doubleValue = numValue.doubleValue();
                if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
                    errors.add("Feature value cannot be NaN or Infinite");
                }
            }
        }
        
        if (event.getUserId() != null && event.getUserId().isBlank()) {
            errors.add("User ID cannot be blank when provided");
        }
        
        if (event.getMetadata() != null) {
            for (Map.Entry<String, Object> entry : event.getMetadata().entrySet()) {
                if (entry.getKey() == null || entry.getKey().isBlank()) {
                    errors.add("Metadata keys cannot be null or blank");
                }
                if (entry.getValue() != null && !(entry.getValue() instanceof String) 
                    && !(entry.getValue() instanceof Number) 
                    && !(entry.getValue() instanceof Boolean)
                    && !(entry.getValue() instanceof List)) {
                    log.debug("Metadata value for key '{}' is not a simple type: {}", 
                             entry.getKey(), entry.getValue().getClass());
                }
            }
        }
        
        return errors;
    }
    
    private String formatViolation(ConstraintViolation<FeatureEvent> violation) {
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();
        Object invalidValue = violation.getInvalidValue();
        
        if (invalidValue != null) {
            return String.format("%s: %s (was: %s)", field, message, invalidValue);
        }
        return String.format("%s: %s", field, message);
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final String summary;
        private final List<String> errors;
        
        private ValidationResult(boolean valid, String summary, List<String> errors) {
            this.valid = valid;
            this.summary = summary;
            this.errors = Collections.unmodifiableList(errors);
        }
        
        public static ValidationResult valid() {
            return new ValidationResult(true, "Validation passed", Collections.emptyList());
        }
        
        public static ValidationResult invalid(String summary, List<String> errors) {
            return new ValidationResult(false, summary, errors);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getSummary() {
            return summary;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public String getErrorsAsString() {
            return String.join("; ", errors);
        }
    }
}