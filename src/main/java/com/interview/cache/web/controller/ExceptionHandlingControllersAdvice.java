package com.interview.cache.web.controller;

import com.interview.cache.web.protocol.response.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlingControllersAdvice {

    private final static Logger LOG = LoggerFactory.getLogger(ExceptionHandlingControllersAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(HttpServletRequest req, MethodArgumentNotValidException ex) {
        BindingResult bindResult = ex.getBindingResult();

// @formatter:off
        final Map<String, List<String>> errors = bindResult.getAllErrors()
                .stream()
                .collect(Collectors.groupingBy(e -> e instanceof FieldError ? ((FieldError) e).getField() : e.getObjectName(),
                                               Collectors.mapping(ObjectError::getDefaultMessage,
                                                                  Collectors.toList())));
// @formatter:on
        final String formattedErrors = formatErrors(errors);
        LOG.info("Validation conflicts on " + formatRequest(req) + ". " + formattedErrors, ex);

        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), formattedErrors, req.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(HttpServletRequest req, ConstraintViolationException ex) {
// @formatter:off
        Map<String, Collection<String>> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(e -> e.getPropertyPath().toString(),
                                          e -> Collections.singletonList(e.getMessage())));
// @formatter:on
        final String formattedErrors = formatErrors(errors);

        LOG.info("Validation conflicts on " + formatRequest(req) + ". " + formattedErrors, ex);

        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), formattedErrors, req.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMessageNotReadableException(HttpServletRequest req, HttpMessageNotReadableException ex) {
        LOG.warn("Conversion failed on " + formatRequest(req), ex);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Request body is missing or invalid: " + ex.getLocalizedMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknownException(HttpServletRequest req, Exception ex) {
        LOG.warn("Exception occurred on " + formatRequest(req), ex);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), req.getRequestURI());
    }

    private String formatRequest(HttpServletRequest req) {
        return req.getMethod() + ' ' + req.getRequestURI();
    }

    private String formatErrors(Map<String, ? extends Collection<String>> errors) {
        return errors.entrySet()
                .stream()
                .map(e -> e.getKey() + " : " + e.getValue())
                .reduce((s1, s2) -> s1 + "; " + s2)
                .orElse("");
    }
}
