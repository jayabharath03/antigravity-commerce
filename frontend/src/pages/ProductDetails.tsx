import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getProductBySlug } from '../api/catalog';
import type { Product } from '../api/catalog';
import { getProductReviews, createProductReview } from '../api/reviews';
import type { Review } from '../api/reviews';
import { Button } from '../components/Button';
import { toast } from '../components/Toast';
import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from '../app/store';
import { addToCart } from '../features/cart/cartSlice';
import { subscribe } from '../utils/realtime';
import { Star, ArrowLeft, ShoppingCart } from 'lucide-react';

export const ProductDetails: React.FC = () => {
    const { slug } = useParams<{ slug: string }>();
    const navigate = useNavigate();
    const dispatch = useDispatch<AppDispatch>();

    const [product, setProduct] = useState<Product | null>(null);
    const [reviews, setReviews] = useState<Review[]>([]);
    const [loading, setLoading] = useState(true);
    const [addingToCart, setAddingToCart] = useState(false);

    const [rating, setRating] = useState(5);
    const [comment, setComment] = useState('');
    const [submittingReview, setSubmittingReview] = useState(false);

    const { user } = useSelector((state: RootState) => state.auth);

    useEffect(() => {
        if (slug) loadProductAndReviews(slug);
    }, [slug]);

    // Live stock: update this product's stock instantly when anyone buys.
    useEffect(() => {
        if (!product) return;
        const unsubscribe = subscribe(
            '/topic/stock',
            (evt: { productId: string; variantId: string; stockQuantity: number }) => {
                setProduct((prev) => {
                    if (!prev || prev.id !== evt.productId) return prev;
                    return {
                        ...prev,
                        variants: prev.variants?.map((v) =>
                            v.id === evt.variantId ? { ...v, stockQuantity: evt.stockQuantity } : v
                        ),
                    };
                });
            }
        );
        return unsubscribe;
    }, [product?.id]);

    const loadProductAndReviews = async (productSlug: string) => {
        setLoading(true);
        try {
            const prod = await getProductBySlug(productSlug);
            setProduct(prod);
            const reviewsData = await getProductReviews(prod.id);
            setReviews(reviewsData.content);
        } catch (error) {
            console.error('Failed to load product details', error);
        } finally {
            setLoading(false);
        }
    };

    const stock = product?.variants?.[0]?.stockQuantity ?? 0;

    const handleAddToCart = () => {
        if (!user) { navigate('/login'); return; }
        if (!product) return;
        if (stock <= 0) { toast('This item is out of stock', 'error'); return; }
        setAddingToCart(true);
        dispatch(addToCart({ productId: product.id, quantity: 1 }))
            .unwrap()
            .then(() => toast(`${product.name} added to cart`, 'success'))
            .catch(() => toast('Could not add to cart', 'error'))
            .finally(() => setTimeout(() => setAddingToCart(false), 800));
    };

    const handleSubmitReview = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!product) return;
        setSubmittingReview(true);
        try {
            await createProductReview(product.id, rating, comment);
            setComment('');
            setRating(5);
            const reviewsData = await getProductReviews(product.id);
            setReviews(reviewsData.content);
            toast('Thanks for your review!', 'success');
        } catch (error) {
            console.error('Failed to submit review', error);
            toast('Could not submit review. Have you already reviewed this product?', 'error');
        } finally {
            setSubmittingReview(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-[60vh] flex items-center justify-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
            </div>
        );
    }

    if (!product) {
        return (
            <div className="min-h-[60vh] flex flex-col items-center justify-center">
                <h1 className="text-2xl font-bold mb-4 text-gray-900">Product Not Found</h1>
                <Button onClick={() => navigate('/catalog')}>Back to Catalog</Button>
            </div>
        );
    }

    const renderStars = (count: number) =>
        Array(5).fill(0).map((_, i) => (
            <Star key={i} className={`w-4 h-4 ${i < count ? 'text-amber-400 fill-amber-400' : 'text-gray-300'}`} />
        ));

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <Link to="/catalog" className="inline-flex items-center gap-2 text-sm text-gray-500 hover:text-indigo-600 mb-6">
                <ArrowLeft className="w-4 h-4" /> Back to Catalog
            </Link>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-10 mb-16">
                {/* Image */}
                <div className="bg-white rounded-3xl p-6 flex items-center justify-center border border-gray-200">
                    <img
                        src={product.images?.[0]?.imageUrl || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=1000&auto=format&fit=crop'}
                        alt={product.name}
                        className="max-w-full h-auto rounded-xl"
                    />
                </div>

                {/* Info */}
                <div className="flex flex-col justify-center">
                    <p className="text-indigo-600 font-semibold tracking-wider uppercase mb-2 text-sm">{product.brand?.name || 'Generic'}</p>
                    <h1 className="text-3xl sm:text-4xl font-extrabold text-gray-900 mb-4 leading-tight">{product.name}</h1>
                    <div className="text-3xl font-bold text-gray-900 mb-4">${product.variants?.[0]?.price ?? '0.00'}</div>

                    {/* Live stock indicator */}
                    {stock <= 0 ? (
                        <div className="mb-6 inline-flex items-center gap-2 text-red-600 font-semibold"><span className="w-2 h-2 rounded-full bg-red-500" /> Out of stock</div>
                    ) : stock <= 5 ? (
                        <div className="mb-6 inline-flex items-center gap-2 text-amber-600 font-semibold"><span className="w-2 h-2 rounded-full bg-amber-500 animate-pulse" /> Only {stock} left — order soon!</div>
                    ) : (
                        <div className="mb-6 inline-flex items-center gap-2 text-green-600 font-semibold"><span className="w-2 h-2 rounded-full bg-green-500" /> In stock ({stock} available)</div>
                    )}

                    <p className="text-gray-600 text-lg mb-8 leading-relaxed">{product.shortDescription}</p>

                    <Button
                        size="lg"
                        className="w-full sm:w-auto flex items-center justify-center gap-2 px-10"
                        onClick={handleAddToCart}
                        disabled={addingToCart || stock <= 0}
                    >
                        <ShoppingCart className="w-5 h-5" />
                        {stock <= 0 ? 'Out of Stock' : addingToCart ? 'Adding…' : 'Add to Cart'}
                    </Button>
                </div>
            </div>

            {/* Reviews */}
            <div className="bg-white rounded-3xl p-8 border border-gray-200">
                <h2 className="text-2xl font-bold mb-8 flex items-center gap-3 text-gray-900">
                    <Star className="w-7 h-7 text-amber-400 fill-amber-400" /> Customer Reviews
                </h2>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
                    <div className="lg:col-span-2 space-y-6">
                        {reviews.length === 0 ? (
                            <p className="text-gray-500 italic">No reviews yet. Be the first to review this product!</p>
                        ) : (
                            reviews.map((review) => (
                                <div key={review.id} className="bg-gray-50 p-6 rounded-2xl border border-gray-200">
                                    <div className="flex items-center justify-between mb-4">
                                        <div className="flex items-center gap-3">
                                            <div className="w-10 h-10 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center font-bold">
                                                {review.customerName.charAt(0)}
                                            </div>
                                            <div>
                                                <p className="font-medium text-gray-900">{review.customerName}</p>
                                                <div className="flex gap-1 mt-1">{renderStars(review.rating)}</div>
                                            </div>
                                        </div>
                                        <span className="text-sm text-gray-400">{new Date(review.createdAt).toLocaleDateString()}</span>
                                    </div>
                                    <p className="text-gray-700 leading-relaxed">"{review.comment}"</p>
                                </div>
                            ))
                        )}
                    </div>

                    <div>
                        {user ? (
                            <div className="bg-gray-50 p-6 rounded-2xl border border-gray-200 sticky top-24">
                                <h3 className="text-lg font-bold mb-4 text-gray-900">Write a Review</h3>
                                <form onSubmit={handleSubmitReview} className="space-y-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-600 mb-2">Rating</label>
                                        <select value={rating} onChange={(e) => setRating(Number(e.target.value))}
                                            className="w-full bg-white border border-gray-300 rounded-lg px-4 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-indigo-500">
                                            <option value={5}>5 Stars - Excellent</option>
                                            <option value={4}>4 Stars - Very Good</option>
                                            <option value={3}>3 Stars - Average</option>
                                            <option value={2}>2 Stars - Poor</option>
                                            <option value={1}>1 Star - Terrible</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-600 mb-2">Review</label>
                                        <textarea value={comment} onChange={(e) => setComment(e.target.value)} rows={4}
                                            className="w-full bg-white border border-gray-300 rounded-lg px-4 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                            placeholder="What did you like or dislike?" required></textarea>
                                    </div>
                                    <Button type="submit" className="w-full" disabled={submittingReview}>
                                        {submittingReview ? 'Submitting…' : 'Submit Review'}
                                    </Button>
                                </form>
                            </div>
                        ) : (
                            <div className="bg-gray-50 p-6 rounded-2xl border border-gray-200 text-center">
                                <h3 className="text-lg font-bold mb-4 text-gray-900">Write a Review</h3>
                                <p className="text-gray-500 mb-6">You must be logged in to leave a review.</p>
                                <Button variant="outline" onClick={() => navigate('/login')} className="w-full">Sign In</Button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};
