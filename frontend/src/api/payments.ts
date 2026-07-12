import api from '../utils/axios';

export interface PaymentOrder {
  razorpayOrderId: string;
  razorpayKeyId: string;
  amount: number;
  currency: string;
  grandTotal: number;
}

export interface VerifyPaymentPayload {
  razorpayOrderId: string;
  razorpayPaymentId: string;
  razorpaySignature: string;
  shippingAddress: string;
}

/** Step 1: ask the backend to create a Razorpay order for the current cart. */
export const createPaymentOrder = async (): Promise<PaymentOrder> => {
  const res = await api.post('/payments/order');
  return res.data.data;
};

/** Step 2: send the popup result to the backend for signature verification + order placement. */
export const verifyPayment = async (payload: VerifyPaymentPayload) => {
  const res = await api.post('/payments/verify', payload);
  return res.data.data; // the placed OrderDto
};

/** Loads the Razorpay checkout script once, on demand. */
export const loadRazorpayScript = (): Promise<boolean> =>
  new Promise((resolve) => {
    if ((window as any).Razorpay) {
      resolve(true);
      return;
    }
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
