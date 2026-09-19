package com.rbdip.bookstore.order;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderEmailNotifier {
    public void sendConfirmationEmail(String customerName, Long orderId, BigDecimal total) {
        // Реальный почтовый транспорт не настроен в учебном проекте - здесь
        // просто эмулируется побочный эффект отправки письма.
        System.out.printf(
                "[email] Dear %s, your order #%d for %s has been placed.%n", customerName, orderId, total);
    }
}
