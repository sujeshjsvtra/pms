package com.sparksupport.pms.service;

import jakarta.servlet.http.HttpServletResponse;

public interface DocumentService {
    public void generateProductsPdf(HttpServletResponse response) throws Exception;

    public void generateProductQrCode(Long id, HttpServletResponse response) throws Exception;
}
