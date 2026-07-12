import axios from '../utils/axios';

export interface Review {
    id: string;
    rating: number;
    comment: string;
    customerName: string;
    createdAt: string;
}

export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

export const getProductReviews = async (productId: string, page = 0, size = 10): Promise<Page<Review>> => {
    const response = await axios.get(`/products/${productId}/reviews`, {
        params: { page, size }
    });
    return response.data;
};

export const createProductReview = async (productId: string, rating: number, comment: string): Promise<Review> => {
    const response = await axios.post(`/products/${productId}/reviews`, {
        rating,
        comment
    });
    return response.data;
};
