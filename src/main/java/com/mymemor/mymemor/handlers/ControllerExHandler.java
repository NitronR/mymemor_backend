package com.mymemor.mymemor.handlers;

import com.mymemor.mymemor.Constants;
import com.mymemor.mymemor.response.FormResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerExHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public FormResponse invalidArgException(MethodArgumentNotValidException ex, HttpServletResponse response) {
        BindingResult bindResult = ex.getBindingResult();
        response.setStatus(HttpServletResponse.SC_OK);
        return processErrors((bindResult).getFieldErrors());
    }

    private FormResponse processErrors(List<FieldError> fieldErrors) {
        FormResponse formResponse = new FormResponse();
        for (FieldError fieldError : fieldErrors) {
            formResponse.addError(Constants.getJsonName(fieldError.getField()), fieldError.getDefaultMessage());
        }
        return formResponse;
    }

    // TODO handle not authenticated
}
