package moe.ofs.backend.security.exception.token;

import moe.ofs.backend.security.exception.BaseSecurityException;
import moe.ofs.backend.security.exception.ErrorCode;

public class InvalidRefreshTokenException extends BaseSecurityException {
    public InvalidRefreshTokenException(String msg) {
        super(msg, ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
