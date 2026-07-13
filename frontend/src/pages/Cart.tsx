import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from '../app/store';
import { fetchCart, updateCartItem, removeCartItem } from '../features/cart/cartSlice';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '../components/Button';
import { toast } from '../components/Toast';
import { Trash2, Minus, Plus, ShoppingBag } from 'lucide-react';

const getStock = (product: any) => product?.variants?.[0]?.stockQuantity ?? 0;

const Cart: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { items, isLoading } = useSelector((state: RootState) => state.cart);
  const { user } = useSelector((state: RootState) => state.auth);

  useEffect(() => {
    if (user) {
      dispatch(fetchCart());
    }
  }, [dispatch, user]);

  if (!user) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <ShoppingBag className="w-16 h-16 mx-auto text-gray-400 mb-4" />
        <h2 className="text-2xl font-bold mb-4">Sign in to view your cart</h2>
        <Button onClick={() => navigate('/login')} variant="primary">Sign In</Button>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-16 flex justify-center items-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (!items || items.length === 0) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <ShoppingBag className="w-16 h-16 mx-auto text-gray-400 mb-4" />
        <h2 className="text-2xl font-bold mb-4">Your cart is empty</h2>
        <p className="text-gray-600 mb-8">Looks like you haven't added anything to your cart yet.</p>
        <Button onClick={() => navigate('/catalog')} variant="primary">Start Shopping</Button>
      </div>
    );
  }

  const getProductPrice = (product: any) => {
    if (product.variants && product.variants.length > 0) {
      return product.variants[0].price;
    }
    return 0;
  };

  const getProductImage = (product: any) => {
    const primaryImage = product.images?.find((img: any) => img.isPrimary);
    return primaryImage ? primaryImage.imageUrl : 'https://placehold.co/100x100?text=No+Image';
  };

  const subTotal = items.reduce((total, item) => {
    return total + (getProductPrice(item.product) * item.quantity);
  }, 0);

  const tax = subTotal * 0.1;
  const shipping = subTotal > 100 ? 0 : 10;
  const grandTotal = subTotal + tax + shipping;

  const handleUpdateQuantity = (item: { id: string; quantity: number; product: any }, delta: number) => {
    const newQuantity = item.quantity + delta;
    if (newQuantity <= 0) {
      dispatch(removeCartItem(item.id));
      return;
    }
    const stock = getStock(item.product);
    if (delta > 0 && stock > 0 && newQuantity > stock) {
      toast(`Only ${stock} in stock`, 'error');
      return;
    }
    dispatch(updateCartItem({ itemId: item.id, quantity: newQuantity }))
      .unwrap()
      .catch((msg) => toast(typeof msg === 'string' ? msg : 'Could not update quantity', 'error'));
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-6xl">
      <h1 className="text-3xl font-bold mb-8 text-gray-900">Shopping Cart</h1>

      <div className="flex flex-col lg:flex-row gap-8">
        {/* Cart Items List */}
        <div className="lg:w-2/3 flex flex-col gap-6">
          {items.map((item) => (
            <div key={item.id} className="flex flex-col sm:flex-row items-center gap-6 p-6 bg-white rounded-2xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
              
              <Link to={`/products/${item.product.slug}`} className="shrink-0">
                <img 
                  src={getProductImage(item.product)} 
                  alt={item.product.name} 
                  className="w-24 h-24 object-cover rounded-xl border border-gray-100"
                />
              </Link>
              
              <div className="flex-1 text-center sm:text-left">
                <Link to={`/products/${item.product.slug}`}>
                  <h3 className="text-lg font-semibold text-gray-900 hover:text-indigo-600 transition-colors">
                    {item.product.name}
                  </h3>
                </Link>
                <p className="text-indigo-600 font-bold mt-1">${getProductPrice(item.product).toFixed(2)}</p>
              </div>

              <div className="flex items-center gap-4">
                <div className="flex flex-col items-center gap-1">
                  <div className="flex items-center bg-gray-50 rounded-lg p-1 border border-gray-200">
                    <button
                      onClick={() => handleUpdateQuantity(item, -1)}
                      className="p-2 hover:bg-white rounded-md text-gray-600 transition-colors"
                    >
                      <Minus className="w-4 h-4" />
                    </button>
                    <span className="w-8 text-center font-medium text-gray-900">{item.quantity}</span>
                    <button
                      onClick={() => handleUpdateQuantity(item, 1)}
                      disabled={getStock(item.product) > 0 && item.quantity >= getStock(item.product)}
                      className="p-2 hover:bg-white rounded-md text-gray-600 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
                    >
                      <Plus className="w-4 h-4" />
                    </button>
                  </div>
                  {getStock(item.product) > 0 && item.quantity >= getStock(item.product) && (
                    <span className="text-xs text-amber-600">Max stock ({getStock(item.product)})</span>
                  )}
                </div>
                <button 
                  onClick={() => dispatch(removeCartItem(item.id))}
                  className="p-3 text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                  title="Remove Item"
                >
                  <Trash2 className="w-5 h-5" />
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* Order Summary */}
        <div className="lg:w-1/3">
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 sticky top-24">
            <h2 className="text-xl font-bold mb-6 text-gray-900">Order Summary</h2>
            
            <div className="flex flex-col gap-4 text-gray-600">
              <div className="flex justify-between items-center">
                <span>Subtotal</span>
                <span className="font-semibold text-gray-900">${subTotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between items-center">
                <span>Tax (10%)</span>
                <span className="font-semibold text-gray-900">${tax.toFixed(2)}</span>
              </div>
              <div className="flex justify-between items-center pb-4 border-b border-gray-100">
                <span>Shipping</span>
                <span className="font-semibold text-gray-900">{shipping === 0 ? 'Free' : `$${shipping.toFixed(2)}`}</span>
              </div>
              
              <div className="flex justify-between items-center mt-2">
                <span className="text-lg font-bold text-gray-900">Total</span>
                <span className="text-2xl font-bold text-indigo-600">${grandTotal.toFixed(2)}</span>
              </div>
            </div>

            <Button 
              onClick={() => navigate('/checkout')}
              variant="primary" 
              className="w-full mt-8 py-4 text-lg"
            >
              Proceed to Checkout
            </Button>
            
            {shipping > 0 && (
              <p className="text-sm text-center text-gray-500 mt-4">
                Add ${(100 - subTotal).toFixed(2)} more to your cart for free shipping!
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;
