import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { Catalog } from './pages/Catalog';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import OrderConfirmation from './pages/OrderConfirmation';
import { Dashboard } from './pages/Dashboard';
import { AdminProducts } from './pages/AdminProducts';
import { MyOrders } from './pages/MyOrders';
import { OrderDetails } from './pages/OrderDetails';
import { AdminOrders } from './pages/AdminOrders';
import { ProductDetails } from './pages/ProductDetails';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/catalog" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/catalog" element={<Catalog />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/order-confirmation/:orderId" element={<OrderConfirmation />} />
        <Route path="/my-orders" element={<MyOrders />} />
        <Route path="/orders/:orderId" element={<OrderDetails />} />
        <Route path="/products/:slug" element={<ProductDetails />} />
        <Route path="/admin/products" element={<AdminProducts />} />
        <Route path="/admin/orders" element={<AdminOrders />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
