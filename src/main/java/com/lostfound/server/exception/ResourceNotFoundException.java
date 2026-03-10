package com.lostfound.server.exception;

/**
 * 资源未找到异常
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
    
    public ResourceNotFoundException(String resource, Object id) {
        super(404, String.format("%s 不存在，ID: %s", resource, id));
    }
}