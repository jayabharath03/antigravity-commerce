-- Store the Razorpay identifiers so each order is traceable to a real payment.
ALTER TABLE orders ADD COLUMN razorpay_order_id VARCHAR(100);
ALTER TABLE orders ADD COLUMN razorpay_payment_id VARCHAR(100);
