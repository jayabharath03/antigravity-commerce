import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from '../app/store';
import { logout } from '../features/auth/authSlice';
import { ShoppingCart, LogOut, LayoutGrid } from 'lucide-react';

export const Navbar: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  const { user } = useSelector((s: RootState) => s.auth);
  const { items } = useSelector((s: RootState) => s.cart);
  const count = items?.reduce((n, i) => n + i.quantity, 0) || 0;
  const isAdmin = user?.roles?.includes('ADMIN');

  const handleLogout = () => {
    dispatch(logout());
    navigate('/catalog');
  };

  return (
    <header className="sticky top-0 z-50 bg-white/95 backdrop-blur border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <Link to="/catalog" className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-indigo-600 text-white font-bold flex items-center justify-center">A</div>
          <span className="text-lg font-bold text-gray-900 tracking-tight">Antigravity</span>
        </Link>

        <nav className="flex items-center gap-1 sm:gap-2">
          <Link to="/catalog" className="px-3 py-2 text-sm font-medium text-gray-600 hover:text-indigo-600 transition-colors">Shop</Link>
          {user && (
            <Link to="/my-orders" className="px-3 py-2 text-sm font-medium text-gray-600 hover:text-indigo-600 transition-colors">My Orders</Link>
          )}
          {isAdmin && (
            <Link to="/admin/products" className="px-3 py-2 text-sm font-medium text-gray-600 hover:text-indigo-600 transition-colors flex items-center gap-1">
              <LayoutGrid className="w-4 h-4" /> Admin
            </Link>
          )}

          <Link to="/cart" className="relative p-2 text-gray-700 hover:text-indigo-600 transition-colors" aria-label="Cart">
            <ShoppingCart className="w-6 h-6" />
            {count > 0 && (
              <span className="absolute -top-0.5 -right-0.5 bg-indigo-600 text-white text-[11px] font-bold rounded-full min-w-5 h-5 px-1 flex items-center justify-center">
                {count}
              </span>
            )}
          </Link>

          {user ? (
            <div className="flex items-center gap-2 pl-1 sm:pl-2">
              <span className="hidden sm:inline text-sm text-gray-700">Hi, {user.firstName}</span>
              <button onClick={handleLogout} className="p-2 text-gray-500 hover:text-red-600 transition-colors" title="Log out" aria-label="Log out">
                <LogOut className="w-5 h-5" />
              </button>
            </div>
          ) : (
            <button
              onClick={() => navigate('/login')}
              className="ml-1 sm:ml-2 px-4 py-2 rounded-lg bg-indigo-600 text-white text-sm font-medium hover:bg-indigo-700 transition-colors"
            >
              Sign In
            </button>
          )}
        </nav>
      </div>
    </header>
  );
};
