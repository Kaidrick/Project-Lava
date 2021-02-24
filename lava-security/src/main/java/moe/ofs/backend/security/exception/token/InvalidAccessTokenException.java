package moe.ofs.backend.security.exception.token;

import moe.ofs.backend.security.exception.BaseSecurityException;
import moe.ofs.backend.security.exception.ErrorCode;

public class InvalidAccessTokenException extends BaseSecurityException {
    public InvalidAccessTokenException(String msg) {
        super(msg, ErrorCode.INVALID_ACCESS_TOKEN);
    }
}
