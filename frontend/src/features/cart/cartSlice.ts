import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import api from '../../utils/axios';

export interface Product {
  id: string;
  name: string;
  slug: string;
  shortDescription: string;
  variants: { price: number }[];
  images: { imageUrl: string; isPrimary: boolean }[];
}

export interface CartItem {
  id: string;
  product: Product;
  quantity: number;
}

export interface CartState {
  items: CartItem[];
  isLoading: boolean;
  error: string | null;
}

const initialState: CartState = {
  items: [],
  isLoading: false,
  error: null,
};

export const fetchCart = createAsyncThunk('cart/fetchCart', async (_, { rejectWithValue }) => {
  try {
    const response = await api.get('/cart');
    return response.data.data;
  } catch (err: any) {
    return rejectWithValue(err.response?.data?.message || 'Failed to fetch cart');
  }
});

export const addToCart = createAsyncThunk(
  'cart/addToCart',
  async ({ productId, quantity }: { productId: string; quantity: number }, { rejectWithValue }) => {
    try {
      const response = await api.post('/cart/items', { productId, quantity });
      return response.data.data;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to add to cart');
    }
  }
);

export const updateCartItem = createAsyncThunk(
  'cart/updateCartItem',
  async ({ itemId, quantity }: { itemId: string; quantity: number }, { rejectWithValue }) => {
    try {
      const response = await api.put(`/cart/items/${itemId}`, { quantity });
      return response.data.data;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to update cart item');
    }
  }
);

export const removeCartItem = createAsyncThunk(
  'cart/removeCartItem',
  async (itemId: string, { rejectWithValue }) => {
    try {
      const response = await api.delete(`/cart/items/${itemId}`);
      return response.data.data;
    } catch (err: any) {
      return rejectWithValue(err.response?.data?.message || 'Failed to remove cart item');
    }
  }
);

export const clearCart = createAsyncThunk('cart/clearCart', async (_, { rejectWithValue }) => {
  try {
    const response = await api.delete('/cart');
    return response.data.data;
  } catch (err: any) {
    return rejectWithValue(err.response?.data?.message || 'Failed to clear cart');
  }
});

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      // Fetch Cart
      .addCase(fetchCart.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchCart.fulfilled, (state, action: PayloadAction<any>) => {
        state.isLoading = false;
        state.items = action.payload?.items || [];
      })
      .addCase(fetchCart.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      // Add to Cart
      .addCase(addToCart.fulfilled, (state, action: PayloadAction<any>) => {
        state.items = action.payload?.items || [];
      })
      // Update Cart
      .addCase(updateCartItem.fulfilled, (state, action: PayloadAction<any>) => {
        state.items = action.payload?.items || [];
      })
      // Remove Item
      .addCase(removeCartItem.fulfilled, (state, action: PayloadAction<any>) => {
        state.items = action.payload?.items || [];
      })
      // Clear Cart
      .addCase(clearCart.fulfilled, (state, action: PayloadAction<any>) => {
        state.items = action.payload?.items || [];
      });
  },
});

export default cartSlice.reducer;
