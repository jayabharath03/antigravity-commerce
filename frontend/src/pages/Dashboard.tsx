import React, { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import type { RootState, AppDispatch } from '../app/store';
import { useNavigate } from 'react-router-dom';
import { getOrders, type Order } from '../api/orders';
import { Package, Clock, LogOut, User } from 'lucide-react';
import { logout } from '../features/auth/authSlice';
import { Button } from '../components/Button';

export const Dashboard: React.FC = () => {
    const { user } = useSelector((state: RootState) => state.auth);
    const navigate = useNavigate();
    const dispatch = useDispatch<AppDispatch>();
    
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) {
            navigate('/login');
            return;
        }
        
        loadOrders();
    }, [user, navigate]);

    const loadOrders = async () => {
        try {
            const data = await getOrders();
            setOrders(data.content);
        } catch (error) {
            console.error('Failed to load orders', error);
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        dispatch(logout());
        navigate('/catalog');
    };

    if (!user) return null;

    return (
        <div className="min-h-screen bg-gray-50 pt-16">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                
                <div className="flex justify-between items-end mb-8">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-900">My Dashboard</h1>
                        <p className="text-gray-500 mt-1">Welcome back, {user.firstName}!</p>
                    </div>
                    <Button variant="outline" onClick={handleLogout} className="flex items-center gap-2 text-gray-600 hover:text-red-600 border-gray-300">
                        <LogOut className="w-4 h-4" />
                        Sign Out
                    </Button>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Sidebar / Profile Info */}
                    <div className="space-y-6">
                        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
                            <div className="flex items-center gap-4 mb-6">
                                <div className="w-16 h-16 bg-indigo-100 rounded-full flex items-center justify-center text-indigo-600">
                                    <User className="w-8 h-8" />
                                </div>
                                <div>
                                    <h3 className="text-lg font-bold text-gray-900">{user.firstName} {user.lastName}</h3>
                                    <p className="text-gray-500 text-sm">{user.email}</p>
                                </div>
                            </div>
                            
                            <div className="border-t border-gray-100 pt-6">
                                <h4 className="text-sm font-semibold text-gray-900 uppercase tracking-wider mb-4">Account Overview</h4>
                                <div className="space-y-4">
                                    <div className="flex items-center justify-between text-sm">
                                        <span className="text-gray-500 flex items-center gap-2"><Package className="w-4 h-4" /> Total Orders</span>
                                        <span className="font-semibold text-gray-900">{orders.length}</span>
                                    </div>
                                    <div className="flex items-center justify-between text-sm">
                                        <span className="text-gray-500 flex items-center gap-2"><Clock className="w-4 h-4" /> Member Since</span>
                                        <span className="font-semibold text-gray-900">2024</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Main Content / Orders */}
                    <div className="lg:col-span-2">
                        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                            <div className="px-6 py-5 border-b border-gray-100">
                                <h3 className="text-lg font-bold text-gray-900">Order History</h3>
                            </div>
                            
                            {loading ? (
                                <div className="p-12 flex justify-center">
                                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
                                </div>
                            ) : orders.length === 0 ? (
                                <div className="p-12 text-center">
                                    <Package className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                                    <h4 className="text-lg font-medium text-gray-900 mb-2">No orders yet</h4>
                                    <p className="text-gray-500 mb-6">When you place an order, it will appear here.</p>
                                    <Button onClick={() => navigate('/catalog')}>Start Shopping</Button>
                                </div>
                            ) : (
                                <div className="divide-y divide-gray-100">
                                    {orders.map((order) => (
                                        <div key={order.id} className="p-6 hover:bg-gray-50 transition-colors">
                                            <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-4 gap-4">
                                                <div>
                                                    <p className="text-sm text-gray-500 mb-1">
                                                        Order placed {new Date(order.createdAt).toLocaleDateString()}
                                                    </p>
                                                    <p className="font-bold text-gray-900 text-lg">
                                                        {order.orderNumber}
                                                    </p>
                                                </div>
                                                <div className="flex flex-col items-end gap-2">
                                                    <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold ${
                                                        order.status === 'COMPLETED' || order.status === 'SHIPPED' ? 'bg-green-100 text-green-800' : 
                                                        'bg-yellow-100 text-yellow-800'
                                                    }`}>
                                                        {order.status}
                                                    </span>
                                                    <p className="font-bold text-indigo-600 text-xl">${order.grandTotal.toFixed(2)}</p>
                                                </div>
                                            </div>
                                            
                                            <div className="bg-gray-50 rounded-lg p-4 mt-4 border border-gray-100">
                                                <h5 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">Items</h5>
                                                <div className="space-y-3">
                                                    {order.items.map((item) => (
                                                        <div key={item.id} className="flex justify-between items-center text-sm">
                                                            <div className="flex items-center gap-3">
                                                                <span className="font-medium text-gray-900">{item.quantity}x</span>
                                                                <span className="text-gray-600">{item.productName}</span>
                                                            </div>
                                                            <span className="text-gray-900 font-medium">${((item.priceAtPurchase || 0) * (item.quantity || 1)).toFixed(2)}</span>
                                                        </div>
                                                    ))}
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};
