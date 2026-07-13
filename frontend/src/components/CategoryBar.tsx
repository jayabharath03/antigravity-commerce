import React, { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { getCategories } from '../api/catalog';

export const CategoryBar: React.FC = () => {
  const [categories, setCategories] = useState<any[]>([]);
  const [searchParams] = useSearchParams();
  const active = searchParams.get('category') || '';

  useEffect(() => {
    getCategories().then(setCategories).catch(() => {});
  }, []);

  if (categories.length === 0) return null;

  const linkCls = (isActive: boolean) =>
    `px-3 py-1.5 rounded-full text-sm whitespace-nowrap transition-colors ${
      isActive ? 'bg-indigo-600 text-white' : 'text-gray-600 hover:bg-gray-100'
    }`;

  return (
    <div className="bg-white border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-12 flex items-center gap-2 overflow-x-auto">
        <Link to="/catalog" className={linkCls(active === '')}>All</Link>
        {categories.map((c) => (
          <Link key={c.id} to={`/catalog?category=${c.slug}`} className={linkCls(active === c.slug)}>
            {c.name}
          </Link>
        ))}
      </div>
    </div>
  );
};
