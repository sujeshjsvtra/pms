package com.sparksupport.pms.service;

public interface AuditLogService {
    void logEvent(String entityType, Long entityId, String action, String username, String details);
} 