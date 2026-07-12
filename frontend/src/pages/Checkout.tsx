import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from '../app/store';
import { useNavigate } from 'react-router-dom';
import { clearCart } from '../features/cart/cartSlice';
import { Button } from '../components/Button';
import { Input } from '../components/Input';
import api from '../utils/axios';
import { CheckCircle, Lock, CreditCard } from 'lucide-react';

const Checkout: React.FC = () => {
  const { items } = useSelector((state: RootState) => state.cart);
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();

  const [address, setAddress] = useState('');
  const [city, setCity] = useState('');
  const [zip, setZip] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD');
  
  const [isProcessing, setIsProcessing] = useState(false);
  const [error, setError] = useState('');

  if (!items || items.length === 0) {
    navigate('/cart');
    return null;
  }

  const getProductPrice = (product: any) => {
    if (product.variants && product.variants.length > 0) {
      return product.variants[0].price;
    }
    return 0;
  };

  const subTotal = items.reduce((total, item) => {
    return total + (getProductPrice(item.product) * item.quantity);
  }, 0);

  const tax = subTotal * 0.1;
  const shipping = subTotal > 100 ? 0 : 10;
  const grandTotal = subTotal + tax + shipping;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsProcessing(true);
    setError('');

    const fullAddress = `${address}, ${city}, ${zip}`;

    try {
      const response = await api.post('/checkout', {
        shippingAddress: fullAddress,
        paymentMethod
      });
      
      if (response.data.success) {
        // Clear local Redux cart
        dispatch(clearCart());
        // Navigate to Order Confirmation
        navigate(`/order-confirmation/${response.data.data.orderNumber}`);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to process checkout');
      setIsProcessing(false);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-6xl">
      <h1 className="text-3xl font-bold mb-8 text-gray-900">Secure Checkout</h1>
      
      <div className="flex flex-col lg:flex-row gap-8">
        {/* Checkout Form */}
        <div className="lg:w-2/3">
          <form onSubmit={handleSubmit} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
            
            {error && (
              <div className="bg-red-50 text-red-600 p-4 rounded-xl mb-6 border border-red-100">
                {error}
              </div>
            )}

            <h2 className="text-xl font-bold mb-6 text-gray-900 flex items-center gap-2">
              <span className="w-8 h-8 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center text-sm">1</span>
              Shipping Address
            </h2>
            
            <div className="space-y-4 mb-10">
              <Input
                label="Street Address"
                value={address}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setAddress(e.target.value)}
                required
                placeholder="123 Main St"
              />
              <div className="grid grid-cols-2 gap-4">
                <Input
                  label="City"
                  value={city}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setCity(e.target.value)}
                  required
                  placeholder="San Francisco"
                />
                <Input
                  label="ZIP Code"
                  value={zip}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setZip(e.target.value)}
                  required
                  placeholder="94105"
                />
              </div>
            </div>

            <h2 className="text-xl font-bold mb-6 text-gray-900 flex items-center gap-2">
              <span className="w-8 h-8 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center text-sm">2</span>
              Payment Method
            </h2>

            <div className="space-y-4 mb-8">
              <label className={`flex items-center gap-4 p-4 rounded-xl border-2 cursor-pointer transition-colors ${paymentMethod === 'CREDIT_CARD' ? 'border-indigo-600 bg-indigo-50' : 'border-gray-200 hover:border-indigo-300'}`}>
                <input 
                  type="radio" 
                  name="payment" 
                  value="CREDIT_CARD"
                  checked={paymentMethod === 'CREDIT_CARD'}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="w-5 h-5 text-indigo-600 focus:ring-indigo-500"
                />
                <CreditCard className={`w-6 h-6 ${paymentMethod === 'CREDIT_CARD' ? 'text-indigo-600' : 'text-gray-400'}`} />
                <div className="flex-1">
                  <p className="font-semibold text-gray-900">Credit Card (Mock)</p>
                  <p className="text-sm text-gray-500">Fast and secure mock payment</p>
                </div>
              </label>
            </div>

            <Button 
              type="submit"
              variant="primary" 
              className="w-full py-4 text-lg flex items-center justify-center gap-2"
              disabled={isProcessing}
            >
              {isProcessing ? (
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
              ) : (
                <>
                  <Lock className="w-5 h-5" />
                  Pay ${grandTotal.toFixed(2)}
                </>
              )}
            </Button>
            <p className="text-center text-gray-400 text-sm mt-4 flex items-center justify-center gap-1">
              <CheckCircle /> Payments are secure and encrypted.
            </p>
          </form>
        </div>

        {/* Order Summary (Sidebar) */}
        <div className="lg:w-1/3">
          <div className="bg-gray-50 rounded-2xl p-6 border border-gray-100 sticky top-24">
            <h2 className="text-xl font-bold mb-6 text-gray-900">Order Summary</h2>
            
            <div className="space-y-4 mb-6">
              {items.map((item) => (
                <div key={item.id} className="flex justify-between text-sm">
                  <div className="flex gap-2">
                    <span className="font-medium text-gray-900">{item.quantity}x</span>
                    <span className="text-gray-600 line-clamp-1">{item.product.name}</span>
                  </div>
                  <span className="font-medium text-gray-900">${(getProductPrice(item.product) * item.quantity).toFixed(2)}</span>
                </div>
              ))}
            </div>

            <div className="border-t border-gray-200 pt-4 flex flex-col gap-3 text-sm text-gray-600">
              <div className="flex justify-between">
                <span>Subtotal</span>
                <span className="font-medium text-gray-900">${subTotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Tax (10%)</span>
                <span className="font-medium text-gray-900">${tax.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Shipping</span>
                <span className="font-medium text-gray-900">{shipping === 0 ? 'Free' : `$${shipping.toFixed(2)}`}</span>
              </div>
              
              <div className="flex justify-between items-center mt-2 pt-4 border-t border-gray-200">
                <span className="text-lg font-bold text-gray-900">Total</span>
                <span className="text-xl font-bold text-indigo-600">${grandTotal.toFixed(2)}</span>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Checkout;
