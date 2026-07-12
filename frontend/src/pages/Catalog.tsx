import React, { useEffect, useState } from 'react';
import { getProducts, getCategories, getBrands } from '../api/catalog';
import type { Product } from '../api/catalog';
import { useNavigate, Link } from 'react-router-dom';
import { Button } from '../components/Button';
import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from '../app/store';
import { addToCart, fetchCart } from '../features/cart/cartSlice';
import { ShoppingCart, Check, Search, Filter } from 'lucide-react';

export const Catalog: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [categories, setCategories] = useState<any[]>([]);
    const [brands, setBrands] = useState<any[]>([]);
    
    // Filters State
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
    const { items: cartItems } = useSelector((state: RootState) => state.cart);

    useEffect(() => {
        loadFilters();
        if (user) {
            dispatch(fetchCart());
        }
    }, [dispatch, user]);

    useEffect(() => {
        const delayDebounceFn = setTimeout(() => {
            loadProducts();
        }, 300); // debounce search
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

    return (
        <div className="min-h-screen bg-gray-950 text-white font-sans selection:bg-indigo-500 selection:text-white">
            {/* Navbar */}
            <nav className="sticky top-0 z-50 backdrop-blur-lg bg-gray-950/80 border-b border-gray-800">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <div className="flex items-center gap-2">
                            <div className="w-8 h-8 rounded bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center font-bold text-lg shadow-lg shadow-indigo-500/20">A</div>
                            <span className="text-xl font-bold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-white to-gray-400">Antigravity Commerce</span>
                        </div>
                        <div className="flex gap-4 items-center">
                            {user ? (
                                <>
                                    <Link to="/cart" className="relative p-2 text-gray-300 hover:text-white transition-colors">
                                        <ShoppingCart className="w-6 h-6" />
                                        {cartItems.length > 0 && (
                                            <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-indigo-500 rounded-full">
                                                {cartItems.length}
                                            </span>
                                        )}
                                    </Link>
                                    <Button onClick={() => navigate('/dashboard')} variant="outline" className="border-gray-700 hover:border-gray-600 text-gray-300 hover:text-white">
                                        My Dashboard
                                    </Button>
                                    {user?.roles?.includes('ADMIN') && (
                                        <Button onClick={() => navigate('/admin/products')} variant="outline" className="border-gray-700 hover:border-gray-600">Admin Panel</Button>
                                    )}
                                </>
                            ) : (
                                <Button variant="outline" onClick={() => navigate('/login')}>Sign In</Button>
                            )}
                        </div>
                    </div>
                </div>
            </nav>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 flex flex-col md:flex-row gap-8">
                {/* Sidebar Filters */}
                <aside className="w-full md:w-64 space-y-8 flex-shrink-0">
                    <div>
                        <h3 className="text-lg font-bold mb-4 flex items-center gap-2"><Search className="w-5 h-5"/> Search</h3>
                        <input
                            type="text"
                            placeholder="Search products..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="w-full bg-gray-900 border border-gray-800 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                        />
                    </div>
                    
                    <div>
                        <h3 className="text-lg font-bold mb-4 flex items-center gap-2"><Filter className="w-5 h-5"/> Filters</h3>
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-400 mb-1">Category</label>
                                <select 
                                    value={categorySlug} 
                                    onChange={(e) => setCategorySlug(e.target.value)}
                                    className="w-full bg-gray-900 border border-gray-800 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                >
                                    <option value="">All Categories</option>
                                    {categories.map(c => (
                                        <option key={c.id} value={c.slug}>{c.name}</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-400 mb-1">Brand</label>
                                <select 
                                    value={brandSlug} 
                                    onChange={(e) => setBrandSlug(e.target.value)}
                                    className="w-full bg-gray-900 border border-gray-800 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                >
                                    <option value="">All Brands</option>
                                    {brands.map(b => (
                                        <option key={b.id} value={b.slug}>{b.name}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="grid grid-cols-2 gap-2">
                                <div>
                                    <label className="block text-sm font-medium text-gray-400 mb-1">Min $</label>
                                    <input
                                        type="number"
                                        value={minPrice}
                                        onChange={(e) => setMinPrice(e.target.value === '' ? '' : Number(e.target.value))}
                                        className="w-full bg-gray-900 border border-gray-800 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-400 mb-1">Max $</label>
                                    <input
                                        type="number"
                                        value={maxPrice}
                                        onChange={(e) => setMaxPrice(e.target.value === '' ? '' : Number(e.target.value))}
                                        className="w-full bg-gray-900 border border-gray-800 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </aside>

                {/* Product Grid */}
                <main className="flex-1">
                    {loading ? (
                        <div className="flex justify-center py-20">
                            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-500"></div>
                        </div>
                    ) : products.length === 0 ? (
                        <div className="text-center py-20 bg-gray-900/50 rounded-2xl border border-gray-800 backdrop-blur-xl">
                            <p className="text-gray-400 text-lg">No products match your filters.</p>
                            <Button className="mt-4 border-gray-700" variant="outline" onClick={() => {
                                setSearch(''); setCategorySlug(''); setBrandSlug(''); setMinPrice(''); setMaxPrice('');
                            }}>Clear Filters</Button>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                            {products.map((product) => (
                                <div key={product.id} className="group relative bg-gray-900 rounded-2xl p-4 transition-all hover:bg-gray-800 border border-gray-800 hover:border-gray-700 shadow-xl shadow-black/50 overflow-hidden flex flex-col">
                                    <div className="aspect-h-1 aspect-w-1 w-full overflow-hidden rounded-xl bg-gray-800 lg:aspect-none group-hover:opacity-75 h-48 mb-4">
                                        <img 
                                            src={product.images?.[0]?.imageUrl || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=1000&auto=format&fit=crop'} 
                                            alt={product.name}
                                            className="h-full w-full object-cover object-center lg:h-full lg:w-full transition-transform duration-500 group-hover:scale-105"
                                        />
                                    </div>
                                    <div className="flex-1">
                                        <div className="flex justify-between items-start mb-2">
                                            <div>
                                                <h3 className="text-xs font-medium text-gray-400 uppercase tracking-wider">
                                                    {product.brand?.name || 'Generic'}
                                                </h3>
                                                <p className="mt-1 text-lg font-bold text-white leading-tight">
                                                    {product.name}
                                                </p>
                                            </div>
                                            <p className="text-lg font-bold text-indigo-400">
                                                ${product.variants?.[0]?.price || '0.00'}
                                            </p>
                                        </div>
                                    </div>
                                    <div className="mt-4 flex gap-2">
                                        <Button 
                                            className="w-1/2 bg-gray-800 text-white hover:bg-gray-700 border-gray-700"
                                            onClick={() => navigate(`/products/${product.slug}`)}
                                        >
                                            Details
                                        </Button>
                                        <Button 
                                            className="w-1/2 bg-indigo-600 text-white hover:bg-indigo-500"
                                            onClick={() => {
                                                if (!user) {
                                                    navigate('/login');
                                                    return;
                                                }
                                                setAddingIds(prev => [...prev, product.id]);
                                                dispatch(addToCart({ productId: product.id, quantity: 1 }))
                                                    .finally(() => {
                                                        setTimeout(() => {
                                                            setAddingIds(prev => prev.filter(id => id !== product.id));
                                                        }, 1000);
                                                    });
                                            }}
                                            disabled={addingIds.includes(product.id)}
                                        >
                                            {addingIds.includes(product.id) ? <Check className="mx-auto" /> : 'Add to Cart'}
                                        </Button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
};
