import React, { useEffect, useState } from 'react';
import { getProducts } from '../api/catalog';
import type { Product } from '../api/catalog';
import { useNavigate } from 'react-router-dom';
import { Button } from '../components/Button';
import axios from 'axios';

export const AdminProducts: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        loadProducts();
    }, []);

    const loadProducts = async () => {
        try {
            const data = await getProducts();
            setProducts(data.content);
        } catch (error) {
            console.error('Failed to load products', error);
            if (axios.isAxiosError(error)) {
                alert(error.response?.data?.message || 'Failed to load products');
            } else {
                alert('An unexpected error occurred');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 text-gray-900 font-sans">
            {/* Admin Header */}
            <header className="bg-white border-b border-gray-200">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
                    <h1 className="text-2xl font-bold text-gray-900">Admin Dashboard</h1>
                    <div className="flex gap-4">
                        <Button variant="outline" onClick={() => navigate('/catalog')}>View Storefront</Button>
                    </div>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="flex justify-between items-center mb-8">
                    <h2 className="text-xl font-semibold">Products Catalog</h2>
                    <Button onClick={() => alert('Create product flow coming soon!')}>Add New Product</Button>
                </div>

                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-gray-50 text-gray-500 text-sm uppercase tracking-wider">
                                    <th className="p-4 font-semibold border-b border-gray-200">Product</th>
                                    <th className="p-4 font-semibold border-b border-gray-200">Brand</th>
                                    <th className="p-4 font-semibold border-b border-gray-200">Status</th>
                                    <th className="p-4 font-semibold border-b border-gray-200">Price</th>
                                    <th className="p-4 font-semibold border-b border-gray-200 text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-200">
                                {loading ? (
                                    <tr>
                                        <td colSpan={5} className="p-8 text-center text-gray-500">Loading...</td>
                                    </tr>
                                ) : products.length === 0 ? (
                                    <tr>
                                        <td colSpan={5} className="p-8 text-center text-gray-500">No products found.</td>
                                    </tr>
                                ) : (
                                    products.map((product) => (
                                        <tr key={product.id} className="hover:bg-gray-50 transition-colors">
                                            <td className="p-4">
                                                <div className="flex items-center gap-3">
                                                    <div className="w-10 h-10 rounded bg-gray-100 flex-shrink-0 overflow-hidden">
                                                        <img src={product.images?.[0]?.imageUrl || 'https://via.placeholder.com/40'} alt="" className="w-full h-full object-cover"/>
                                                    </div>
                                                    <div>
                                                        <p className="font-medium text-gray-900">{product.name}</p>
                                                        <p className="text-sm text-gray-500">{product.variants?.length || 0} variants</p>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="p-4 text-gray-600">{product.brand?.name || '-'}</td>
                                            <td className="p-4">
                                                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                                    product.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 
                                                    product.status === 'DRAFT' ? 'bg-yellow-100 text-yellow-800' : 'bg-gray-100 text-gray-800'
                                                }`}>
                                                    {product.status}
                                                </span>
                                            </td>
                                            <td className="p-4 text-gray-900 font-medium">
                                                ${product.variants?.[0]?.price || '0.00'}
                                            </td>
                                            <td className="p-4 text-right">
                                                <button className="text-indigo-600 hover:text-indigo-900 text-sm font-medium">Edit</button>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </main>
        </div>
    );
};
