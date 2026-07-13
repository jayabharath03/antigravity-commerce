import React from 'react';
import { Link } from 'react-router-dom';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-900 text-gray-400 mt-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 grid grid-cols-2 md:grid-cols-4 gap-8">
        <div className="col-span-2 md:col-span-1">
          <div className="flex items-center gap-2 mb-3">
            <div className="w-8 h-8 rounded-lg bg-indigo-600 text-white font-bold flex items-center justify-center">A</div>
            <span className="text-lg font-bold text-white">Antigravity</span>
          </div>
          <p className="text-sm">Your everyday store for tech, audio, wearables & more.</p>
        </div>
        <div>
          <h4 className="text-white font-semibold mb-3 text-sm">Shop</h4>
          <ul className="space-y-2 text-sm">
            <li><Link to="/catalog" className="hover:text-white">All Products</Link></li>
            <li><Link to="/cart" className="hover:text-white">Cart</Link></li>
            <li><Link to="/my-orders" className="hover:text-white">My Orders</Link></li>
          </ul>
        </div>
        <div>
          <h4 className="text-white font-semibold mb-3 text-sm">Company</h4>
          <ul className="space-y-2 text-sm">
            <li><span className="hover:text-white cursor-pointer">About</span></li>
            <li><span className="hover:text-white cursor-pointer">Careers</span></li>
            <li><span className="hover:text-white cursor-pointer">Contact</span></li>
          </ul>
        </div>
        <div>
          <h4 className="text-white font-semibold mb-3 text-sm">Support</h4>
          <ul className="space-y-2 text-sm">
            <li><span className="hover:text-white cursor-pointer">Shipping</span></li>
            <li><span className="hover:text-white cursor-pointer">Returns</span></li>
            <li><span className="hover:text-white cursor-pointer">FAQ</span></li>
          </ul>
        </div>
      </div>
      <div className="border-t border-gray-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 text-sm text-center">
          © {new Date().getFullYear()} Antigravity Commerce. Built as a full-stack demo.
        </div>
      </div>
    </footer>
  );
};
