import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getProductBySlug } from '../api/catalog';
import type { Product } from '../api/catalog';
import { getProductReviews, createProductReview } from '../api/reviews';
import type { Review } from '../api/reviews';
import { Button } from '../components/Button';
import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from '../app/store';
import { addToCart } from '../features/cart/cartSlice';
import { Star, ArrowLeft, ShoppingCart, Check } from 'lucide-react';

export const ProductDetails: React.FC = () => {
    const { slug } = useParams<{ slug: string }>();
    const navigate = useNavigate();
    const dispatch = useDispatch<AppDispatch>();
    
    const [product, setProduct] = useState<Product | null>(null);
    const [reviews, setReviews] = useState<Review[]>([]);
    const [loading, setLoading] = useState(true);
    const [addingToCart, setAddingToCart] = useState(false);
    
    // Review form state
    const [rating, setRating] = useState(5);
    const [comment, setComment] = useState('');
    const [submittingReview, setSubmittingReview] = useState(false);

    const { user } = useSelector((state: RootState) => state.auth);

    useEffect(() => {
        if (slug) {
            loadProductAndReviews(slug);
        }
    }, [slug]);

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

    const handleAddToCart = () => {
        if (!user) {
            navigate('/login');
            return;
        }
        if (!product) return;
        
        setAddingToCart(true);
        dispatch(addToCart({ productId: product.id, quantity: 1 }))
            .finally(() => {
                setTimeout(() => setAddingToCart(false), 1000);
            });
    };

    const handleSubmitReview = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!product) return;
        setSubmittingReview(true);
        try {
            await createProductReview(product.id, rating, comment);
            setComment('');
            setRating(5);
            // reload reviews
            const reviewsData = await getProductReviews(product.id);
            setReviews(reviewsData.content);
        } catch (error) {
            console.error('Failed to submit review', error);
            alert('Failed to submit review. Have you already reviewed this product?');
        } finally {
            setSubmittingReview(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-950 flex items-center justify-center text-white">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-500"></div>
            </div>
        );
    }

    if (!product) {
        return (
            <div className="min-h-screen bg-gray-950 flex flex-col items-center justify-center text-white">
                <h1 className="text-2xl font-bold mb-4">Product Not Found</h1>
                <Button onClick={() => navigate('/catalog')}>Back to Catalog</Button>
            </div>
        );
    }

    const renderStars = (count: number) => {
        return Array(5).fill(0).map((_, i) => (
            <Star key={i} className={`w-4 h-4 ${i < count ? 'text-yellow-400 fill-yellow-400' : 'text-gray-600'}`} />
        ));
    };

    return (
        <div className="min-h-screen bg-gray-950 text-white font-sans pb-24">
            <nav className="sticky top-0 z-50 backdrop-blur-lg bg-gray-950/80 border-b border-gray-800 h-16 flex items-center px-4 sm:px-6 lg:px-8">
                <Link to="/catalog" className="flex items-center gap-2 text-gray-400 hover:text-white transition-colors">
                    <ArrowLeft className="w-5 h-5" /> Back to Catalog
                </Link>
            </nav>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-12 mb-16">
                    {/* Product Image */}
                    <div className="bg-gray-900 rounded-3xl p-8 flex items-center justify-center border border-gray-800">
                        <img 
                            src={product.images?.[0]?.imageUrl || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=1000&auto=format&fit=crop'} 
                            alt={product.name}
                            className="max-w-full h-auto rounded-xl shadow-2xl"
                        />
                    </div>

                    {/* Product Info */}
                    <div className="flex flex-col justify-center">
                        <p className="text-indigo-400 font-semibold tracking-wider uppercase mb-2">
                            {product.brand?.name || 'Generic'}
                        </p>
                        <h1 className="text-4xl sm:text-5xl font-extrabold text-white mb-4 leading-tight">
                            {product.name}
                        </h1>
                        <div className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-green-400 to-cyan-400 mb-6">
                            ${product.variants?.[0]?.price || '0.00'}
                        </div>
                        <p className="text-gray-400 text-lg mb-8 leading-relaxed">
                            {product.shortDescription}
                        </p>
                        
                        <div className="flex gap-4">
                            <Button 
                                size="lg" 
                                className="flex-1 bg-indigo-600 hover:bg-indigo-500 text-white flex items-center justify-center gap-2"
                                onClick={handleAddToCart}
                                disabled={addingToCart}
                            >
                                {addingToCart ? <Check className="w-5 h-5" /> : <ShoppingCart className="w-5 h-5" />}
                                {addingToCart ? 'Added to Cart' : 'Add to Cart'}
                            </Button>
                        </div>
                    </div>
                </div>

                {/* Reviews Section */}
                <div className="bg-gray-900/50 rounded-3xl p-8 border border-gray-800 backdrop-blur-xl">
                    <h2 className="text-3xl font-bold mb-8 flex items-center gap-3">
                        <Star className="w-8 h-8 text-yellow-400 fill-yellow-400" /> 
                        Customer Reviews
                    </h2>

                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
                        {/* Review List */}
                        <div className="lg:col-span-2 space-y-6">
                            {reviews.length === 0 ? (
                                <p className="text-gray-400 italic">No reviews yet. Be the first to review this product!</p>
                            ) : (
                                reviews.map(review => (
                                    <div key={review.id} className="bg-gray-800/50 p-6 rounded-2xl border border-gray-700">
                                        <div className="flex items-center justify-between mb-4">
                                            <div className="flex items-center gap-3">
                                                <div className="w-10 h-10 rounded-full bg-indigo-500/20 text-indigo-400 flex items-center justify-center font-bold">
                                                    {review.customerName.charAt(0)}
                                                </div>
                                                <div>
                                                    <p className="font-medium text-white">{review.customerName}</p>
                                                    <div className="flex gap-1 mt-1">
                                                        {renderStars(review.rating)}
                                                    </div>
                                                </div>
                                            </div>
                                            <span className="text-sm text-gray-500">
                                                {new Date(review.createdAt).toLocaleDateString()}
                                            </span>
                                        </div>
                                        <p className="text-gray-300 leading-relaxed">"{review.comment}"</p>
                                    </div>
                                ))
                            )}
                        </div>

                        {/* Write Review Form */}
                        <div>
                            {user ? (
                                <div className="bg-gray-800 p-6 rounded-2xl border border-gray-700 sticky top-24">
                                    <h3 className="text-xl font-bold mb-4">Write a Review</h3>
                                    <form onSubmit={handleSubmitReview} className="space-y-4">
                                        <div>
                                            <label className="block text-sm font-medium text-gray-400 mb-2">Rating</label>
                                            <select 
                                                value={rating} 
                                                onChange={(e) => setRating(Number(e.target.value))}
                                                className="w-full bg-gray-900 border border-gray-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                            >
                                                <option value={5}>5 Stars - Excellent</option>
                                                <option value={4}>4 Stars - Very Good</option>
                                                <option value={3}>3 Stars - Average</option>
                                                <option value={2}>2 Stars - Poor</option>
                                                <option value={1}>1 Star - Terrible</option>
                                            </select>
                                        </div>
                                        <div>
                                            <label className="block text-sm font-medium text-gray-400 mb-2">Review</label>
                                            <textarea 
                                                value={comment}
                                                onChange={(e) => setComment(e.target.value)}
                                                rows={4}
                                                className="w-full bg-gray-900 border border-gray-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                                placeholder="What did you like or dislike?"
                                                required
                                            ></textarea>
                                        </div>
                                        <Button type="submit" className="w-full" disabled={submittingReview}>
                                            {submittingReview ? 'Submitting...' : 'Submit Review'}
                                        </Button>
                                    </form>
                                </div>
                            ) : (
                                <div className="bg-gray-800 p-6 rounded-2xl border border-gray-700 text-center">
                                    <h3 className="text-xl font-bold mb-4">Write a Review</h3>
                                    <p className="text-gray-400 mb-6">You must be logged in to leave a review.</p>
                                    <Button variant="outline" onClick={() => navigate('/login')} className="w-full">
                                        Sign In
                                    </Button>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};
