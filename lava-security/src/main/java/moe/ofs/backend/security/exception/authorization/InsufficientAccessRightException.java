package moe.ofs.backend.security.exception.authorization;

import moe.ofs.backend.security.exception.BaseSecurityException;
import moe.ofs.backend.security.exception.ErrorCode;

public class InsufficientAccessRightException extends BaseSecurityException {
    public InsufficientAccessRightException(String msg) {
        super(msg, ErrorCode.INSUFFICIENT_ACCESS_RIGHT);
    }
}
