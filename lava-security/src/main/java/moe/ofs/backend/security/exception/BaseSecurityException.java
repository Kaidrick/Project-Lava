package moe.ofs.backend.security.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;

@Getter
@Setter
public class BaseSecurityException extends AuthenticationException {
    private ErrorCode errorCode;
    private Object metadata;

    public BaseSecurityException(String msg) {
        super(msg);
    }

    public BaseSecurityException(String msg, ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public BaseSecurityException(String msg, ErrorCode errorCode, Object metadata) {
        this(msg, errorCode);
        this.metadata = metadata;
    }

    public BaseSecurityException(String msg, Throwable throwable, ErrorCode errorCode, Object metadata) {
        super(msg, throwable);
        this.errorCode = errorCode;
        this.metadata = metadata;
    }
}
