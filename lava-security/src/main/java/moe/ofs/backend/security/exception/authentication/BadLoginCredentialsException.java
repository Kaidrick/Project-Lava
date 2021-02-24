package moe.ofs.backend.security.exception.authentication;

import moe.ofs.backend.security.exception.BaseSecurityException;
import moe.ofs.backend.security.exception.ErrorCode;

public class BadLoginCredentialsException extends BaseSecurityException {
    public BadLoginCredentialsException(String msg) {
        super(msg, ErrorCode.BAD_LOGIN_CREDENTIALS);
    }
}
