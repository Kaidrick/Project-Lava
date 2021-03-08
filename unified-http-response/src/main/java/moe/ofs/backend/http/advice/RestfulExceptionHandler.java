package moe.ofs.backend.http.advice;

import moe.ofs.backend.security.exception.authentication.BadLoginCredentialsException;
import moe.ofs.backend.security.exception.BaseSecurityException;
import moe.ofs.backend.security.exception.authorization.InsufficientAccessRightException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestfulExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Throwable.class})
    protected ResponseEntity<Exception> handleGlobalException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {BaseSecurityException.class})
    protected ResponseEntity<Exception> handleSecurityException(  // Although the HTTP standard specifies "unauthorized", semantically this response means "unauthenticated".
            Exception ex, WebRequest request) {
        ex.printStackTrace();

        if (ex instanceof BadLoginCredentialsException) {
            return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        } else if (ex instanceof InsufficientAccessRightException) {
            return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(ex, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }
    }
}
