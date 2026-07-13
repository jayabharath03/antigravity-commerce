import React, { useEffect, useState } from 'react';
import { getAllOrders, updateOrderStatus } from '../api/orders';
import type { Order } from '../api/orders';
import { toast } from '../components/Toast';

const STATUS_OPTIONS = ['PAID', 'PACKED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

export const AdminOrders: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await getAllOrders(0, 50);
      setOrders(data.content);
    } catch (error) {
      console.error('Error fetching admin orders:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (orderNumber: string, newStatus: string) => {
    try {
      await updateOrderStatus(orderNumber, newStatus);
      // Optimistically update the UI
      setOrders(orders.map(order =>
        order.orderNumber === orderNumber ? { ...order, status: newStatus } : order
      ));
      toast(`Order ${orderNumber} → ${newStatus}`, 'success');
    } catch (error) {
      console.error('Error updating order status:', error);
      toast('Failed to update status', 'error');
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-gray-500">Loading orders dashboard...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Order Management</h1>
        <div className="text-sm text-gray-500 bg-gray-100 px-4 py-2 rounded-lg">
          Total Orders: {orders.length}
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <table className="w-full text-left">
          <thead className="bg-gray-50 text-gray-600 text-sm font-semibold border-b border-gray-100">
            <tr>
              <th className="py-4 px-6">Order #</th>
              <th className="py-4 px-6">Date</th>
              <th className="py-4 px-6">Total</th>
              <th className="py-4 px-6">Status</th>
              <th className="py-4 px-6 text-right">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {orders.length === 0 ? (
              <tr>
                <td colSpan={5} className="py-8 text-center text-gray-500">No orders found.</td>
              </tr>
            ) : (
              orders.map((order) => (
                <tr key={order.id} className="hover:bg-gray-50 transition-colors">
                  <td className="py-4 px-6 font-medium text-gray-900">{order.orderNumber}</td>
                  <td className="py-4 px-6 text-gray-600">{new Date(order.createdAt).toLocaleDateString()}</td>
                  <td className="py-4 px-6 font-semibold text-gray-900">${order.grandTotal.toFixed(2)}</td>
                  <td className="py-4 px-6">
                    <span className={`px-3 py-1 rounded-full text-xs font-medium uppercase tracking-wider ${
                      order.status === 'DELIVERED' ? 'bg-green-100 text-green-700' :
                      order.status === 'SHIPPED' ? 'bg-blue-100 text-blue-700' :
                      order.status === 'CANCELLED' ? 'bg-red-100 text-red-700' :
                      'bg-yellow-100 text-yellow-700'
                    }`}>
                      {order.status}
                    </span>
                  </td>
                  <td className="py-4 px-6 text-right">
                    <select
                      value={order.status}
                      onChange={(e) => handleStatusChange(order.orderNumber, e.target.value)}
                      className="text-sm border border-gray-300 rounded-lg px-3 py-2 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                    >
                      {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
                    </select>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
