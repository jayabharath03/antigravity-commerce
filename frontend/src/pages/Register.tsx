import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, Link } from 'react-router-dom';
import { setCredentials } from '../features/auth/authSlice';
import api from '../utils/axios';
import { Input } from '../components/Input';
import { Button } from '../components/Button';

export const Register: React.FC = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phone: '',
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await api.post('/auth/register', formData);
      if (response.data.success) {
        dispatch(setCredentials({
          user: response.data.data.user,
          accessToken: response.data.data.accessToken,
          refreshToken: response.data.data.refreshToken,
        }));
        navigate('/dashboard');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to register');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8 bg-white p-8 rounded-xl shadow-md">
        <div>
          <h2 className="mt-6 text-center text-3xl font-bold tracking-tight text-gray-900">
            Create an account
          </h2>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {error && (
            <div className="bg-red-50 text-red-500 p-3 rounded-md text-sm text-center">
              {error}
            </div>
          )}
          <div className="space-y-4 rounded-md shadow-sm">
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="First Name"
                name="firstName"
                required
                value={formData.firstName}
                onChange={handleChange}
                placeholder="John"
              />
              <Input
                label="Last Name"
                name="lastName"
                required
                value={formData.lastName}
                onChange={handleChange}
                placeholder="Doe"
              />
            </div>
            <Input
              label="Email address"
              name="email"
              type="email"
              required
              value={formData.email}
              onChange={handleChange}
              placeholder="you@example.com"
            />
            <Input
              label="Phone number (Optional)"
              name="phone"
              type="tel"
              value={formData.phone}
              onChange={handleChange}
              placeholder="+1234567890"
            />
            <Input
              label="Password"
              name="password"
              type="password"
              required
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              minLength={8}
            />
          </div>

          <div>
            <Button type="submit" className="w-full" isLoading={isLoading}>
              Register
            </Button>
          </div>
          <div className="text-center text-sm text-gray-600">
            Already have an account?{' '}
            <Link to="/login" className="font-medium text-blue-600 hover:text-blue-500">
              Sign in
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};
