package moe.ofs.backend.security.exception.token;

import moe.ofs.backend.security.exception.BaseSecurityException;
import moe.ofs.backend.security.exception.ErrorCode;

public class RefreshTokenExpiredException extends BaseSecurityException {
    public RefreshTokenExpiredException(String msg) {
        super(msg, ErrorCode.REFRESH_TOKEN_EXPIRED);
    }
}
