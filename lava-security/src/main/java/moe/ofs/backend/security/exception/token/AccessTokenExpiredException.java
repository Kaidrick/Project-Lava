package moe.ofs.backend.security.exception.token;

import moe.ofs.backend.security.exception.BaseSecurityException;
import moe.ofs.backend.security.exception.ErrorCode;

public class AccessTokenExpiredException extends BaseSecurityException {
    public AccessTokenExpiredException(String msg) {
        super(msg, ErrorCode.ACCESS_TOKEN_EXPIRED);
    }
}
