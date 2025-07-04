package com.sparksupport.pms.service;

import com.sparksupport.pms.model.AuditLog;
import com.sparksupport.pms.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
 
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void logEvent(String entityType, Long entityId, String action, String username, String details) {
        AuditLog log = new AuditLog(entityType, entityId, action, username, LocalDateTime.now(), details);
        auditLogRepository.save(log);
    }
} 