import React from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';
import { CategoryBar } from './CategoryBar';
import { Footer } from './Footer';
import { Toaster } from './Toast';

/** App shell: persistent navbar + footer around every page, plus the toast host. */
export const Layout: React.FC = () => {
  return (
    <div className="min-h-screen flex flex-col bg-gray-50 text-gray-900">
      <Navbar />
      <CategoryBar />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
      <Toaster />
    </div>
  );
};
