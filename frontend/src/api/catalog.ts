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

export interface ProductInput {
    name: string;
    shortDescription?: string;
    categoryId: string;
    brandId?: string;
    status?: string;
    price?: number;
    stockQuantity?: number;
    imageUrl?: string;
}

export const createProduct = async (input: ProductInput) => {
    const response = await axios.post('/products', input);
    return response.data;
};

export const updateProduct = async (id: string, input: ProductInput) => {
    const response = await axios.put(`/products/${id}`, input);
    return response.data;
};

export const deleteProduct = async (id: string) => {
    const response = await axios.delete(`/products/${id}`);
    return response.data;
};
