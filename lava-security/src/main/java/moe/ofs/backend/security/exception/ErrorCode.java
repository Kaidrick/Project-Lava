package moe.ofs.backend.security.exception;

public enum ErrorCode {
    BAD_LOGIN_CREDENTIALS(4001),
    ACCESS_TOKEN_EXPIRED(4002),
    REFRESH_TOKEN_EXPIRED(4003),
    INVALID_ACCESS_TOKEN(4004),
    INVALID_REFRESH_TOKEN(4005),
    INSUFFICIENT_ACCESS_RIGHT(4006);

    ErrorCode(int code) {
        this.code = code;
    }

    private final int code;

    public int getCode() {
        return code;
    }
}
