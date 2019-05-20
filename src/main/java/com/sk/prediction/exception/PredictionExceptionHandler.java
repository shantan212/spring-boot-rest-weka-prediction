package com.sk.prediction.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sk.prediction.constants.ErrorConstants;
import com.sk.prediction.model.RestErrorInfo;
import com.sk.prediction.util.Utils;


@ControllerAdvice(basePackages = "com.sk.prediction")
public class PredictionExceptionHandler {


    @ExceptionHandler(PredictionException.class)
    public ResponseEntity<RestErrorInfo> notFoundException(final PredictionException e,
            HttpServletRequest request) {
    	
        return error(e, HttpStatus.NOT_FOUND, "", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RestErrorInfo> notFoundException(HttpServletRequest request, HttpServletResponse response) {
    	PredictionException ex = new PredictionException(ErrorConstants.INTERNAL_EXCEPTION);
        return error(ex, HttpStatus.BAD_REQUEST, "", request);
    }

    private ResponseEntity<RestErrorInfo> error(final PredictionException exception, final HttpStatus httpStatus,
            final String logRef, HttpServletRequest request) {
        try {
            String path = Utils.getServerContextPath(request);
            String value = Utils.getBundleValue(path, request.getHeader("Accept-Language"))
                    .getString(exception.getErrorCode());

            return new ResponseEntity<RestErrorInfo>(new RestErrorInfo(exception.getErrorCode(), value), httpStatus);
        } catch (Exception e) {
            return new ResponseEntity<RestErrorInfo>(new RestErrorInfo(exception.getErrorCode(), "en-US"), httpStatus);
        }

    }


}
