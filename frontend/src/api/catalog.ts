import axios from '../utils/axios';

export interface Product {
    id: string;
    name: string;
    slug: string;
    shortDescription: string;
    status: string;
    brand: any;
    category: any;
    variants: any[];
    images: any[];
    price?: number; // derived field
}

interface ProductFilters {
    search?: string;
    categorySlug?: string;
    brandSlug?: string;
    minPrice?: number;
    maxPrice?: number;
    page?: number;
    size?: number;
}

export const getProducts = async (filters: ProductFilters = {}) => {
    const response = await axios.get('/products', {
        params: filters
    });
    return response.data; // Page<ProductDto>
};

export const getProductBySlug = async (slug: string) => {
    const response = await axios.get(`/products/slug/${slug}`);
    return response.data;
};

export const getCategories = async () => {
    const response = await axios.get('/categories');
    return response.data;
};

export const getBrands = async () => {
    const response = await axios.get('/brands');
    return response.data;
};
