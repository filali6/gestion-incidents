package com.ville.intelligente.gestionincidents.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Long> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> requestTimes = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 100;  
    private static final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(1);  

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String clientIP = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        requestCounts.putIfAbsent(clientIP, 0L);
        requestTimes.putIfAbsent(clientIP, currentTime);

        long firstRequestTime = requestTimes.get(clientIP);
        long elapsedTime = currentTime - firstRequestTime;

        // Réinitialiser après 1 minute
        if (elapsedTime > TIME_WINDOW) {
            requestCounts.put(clientIP, 1L);
            requestTimes.put(clientIP, currentTime);
            return true;
        }

        long count = requestCounts.get(clientIP);

        // Bloquer si limite dépassée
        if (count >= MAX_REQUESTS) {
            response.setStatus(429); // Too Many Requests
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(
                    "<html><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                            "<h1 style='color: #e74c3c;'>⚠️ Trop de requêtes</h1>" +
                            "<p>Vous avez dépassé la limite de <strong>100 requêtes par minute</strong>.</p>" +
                            "<p>Veuillez patienter <strong>1 minute</strong>.</p>" +
                            "</body></html>");
            return false;
        }

        // Incrémenter le compteur
        requestCounts.put(clientIP, count + 1);
        return true;
    }
}