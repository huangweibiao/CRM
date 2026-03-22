package com.crm.exception;

/**
 * BusinessException - 业务异常
 *
 * @author CRM Team
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
