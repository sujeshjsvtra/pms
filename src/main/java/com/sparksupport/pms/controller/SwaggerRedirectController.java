package com.sparksupport.pms.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class SwaggerRedirectController {

    @GetMapping({"/swagger-ui", "/swagger-ui.html"})
    public void redirectToSwaggerByRole(HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("Logged-in user roles:");
            auth.getAuthorities().forEach(grantedAuthority -> System.out.println(grantedAuthority.getAuthority()));
        }
        if (auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            response.sendRedirect("/swagger-ui-admin");
        } else if (auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            response.sendRedirect("/swagger-ui-user");
        } else {
            response.sendRedirect("/swagger-ui-user.html");
        }
    }

    @GetMapping("/swagger-ui-user")
    public void userSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui-user.html?configUrl=/v3/api-docs/user-api");
    }

    @GetMapping("/swagger-ui-admin")
    public void adminSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui-admin.html?configUrl=/v3/api-docs/admin-api");
    }

    @GetMapping("/swagger-ui-user.html")
    @ResponseBody
    public ResponseEntity<String> serveUserSwaggerHtml() throws IOException {
        Resource resource = new ClassPathResource("static/swagger-ui-user.html");
        if (resource.exists()) {
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(content);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/swagger-ui-admin.html")
    @ResponseBody
    public ResponseEntity<String> serveAdminSwaggerHtml() throws IOException {
        Resource resource = new ClassPathResource("static/swagger-ui-admin.html");
        if (resource.exists()) {
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(content);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

     
}
