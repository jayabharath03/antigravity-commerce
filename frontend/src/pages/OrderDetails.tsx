import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getOrderByOrderNumber } from '../api/orders';
import type { Order } from '../api/orders';
import { subscribe } from '../utils/realtime';

const STATUS_STEPS = ['PAID', 'PACKED', 'SHIPPED', 'DELIVERED'];

export const OrderDetails: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    if (orderId) {
      fetchOrderDetails(orderId);
    }
  }, [orderId]);

  // Live order tracking: update the status instantly when an admin changes it.
  useEffect(() => {
    if (!orderId) return;
    const unsubscribe = subscribe(
      `/topic/orders/${orderId}`,
      (evt: { orderNumber: string; status: string }) => {
        setOrder((prev) => (prev ? { ...prev, status: evt.status } : prev));
      }
    );
    return unsubscribe;
  }, [orderId]);

  const fetchOrderDetails = async (id: string) => {
    try {
      setLoading(true);
      const data = await getOrderByOrderNumber(id);
      setOrder(data);
    } catch (error) {
      console.error('Error fetching order details:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-gray-500">Loading order details...</div>;
  }

  if (!order) {
    return <div className="p-8 text-center text-red-500">Order not found.</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <button onClick={() => navigate('/my-orders')} className="text-sm text-indigo-600 mb-6 hover:underline">
        ← Back to My Orders
      </button>
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 border-b border-gray-100 pb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 mb-1">Order #{order.orderNumber}</h1>
            <p className="text-gray-500 text-sm">Placed on {new Date(order.createdAt).toLocaleString()}</p>
          </div>
          <span className="mt-4 md:mt-0 px-4 py-2 rounded-full text-sm font-bold uppercase tracking-wider bg-indigo-50 text-indigo-700 flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-green-500 animate-pulse" title="Live" />
            {order.status}
          </span>
        </div>

        {/* Live order tracker — advances in realtime as the admin updates the order */}
        {STATUS_STEPS.includes(order.status) && (
          <div className="mb-8">
            <div className="flex items-center">
              {STATUS_STEPS.map((step, i) => {
                const currentIndex = STATUS_STEPS.indexOf(order.status);
                const done = i <= currentIndex;
                return (
                  <React.Fragment key={step}>
                    <div className="flex flex-col items-center">
                      <div className={`w-9 h-9 rounded-full flex items-center justify-center text-sm font-bold transition-colors ${done ? 'bg-indigo-600 text-white' : 'bg-gray-200 text-gray-400'}`}>
                        {i + 1}
                      </div>
                      <span className={`mt-2 text-xs font-medium ${done ? 'text-indigo-700' : 'text-gray-400'}`}>{step}</span>
                    </div>
                    {i < STATUS_STEPS.length - 1 && (
                      <div className={`flex-1 h-1 mx-2 rounded transition-colors ${i < currentIndex ? 'bg-indigo-600' : 'bg-gray-200'}`} />
                    )}
                  </React.Fragment>
                );
              })}
            </div>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-3">Shipping Address</h3>
            <p className="text-gray-600 whitespace-pre-line">{order.shippingAddress}</p>
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-3">Payment Information</h3>
            <p className="text-gray-600">Status: {order.paymentStatus}</p>
          </div>
        </div>

        <h3 className="text-lg font-semibold text-gray-900 mb-4">Items Ordered</h3>
        <div className="space-y-4 mb-8">
          {order.items.map((item) => (
            <div key={item.id} className="flex justify-between items-center py-3 border-b border-gray-50 last:border-0">
              <div>
                <p className="font-medium text-gray-900">{item.productName}</p>
                <p className="text-sm text-gray-500">Qty: {item.quantity}</p>
              </div>
              <p className="font-medium text-gray-900">${(item.priceAtPurchase * item.quantity).toFixed(2)}</p>
            </div>
          ))}
        </div>

        <div className="border-t border-gray-100 pt-6 space-y-3">
          <div className="flex justify-between text-gray-600">
            <span>Subtotal</span>
            <span>${order.subTotal.toFixed(2)}</span>
          </div>
          <div className="flex justify-between text-gray-600">
            <span>Tax</span>
            <span>${order.taxTotal.toFixed(2)}</span>
          </div>
          <div className="flex justify-between text-gray-600">
            <span>Shipping</span>
            <span>${order.shippingTotal.toFixed(2)}</span>
          </div>
          <div className="flex justify-between items-center pt-4 mt-4 border-t border-gray-100">
            <span className="text-xl font-bold text-gray-900">Total</span>
            <span className="text-2xl font-bold text-indigo-600">${order.grandTotal.toFixed(2)}</span>
          </div>
        </div>
      </div>
    </div>
  );
};
