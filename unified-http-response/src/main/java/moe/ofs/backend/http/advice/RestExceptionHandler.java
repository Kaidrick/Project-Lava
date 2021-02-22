package moe.ofs.backend.http.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.authentication.BadCredentialsException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { Throwable.class })
    protected ResponseEntity<Exception> handleGlobalException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { BadCredentialsException.class })
    protected ResponseEntity<Exception> handleBadCredentialException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }
}
