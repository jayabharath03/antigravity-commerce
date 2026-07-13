import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button } from '../components/Button';
import { CheckCircle } from 'lucide-react';

const OrderConfirmation: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center items-center px-4 py-16">
      <div className="bg-white p-8 md:p-12 rounded-3xl shadow-xl border border-gray-100 max-w-lg w-full text-center">
        
        <div className="flex justify-center mb-6">
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center">
            <CheckCircle className="w-10 h-10 text-green-500" />
          </div>
        </div>

        <h1 className="text-3xl font-extrabold text-gray-900 mb-2">Order Confirmed!</h1>
        <p className="text-gray-500 mb-8">
          Thank you for your purchase. We're getting your order ready to be shipped.
        </p>

        <div className="bg-gray-50 rounded-2xl p-6 border border-gray-100 mb-8">
          <p className="text-sm text-gray-500 mb-1">Order Number</p>
          <p className="text-xl font-bold text-gray-900 font-mono tracking-tight">{orderId}</p>
        </div>

        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Button 
            onClick={() => navigate('/catalog')}
            variant="outline"
            className="w-full sm:w-auto"
          >
            Continue Shopping
          </Button>
          <Button
            onClick={() => navigate(`/orders/${orderId}`)}
            variant="primary"
            className="w-full sm:w-auto"
          >
            Track Order
          </Button>
        </div>

      </div>
    </div>
  );
};

export default OrderConfirmation;
