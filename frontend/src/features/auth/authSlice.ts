import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
}

const savedUser = localStorage.getItem('user');
const savedToken = localStorage.getItem('accessToken');

const initialState: AuthState = {
  user: savedUser ? JSON.parse(savedUser) : null,
  isAuthenticated: !!savedToken,
  loading: false,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (
      state,
      action: PayloadAction<{ user: User; accessToken: string; refreshToken: string }>
    ) => {
      state.user = action.payload.user;
      state.isAuthenticated = true;
      state.loading = false;
      localStorage.setItem('accessToken', action.payload.accessToken);
      localStorage.setItem('refreshToken', action.payload.refreshToken);
      localStorage.setItem('user', JSON.stringify(action.payload.user));
    },
    logout: (state) => {
      state.user = null;
      state.isAuthenticated = false;
      state.loading = false;
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setUser: (state, action: PayloadAction<User>) => {
      state.user = action.payload;
      state.isAuthenticated = true;
      state.loading = false;
    },
  },
});

export const { setCredentials, logout, setLoading, setUser } = authSlice.actions;

export default authSlice.reducer;
