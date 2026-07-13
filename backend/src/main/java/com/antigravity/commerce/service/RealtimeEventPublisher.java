package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.OrderStatusEvent;
import com.antigravity.commerce.dto.StockUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/** Pushes realtime updates to subscribed browsers over WebSocket (STOMP). */
@Slf4j
@Service
@RequiredArgsConstructor
public class RealtimeEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /** Notify everyone viewing the catalog / product page that a variant's stock changed. */
    public void publishStockUpdate(UUID productId, UUID variantId, Integer stockQuantity) {
        log.info("Broadcasting stock update: variant {} -> {}", variantId, stockQuantity);
        messagingTemplate.convertAndSend("/topic/stock",
                new StockUpdateEvent(productId, variantId, stockQuantity));
    }

    /** Notify the customer watching a specific order that its status changed. */
    public void publishOrderStatus(String orderNumber, String status) {
        log.info("Broadcasting order status: {} -> {}", orderNumber, status);
        messagingTemplate.convertAndSend("/topic/orders/" + orderNumber,
                new OrderStatusEvent(orderNumber, status));
    }
}
