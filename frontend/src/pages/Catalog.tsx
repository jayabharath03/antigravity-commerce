import React, { useEffect, useState } from 'react';
import { getProducts, getCategories, getBrands } from '../api/catalog';
import type { Product } from '../api/catalog';
import { useNavigate } from 'react-router-dom';
import { Button } from '../components/Button';
import { toast } from '../components/Toast';
import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from '../app/store';
import { addToCart, fetchCart } from '../features/cart/cartSlice';
import { ShoppingCart, Check, Search, Filter } from 'lucide-react';

const stockOf = (p: Product) => p.variants?.[0]?.stockQuantity ?? 0;

export const Catalog: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [categories, setCategories] = useState<any[]>([]);
    const [brands, setBrands] = useState<any[]>([]);

    const [search, setSearch] = useState('');
    const [categorySlug, setCategorySlug] = useState('');
    const [brandSlug, setBrandSlug] = useState('');
    const [minPrice, setMinPrice] = useState<number | ''>('');
    const [maxPrice, setMaxPrice] = useState<number | ''>('');

    const [loading, setLoading] = useState(true);
    const [addingIds, setAddingIds] = useState<string[]>([]);

    const navigate = useNavigate();
    const dispatch = useDispatch<AppDispatch>();

    const { user } = useSelector((state: RootState) => state.auth);

    useEffect(() => {
        loadFilters();
        if (user) {
            dispatch(fetchCart());
        }
    }, [dispatch, user]);

    useEffect(() => {
        const delayDebounceFn = setTimeout(() => {
            loadProducts();
        }, 300);
        return () => clearTimeout(delayDebounceFn);
    }, [search, categorySlug, brandSlug, minPrice, maxPrice]);

    const loadFilters = async () => {
        try {
            const [catRes, brandRes] = await Promise.all([getCategories(), getBrands()]);
            setCategories(catRes);
            setBrands(brandRes);
        } catch (error) {
            console.error('Failed to load filter options', error);
        }
    };

    const loadProducts = async () => {
        setLoading(true);
        try {
            const filters = {
                search: search || undefined,
                categorySlug: categorySlug || undefined,
                brandSlug: brandSlug || undefined,
                minPrice: minPrice !== '' ? minPrice : undefined,
                maxPrice: maxPrice !== '' ? maxPrice : undefined,
            };
            const data = await getProducts(filters);
            setProducts(data.content);
        } catch (error) {
            console.error('Failed to load products', error);
        } finally {
            setLoading(false);
        }
    };

    const handleAddToCart = (product: Product) => {
        if (!user) {
            navigate('/login');
            return;
        }
        if (stockOf(product) <= 0) {
            toast('This item is out of stock', 'error');
            return;
        }
        setAddingIds((prev) => [...prev, product.id]);
        dispatch(addToCart({ productId: product.id, quantity: 1 }))
            .unwrap()
            .then(() => toast(`${product.name} added to cart`, 'success'))
            .catch(() => toast('Could not add to cart', 'error'))
            .finally(() => {
                setTimeout(() => setAddingIds((prev) => prev.filter((id) => id !== product.id)), 800);
            });
    };

    const clearFilters = () => {
        setSearch(''); setCategorySlug(''); setBrandSlug(''); setMinPrice(''); setMaxPrice('');
    };

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900">Shop all products</h1>
                <p className="text-gray-500 mt-1">Tech, audio, wearables and more — with live stock.</p>
            </div>

            <div className="flex flex-col md:flex-row gap-8">
                {/* Sidebar Filters */}
                <aside className="w-full md:w-64 flex-shrink-0 space-y-6">
                    <div>
                        <label className="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2"><Search className="w-4 h-4" /> Search</label>
                        <input
                            type="text"
                            placeholder="Search products..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="w-full bg-white border border-gray-300 rounded-lg px-4 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>

                    <div className="bg-white border border-gray-200 rounded-xl p-4 space-y-4">
                        <h3 className="text-sm font-semibold text-gray-700 flex items-center gap-2"><Filter className="w-4 h-4" /> Filters</h3>
                        <div>
                            <label className="block text-xs font-medium text-gray-500 mb-1">Category</label>
                            <select value={categorySlug} onChange={(e) => setCategorySlug(e.target.value)}
                                className="w-full bg-white border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                                <option value="">All Categories</option>
                                {categories.map((c) => <option key={c.id} value={c.slug}>{c.name}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className="block text-xs font-medium text-gray-500 mb-1">Brand</label>
                            <select value={brandSlug} onChange={(e) => setBrandSlug(e.target.value)}
                                className="w-full bg-white border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                                <option value="">All Brands</option>
                                {brands.map((b) => <option key={b.id} value={b.slug}>{b.name}</option>)}
                            </select>
                        </div>
                        <div className="grid grid-cols-2 gap-2">
                            <div>
                                <label className="block text-xs font-medium text-gray-500 mb-1">Min $</label>
                                <input type="number" value={minPrice} onChange={(e) => setMinPrice(e.target.value === '' ? '' : Number(e.target.value))}
                                    className="w-full bg-white border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                            </div>
                            <div>
                                <label className="block text-xs font-medium text-gray-500 mb-1">Max $</label>
                                <input type="number" value={maxPrice} onChange={(e) => setMaxPrice(e.target.value === '' ? '' : Number(e.target.value))}
                                    className="w-full bg-white border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                            </div>
                        </div>
                        <button onClick={clearFilters} className="text-sm text-indigo-600 hover:text-indigo-800 font-medium">Clear filters</button>
                    </div>
                </aside>

                {/* Product Grid */}
                <main className="flex-1">
                    {loading ? (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                            {Array.from({ length: 6 }).map((_, i) => (
                                <div key={i} className="bg-white rounded-2xl border border-gray-200 p-4 animate-pulse">
                                    <div className="h-48 bg-gray-200 rounded-xl mb-4" />
                                    <div className="h-4 bg-gray-200 rounded w-1/3 mb-2" />
                                    <div className="h-5 bg-gray-200 rounded w-2/3" />
                                </div>
                            ))}
                        </div>
                    ) : products.length === 0 ? (
                        <div className="text-center py-20 bg-white rounded-2xl border border-gray-200">
                            <p className="text-gray-500 text-lg">No products match your filters.</p>
                            <Button className="mt-4" variant="outline" onClick={clearFilters}>Clear Filters</Button>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                            {products.map((product) => {
                                const stock = stockOf(product);
                                const out = stock <= 0;
                                const low = stock > 0 && stock <= 5;
                                return (
                                    <div key={product.id} className="group bg-white rounded-2xl border border-gray-200 overflow-hidden flex flex-col hover:shadow-lg transition-shadow">
                                        <button onClick={() => navigate(`/products/${product.slug}`)} className="relative block h-52 bg-gray-100 overflow-hidden">
                                            <img
                                                src={product.images?.[0]?.imageUrl || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=800&auto=format&fit=crop'}
                                                alt={product.name}
                                                className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
                                            />
                                            {out && <span className="absolute top-3 left-3 bg-red-600 text-white text-xs font-semibold px-2 py-1 rounded">Out of stock</span>}
                                            {low && <span className="absolute top-3 left-3 bg-amber-500 text-white text-xs font-semibold px-2 py-1 rounded">Only {stock} left</span>}
                                        </button>
                                        <div className="p-4 flex flex-col flex-1">
                                            <p className="text-xs font-medium text-gray-400 uppercase tracking-wider">{product.brand?.name || 'Generic'}</p>
                                            <button onClick={() => navigate(`/products/${product.slug}`)} className="text-left mt-1 text-base font-semibold text-gray-900 leading-tight hover:text-indigo-600 line-clamp-2">
                                                {product.name}
                                            </button>
                                            <p className="mt-2 text-lg font-bold text-gray-900">${product.variants?.[0]?.price ?? '0.00'}</p>
                                            <div className="mt-4 flex gap-2">
                                                <Button variant="outline" className="flex-1" onClick={() => navigate(`/products/${product.slug}`)}>Details</Button>
                                                <Button
                                                    className="flex-1"
                                                    onClick={() => handleAddToCart(product)}
                                                    disabled={out || addingIds.includes(product.id)}
                                                >
                                                    {addingIds.includes(product.id) ? <Check className="w-5 h-5" /> : out ? 'Sold out' : (<><ShoppingCart className="w-4 h-4 mr-1" /> Add</>)}
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
};
