import React, { useEffect, useState } from 'react';
import {
    getProducts, getCategories, getBrands,
    createProduct, updateProduct, deleteProduct,
    type Product, type ProductInput,
} from '../api/catalog';
import { Button } from '../components/Button';
import { toast } from '../components/Toast';
import { Plus, Pencil, Trash2, X } from 'lucide-react';

interface FormState {
    id?: string;
    name: string;
    shortDescription: string;
    categoryId: string;
    brandId: string;
    price: string;
    stockQuantity: string;
    imageUrl: string;
    status: string;
}

const emptyForm: FormState = {
    name: '', shortDescription: '', categoryId: '', brandId: '',
    price: '', stockQuantity: '', imageUrl: '', status: 'ACTIVE',
};

export const AdminProducts: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [categories, setCategories] = useState<any[]>([]);
    const [brands, setBrands] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    const [modalOpen, setModalOpen] = useState(false);
    const [saving, setSaving] = useState(false);
    const [form, setForm] = useState<FormState>(emptyForm);

    useEffect(() => { loadAll(); }, []);

    const loadAll = async () => {
        setLoading(true);
        try {
            const [prodRes, catRes, brandRes] = await Promise.all([getProducts(), getCategories(), getBrands()]);
            setProducts(prodRes.content);
            setCategories(catRes);
            setBrands(brandRes);
        } catch (e) {
            toast('Failed to load products', 'error');
        } finally {
            setLoading(false);
        }
    };

    const openCreate = () => {
        setForm({ ...emptyForm, categoryId: categories[0]?.id || '' });
        setModalOpen(true);
    };

    const openEdit = (p: Product) => {
        setForm({
            id: p.id,
            name: p.name,
            shortDescription: p.shortDescription || '',
            categoryId: p.category?.id || '',
            brandId: p.brand?.id || '',
            price: p.variants?.[0]?.price != null ? String(p.variants[0].price) : '',
            stockQuantity: p.variants?.[0]?.stockQuantity != null ? String(p.variants[0].stockQuantity) : '',
            imageUrl: p.images?.[0]?.imageUrl || '',
            status: p.status || 'ACTIVE',
        });
        setModalOpen(true);
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!form.name.trim() || !form.categoryId) {
            toast('Name and category are required', 'error');
            return;
        }
        setSaving(true);
        const input: ProductInput = {
            name: form.name.trim(),
            shortDescription: form.shortDescription || undefined,
            categoryId: form.categoryId,
            brandId: form.brandId || undefined,
            status: form.status,
            price: form.price !== '' ? Number(form.price) : undefined,
            stockQuantity: form.stockQuantity !== '' ? Number(form.stockQuantity) : undefined,
            imageUrl: form.imageUrl || undefined,
        };
        try {
            if (form.id) {
                await updateProduct(form.id, input);
                toast('Product updated', 'success');
            } else {
                await createProduct(input);
                toast('Product created', 'success');
            }
            setModalOpen(false);
            await loadAll();
        } catch (err: any) {
            toast(err.response?.data?.message || 'Failed to save product', 'error');
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async (p: Product) => {
        if (!window.confirm(`Delete "${p.name}"? This archives the product.`)) return;
        try {
            await deleteProduct(p.id);
            toast('Product deleted', 'success');
            await loadAll();
        } catch (err: any) {
            toast(err.response?.data?.message || 'Failed to delete', 'error');
        }
    };

    const field = (label: string, node: React.ReactNode) => (
        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
            {node}
        </div>
    );
    const inputCls = 'w-full bg-white border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-indigo-500';

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Products</h1>
                    <p className="text-gray-500 text-sm">Manage your catalog</p>
                </div>
                <Button onClick={openCreate} className="flex items-center gap-2"><Plus className="w-4 h-4" /> Add New Product</Button>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full text-left">
                        <thead>
                            <tr className="bg-gray-50 text-gray-500 text-xs uppercase tracking-wider">
                                <th className="p-4 font-semibold">Product</th>
                                <th className="p-4 font-semibold">Brand</th>
                                <th className="p-4 font-semibold">Stock</th>
                                <th className="p-4 font-semibold">Status</th>
                                <th className="p-4 font-semibold">Price</th>
                                <th className="p-4 font-semibold text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {loading ? (
                                <tr><td colSpan={6} className="p-8 text-center text-gray-500">Loading…</td></tr>
                            ) : products.length === 0 ? (
                                <tr><td colSpan={6} className="p-8 text-center text-gray-500">No products yet. Click "Add New Product".</td></tr>
                            ) : (
                                products.map((p) => {
                                    const stock = p.variants?.[0]?.stockQuantity ?? 0;
                                    return (
                                        <tr key={p.id} className="hover:bg-gray-50 transition-colors">
                                            <td className="p-4">
                                                <div className="flex items-center gap-3">
                                                    <div className="w-10 h-10 rounded bg-gray-100 flex-shrink-0 overflow-hidden">
                                                        <img src={p.images?.[0]?.imageUrl || 'https://placehold.co/40'} alt="" className="w-full h-full object-cover" />
                                                    </div>
                                                    <p className="font-medium text-gray-900">{p.name}</p>
                                                </div>
                                            </td>
                                            <td className="p-4 text-gray-600">{p.brand?.name || '-'}</td>
                                            <td className="p-4">
                                                <span className={stock <= 0 ? 'text-red-600 font-medium' : stock <= 5 ? 'text-amber-600 font-medium' : 'text-gray-700'}>{stock}</span>
                                            </td>
                                            <td className="p-4">
                                                <span className={`inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium ${p.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'}`}>{p.status}</span>
                                            </td>
                                            <td className="p-4 text-gray-900 font-medium">${p.variants?.[0]?.price ?? '0.00'}</td>
                                            <td className="p-4">
                                                <div className="flex items-center justify-end gap-2">
                                                    <button onClick={() => openEdit(p)} className="p-2 text-indigo-600 hover:bg-indigo-50 rounded-lg" title="Edit"><Pencil className="w-4 h-4" /></button>
                                                    <button onClick={() => handleDelete(p)} className="p-2 text-red-600 hover:bg-red-50 rounded-lg" title="Delete"><Trash2 className="w-4 h-4" /></button>
                                                </div>
                                            </td>
                                        </tr>
                                    );
                                })
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Add/Edit modal */}
            {modalOpen && (
                <div className="fixed inset-0 z-[90] bg-black/40 flex items-center justify-center p-4" onClick={() => setModalOpen(false)}>
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                        <div className="flex items-center justify-between p-6 border-b border-gray-100">
                            <h2 className="text-lg font-bold text-gray-900">{form.id ? 'Edit Product' : 'Add New Product'}</h2>
                            <button onClick={() => setModalOpen(false)} className="p-1 text-gray-400 hover:text-gray-700"><X className="w-5 h-5" /></button>
                        </div>
                        <form onSubmit={handleSave} className="p-6 space-y-4">
                            {field('Name *', <input className={inputCls} value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />)}
                            {field('Short description', <textarea className={inputCls} rows={2} value={form.shortDescription} onChange={(e) => setForm({ ...form, shortDescription: e.target.value })} />)}
                            <div className="grid grid-cols-2 gap-4">
                                {field('Category *', (
                                    <select className={inputCls} value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })} required>
                                        <option value="">Select…</option>
                                        {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
                                    </select>
                                ))}
                                {field('Brand', (
                                    <select className={inputCls} value={form.brandId} onChange={(e) => setForm({ ...form, brandId: e.target.value })}>
                                        <option value="">None</option>
                                        {brands.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
                                    </select>
                                ))}
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                {field('Price ($)', <input type="number" step="0.01" min="0" className={inputCls} value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} />)}
                                {field('Stock quantity', <input type="number" min="0" className={inputCls} value={form.stockQuantity} onChange={(e) => setForm({ ...form, stockQuantity: e.target.value })} />)}
                            </div>
                            {field('Image URL', <input className={inputCls} placeholder="https://…" value={form.imageUrl} onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} />)}
                            {field('Status', (
                                <select className={inputCls} value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                                    <option value="ACTIVE">Active</option>
                                    <option value="DRAFT">Draft</option>
                                    <option value="ARCHIVED">Archived</option>
                                </select>
                            ))}
                            <div className="flex justify-end gap-3 pt-2">
                                <Button type="button" variant="outline" onClick={() => setModalOpen(false)}>Cancel</Button>
                                <Button type="submit" disabled={saving}>{saving ? 'Saving…' : form.id ? 'Save Changes' : 'Create Product'}</Button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};
