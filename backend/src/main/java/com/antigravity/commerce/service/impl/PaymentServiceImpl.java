package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.dto.PaymentOrderResponse;
import com.antigravity.commerce.dto.PaymentVerificationRequest;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.service.CheckoutService;
import com.antigravity.commerce.service.PaymentService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final CheckoutService checkoutService;
    private final String keyId;
    private final String keySecret;
    private final String currency;

    public PaymentServiceImpl(CheckoutService checkoutService,
                              @Value("${razorpay.key-id:}") String keyId,
                              @Value("${razorpay.key-secret:}") String keySecret,
                              @Value("${razorpay.currency:INR}") String currency) {
        this.checkoutService = checkoutService;
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.currency = currency;
    }

    private void ensureConfigured() {
        if (!StringUtils.hasText(keyId) || !StringUtils.hasText(keySecret)) {
            throw new BadRequestException("Payment gateway is not configured. Set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentOrderResponse createOrder() {
        ensureConfigured();

        BigDecimal grandTotal = checkoutService.calculateGrandTotal();
        // Razorpay expects the amount in the smallest currency unit (paise for INR).
        long amountMinorUnit = grandTotal.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP).longValueExact();

        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountMinorUnit);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "rcpt_" + UUID.randomUUID().toString().substring(0, 12));

            com.razorpay.Order razorpayOrder = client.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");

            log.info("Created Razorpay order {} for amount {} {}", razorpayOrderId, amountMinorUnit, currency);

            return PaymentOrderResponse.builder()
                    .razorpayOrderId(razorpayOrderId)
                    .razorpayKeyId(keyId)
                    .amount(amountMinorUnit)
                    .currency(currency)
                    .grandTotal(grandTotal)
                    .build();
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order", e);
            throw new BadRequestException("Could not initiate payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderDto verifyAndPlaceOrder(PaymentVerificationRequest request) {
        ensureConfigured();

        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", request.getRazorpayOrderId());
        attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
        attributes.put("razorpay_signature", request.getRazorpaySignature());

        boolean valid;
        try {
            valid = Utils.verifyPaymentSignature(attributes, keySecret);
        } catch (RazorpayException e) {
            log.error("Razorpay signature verification error", e);
            throw new BadRequestException("Payment verification failed");
        }

        if (!valid) {
            throw new BadRequestException("Payment verification failed: invalid signature");
        }

        return checkoutService.placeOrder(
                request.getShippingAddress(),
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId());
    }
}
