import api from '../utils/axios';

export interface OrderItem {
  id: string;
  productId: string;
  productName: string;
  quantity: number;
  priceAtPurchase: number;
}

export interface Order {
  id: string;
  orderNumber: string;
  status: string;
  subTotal: number;
  taxTotal: number;
  shippingTotal: number;
  grandTotal: number;
  shippingAddress: string;
  paymentStatus: string;
  items: OrderItem[];
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export const getOrders = async (page = 0, size = 20): Promise<Page<Order>> => {
  const response = await api.get(`/orders?page=${page}&size=${size}`);
  return response.data;
};

export const getOrderByOrderNumber = async (orderNumber: string): Promise<Order> => {
  const response = await api.get(`/orders/${orderNumber}`);
  return response.data;
};

export const cancelOrder = async (orderNumber: string): Promise<Order> => {
  const response = await api.post(`/orders/${orderNumber}/cancel`);
  return response.data;
};

// Admin Endpoints
export const getAllOrders = async (page = 0, size = 20): Promise<Page<Order>> => {
  const response = await api.get(`/admin/orders?page=${page}&size=${size}`);
  return response.data;
};

export const updateOrderStatus = async (orderNumber: string, status: string): Promise<Order> => {
  const response = await api.patch(`/admin/orders/${orderNumber}/status`, { status });
  return response.data;
};
