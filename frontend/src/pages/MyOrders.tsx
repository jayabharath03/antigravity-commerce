import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getOrders } from '../api/orders';
import type { Order } from '../api/orders';

export const MyOrders: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await getOrders(0, 50);
      setOrders(data.content);
    } catch (error) {
      console.error('Error fetching orders:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-gray-500">Loading orders...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <h1 className="text-3xl font-bold mb-8 text-gray-900">My Orders</h1>
      {orders.length === 0 ? (
        <div className="bg-gray-50 rounded-2xl p-8 text-center text-gray-500">
          You haven't placed any orders yet.
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div key={order.id} className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
              <div>
                <p className="text-sm text-gray-500 mb-1">Order #{order.orderNumber}</p>
                <p className="text-lg font-semibold text-gray-900">${order.grandTotal.toFixed(2)}</p>
                <p className="text-sm text-gray-500 mt-1">Placed on {new Date(order.createdAt).toLocaleDateString()}</p>
              </div>
              <div className="flex flex-col items-end gap-2">
                <span className={`px-3 py-1 rounded-full text-xs font-medium uppercase tracking-wider ${
                  order.status === 'DELIVERED' ? 'bg-green-100 text-green-700' :
                  order.status === 'SHIPPED' ? 'bg-blue-100 text-blue-700' :
                  'bg-yellow-100 text-yellow-700'
                }`}>
                  {order.status}
                </span>
                <button
                  onClick={() => navigate(`/orders/${order.orderNumber}`)}
                  className="text-indigo-600 hover:text-indigo-700 text-sm font-medium"
                >
                  View Details →
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
