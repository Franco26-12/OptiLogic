import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Package, TrendingUp, Users, Truck, AlertTriangle,
  ChevronRight, ChevronDown, Home, Box, Tag, UserCheck,
  Send, BarChart3, Settings, LogOut, Menu, X, Plus,
  Search, Filter, RefreshCw, Eye, Edit, Trash2, QrCode,
  Calendar, Clock, CheckCircle, XCircle, AlertCircle
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import {
  productosService,
  categoriasService,
  enviosService,
  usuariosService,
  dashboardService
} from '../services/api';

// Componente de Tarjeta de Estadística
const StatCard = ({ icon: Icon, title, value, change, color, onClick }) => (
  <motion.div
    whileHover={{ scale: 1.02 }}
    whileTap={{ scale: 0.98 }}
    onClick={onClick}
    className="card cursor-pointer hover:shadow-xl transition-shadow"
  >
    <div className="flex items-start justify-between">
      <div className="flex-1">
        <p className="text-sm font-medium text-gray-600 mb-1">{title}</p>
        <h3 className="text-2xl font-bold text-gray-800">{value}</h3>
        {change && (
          <p className={`text-sm mt-2 flex items-center gap-1 ${
            change > 0 ? 'text-green-600' : 'text-red-600'
          }`}>
            <TrendingUp size={16} className={change < 0 ? 'rotate-180' : ''} />
            {Math.abs(change)}% vs mes anterior
          </p>
        )}
      </div>
      <div className={`p-3 rounded-lg bg-${color}-100`}>
        <Icon size={24} className={`text-${color}-600`} />
      </div>
    </div>
  </motion.div>
);

// Componente principal del Dashboard
const Dashboard = () => {
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [activeSection, setActiveSection] = useState('dashboard');
  const [expandedMenus, setExpandedMenus] = useState(['productos']);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  
  // Estados para datos
  const [stats, setStats] = useState({
    totalProductos: 0,
    productosStockBajo: 0,
    totalEnvios: 0,
    enviosPendientes: 0,
    totalRepartidores: 0,
    repartidoresActivos: 0
  });
  
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [enviosRecientes, setEnviosRecientes] = useState([]);
  const [repartidores, setRepartidores] = useState([]);
  
  // Usuario actual
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  // Menú lateral
  const menuItems = [
    {
      id: 'dashboard',
      label: 'Dashboard',
      icon: Home,
      badge: null
    },
    {
      id: 'productos',
      label: 'Productos',
      icon: Box,
      expandable: true,
      subItems: [
        { id: 'productos-lista', label: 'Ver Productos', action: 'list' },
        { id: 'productos-nuevo', label: 'Nuevo Producto', action: 'new' },
        { id: 'productos-stock', label: 'Control Stock', action: 'stock', badge: stats.productosStockBajo }
      ]
    },
    {
      id: 'categorias',
      label: 'Categorías',
      icon: Tag,
      expandable: true,
      subItems: [
        { id: 'categorias-lista', label: 'Ver Categorías', action: 'list' },
        { id: 'categorias-nueva', label: 'Nueva Categoría', action: 'new' }
      ]
    },
    {
      id: 'envios',
      label: 'Envíos',
      icon: Send,
      expandable: true,
      subItems: [
        { id: 'envios-lista', label: 'Todos los Envíos', action: 'list' },
        { id: 'envios-nuevo', label: 'Crear Envío', action: 'new' },
        { id: 'envios-pendientes', label: 'Pendientes', action: 'pending', badge: stats.enviosPendientes },
        { id: 'envios-tracking', label: 'Tracking', action: 'tracking' }
      ]
    },
    {
      id: 'repartidores',
      label: 'Repartidores',
      icon: Truck,
      expandable: true,
      subItems: [
        { id: 'repartidores-lista', label: 'Ver Repartidores', action: 'list' },
        { id: 'repartidores-nuevo', label: 'Nuevo Repartidor', action: 'new' },
        { id: 'repartidores-asignar', label: 'Asignar Rutas', action: 'assign' }
      ]
    },
    {
      id: 'usuarios',
      label: 'Usuarios',
      icon: Users,
      expandable: true,
      subItems: [
        { id: 'usuarios-admins', label: 'Administradores', action: 'admins' },
        { id: 'usuarios-clientes', label: 'Clientes', action: 'clients' }
      ]
    },
    {
      id: 'reportes',
      label: 'Reportes',
      icon: BarChart3,
      badge: null
    },
    {
      id: 'configuracion',
      label: 'Configuración',
      icon: Settings
    }
  ];

  // Cargar datos iniciales
  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      // Cargar estadísticas de productos
      const prodStats = await productosService.getEstadisticas();
      const prodList = await productosService.getAll();
      const catList = await categoriasService.getAll();
      const envStats = await enviosService.getEstadisticas();
      const envList = await enviosService.getAll();
      const repList = await usuariosService.getRepartidores();
      
      setStats({
        totalProductos: prodStats.data.totalActivos || 0,
        productosStockBajo: prodStats.data.conStockBajo || 0,
        totalEnvios: envStats.data.total || 0,
        enviosPendientes: envStats.data.pendientes || 0,
        totalRepartidores: repList.data.length || 0,
        repartidoresActivos: repList.data.filter(r => r.estado === 'DISPONIBLE').length || 0
      });
      
      setProductos(prodList.data.slice(0, 5));
      setCategorias(catList.data);
      setEnviosRecientes(envList.data.slice(0, 5));
      setRepartidores(repList.data.slice(0, 5));
    } catch (error) {
      console.error('Error cargando dashboard:', error);
      // Usar datos de ejemplo si falla la API
      setStats({
        totalProductos: 156,
        productosStockBajo: 8,
        totalEnvios: 342,
        enviosPendientes: 23,
        totalRepartidores: 12,
        repartidoresActivos: 8
      });
    } finally {
      setLoading(false);
    }
  };

  const toggleMenu = (menuId) => {
    setExpandedMenus(prev => 
      prev.includes(menuId) 
        ? prev.filter(id => id !== menuId)
        : [...prev, menuId]
    );
  };

  const handleMenuClick = (item, subItem = null) => {
    if (item.expandable && !subItem) {
      toggleMenu(item.id);
    } else {
      setActiveSection(subItem ? subItem.id : item.id);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    toast.success('Sesión cerrada exitosamente');
    navigate('/login');
  };

  const getEstadoColor = (estado) => {
    const colors = {
      'PENDIENTE': 'bg-yellow-100 text-yellow-800',
      'EN_TRANSITO': 'bg-blue-100 text-blue-800',
      'ENTREGADO': 'bg-green-100 text-green-800',
      'CANCELADO': 'bg-red-100 text-red-800'
    };
    return colors[estado] || 'bg-gray-100 text-gray-800';
  };

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <motion.aside
        initial={{ x: 0 }}
        animate={{ x: sidebarOpen ? 0 : -280 }}
        className="fixed left-0 top-0 h-full w-72 bg-white shadow-xl z-30"
      >
        <div className="p-6 border-b">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-600 rounded-lg">
              <Package size={28} className="text-white" />
            </div>
            <div>
              <h1 className="text-xl font-bold text-gray-800">OptiLogic</h1>
              <p className="text-xs text-gray-500">Sistema de Gestión</p>
            </div>
          </div>
        </div>

        <nav className="p-4 overflow-y-auto h-[calc(100%-140px)]">
          {menuItems.map(item => (
            <div key={item.id} className="mb-1">
              <button
                onClick={() => handleMenuClick(item)}
                className={`w-full flex items-center justify-between p-3 rounded-lg transition-all ${
                  activeSection === item.id
                    ? 'bg-blue-50 text-blue-600'
                    : 'hover:bg-gray-100 text-gray-700'
                }`}
              >
                <div className="flex items-center gap-3">
                  <item.icon size={20} />
                  <span className="font-medium">{item.label}</span>
                </div>
                <div className="flex items-center gap-2">
                  {item.badge && (
                    <span className="px-2 py-1 text-xs bg-red-500 text-white rounded-full">
                      {item.badge}
                    </span>
                  )}
                  {item.expandable && (
                    expandedMenus.includes(item.id) 
                      ? <ChevronDown size={16} />
                      : <ChevronRight size={16} />
                  )}
                </div>
              </button>
              
              {/* Submenú */}
              <AnimatePresence>
                {item.expandable && expandedMenus.includes(item.id) && (
                  <motion.div
                    initial={{ height: 0, opacity: 0 }}
                    animate={{ height: 'auto', opacity: 1 }}
                    exit={{ height: 0, opacity: 0 }}
                    className="ml-9 mt-1"
                  >
                    {item.subItems.map(subItem => (
                      <button
                        key={subItem.id}
                        onClick={() => handleMenuClick(item, subItem)}
                        className={`w-full flex items-center justify-between p-2 rounded-lg text-sm transition-all ${
                          activeSection === subItem.id
                            ? 'bg-blue-50 text-blue-600'
                            : 'hover:bg-gray-100 text-gray-600'
                        }`}
                      >
                        <span>{subItem.label}</span>
                        {subItem.badge && (
                          <span className="px-2 py-0.5 text-xs bg-red-500 text-white rounded-full">
                            {subItem.badge}
                          </span>
                        )}
                      </button>
                    ))}
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          ))}
        </nav>

        <div className="absolute bottom-0 w-full p-4 border-t bg-white">
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 p-3 text-red-600 hover:bg-red-50 rounded-lg transition-all"
          >
            <LogOut size={20} />
            <span className="font-medium">Cerrar Sesión</span>
          </button>
        </div>
      </motion.aside>

      {/* Main Content */}
      <div className={`flex-1 ${sidebarOpen ? 'ml-72' : 'ml-0'} transition-all`}>
        {/* Header */}
        <header className="bg-white shadow-sm border-b">
          <div className="flex items-center justify-between px-6 py-4">
            <div className="flex items-center gap-4">
              <button
                onClick={() => setSidebarOpen(!sidebarOpen)}
                className="p-2 hover:bg-gray-100 rounded-lg"
              >
                {sidebarOpen ? <X size={24} /> : <Menu size={24} />}
              </button>
              <h2 className="text-xl font-semibold text-gray-800">
                {activeSection === 'dashboard' ? 'Dashboard Principal' : 
                 menuItems.find(m => m.id === activeSection.split('-')[0])?.label || 'Dashboard'}
              </h2>
            </div>
            
            <div className="flex items-center gap-4">
              {/* Barra de búsqueda */}
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                <input
                  type="text"
                  placeholder="Buscar..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 pr-4 py-2 border rounded-lg w-64 focus:outline-none focus:border-blue-500"
                />
              </div>
              
              {/* Usuario info */}
              <div className="flex items-center gap-3">
                <div className="text-right">
                  <p className="font-medium text-gray-800">{user.nombre} {user.apellido}</p>
                  <p className="text-xs text-gray-500">{user.rol}</p>
                </div>
                <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
                  <span className="text-white font-bold">
                    {user.nombre?.[0]}{user.apellido?.[0]}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Main Content Area */}
        <main className="p-6 overflow-y-auto h-[calc(100vh-73px)]">
          {loading ? (
            <div className="flex items-center justify-center h-64">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
          ) : (
            <AnimatePresence mode="wait">
              {activeSection === 'dashboard' && (
                <motion.div
                  key="dashboard"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -20 }}
                  className="space-y-6"
                >
                  {/* Estadísticas */}
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                    <StatCard
                      icon={Package}
                      title="Total Productos"
                      value={stats.totalProductos}
                      change={12}
                      color="blue"
                    />
                    <StatCard
                      icon={AlertTriangle}
                      title="Stock Bajo"
                      value={stats.productosStockBajo}
                      change={-5}
                      color="yellow"
                    />
                    <StatCard
                      icon={Send}
                      title="Envíos Totales"
                      value={stats.totalEnvios}
                      change={18}
                      color="green"
                    />
                    <StatCard
                      icon={Truck}
                      title="Repartidores Activos"
                      value={`${stats.repartidoresActivos}/${stats.totalRepartidores}`}
                      color="purple"
                    />
                  </div>

                  {/* Tablas y listas */}
                  <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {/* Productos con stock bajo */}
                    <div className="card">
                      <div className="flex items-center justify-between mb-4">
                        <h3 className="text-lg font-semibold text-gray-800">Productos - Stock Crítico</h3>
                        <button className="text-blue-600 hover:underline text-sm">
                          Ver todos
                        </button>
                      </div>
                      <div className="space-y-2">
                        {productos.length > 0 ? productos.map((producto, idx) => (
                          <div key={idx} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                            <div>
                              <p className="font-medium text-gray-800">{producto.nombre}</p>
                              <p className="text-sm text-gray-500">SKU: {producto.sku}</p>
                            </div>
                            <div className="text-right">
                              <p className="font-bold text-red-600">{producto.stockActual} uds</p>
                              <p className="text-xs text-gray-500">Min: {producto.stockMinimo}</p>
                            </div>
                          </div>
                        )) : (
                          <p className="text-gray-500 text-center py-4">No hay productos con stock bajo</p>
                        )}
                      </div>
                    </div>

                    {/* Envíos recientes */}
                    <div className="card">
                      <div className="flex items-center justify-between mb-4">
                        <h3 className="text-lg font-semibold text-gray-800">Envíos Recientes</h3>
                        <button className="text-blue-600 hover:underline text-sm">
                          Ver todos
                        </button>
                      </div>
                      <div className="space-y-2">
                        {enviosRecientes.length > 0 ? enviosRecientes.map((envio, idx) => (
                          <div key={idx} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                            <div>
                              <p className="font-medium text-gray-800">#{envio.numeroSeguimiento}</p>
                              <p className="text-sm text-gray-500">{envio.ciudadDestino}</p>
                            </div>
                            <div className="flex items-center gap-2">
                              <span className={`px-2 py-1 text-xs rounded-full ${getEstadoColor(envio.estado)}`}>
                                {envio.estado}
                              </span>
                            </div>
                          </div>
                        )) : (
                          <p className="text-gray-500 text-center py-4">No hay envíos recientes</p>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Tabla de repartidores */}
                  <div className="card">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-lg font-semibold text-gray-800">Estado de Repartidores</h3>
                      <button className="btn btn-primary btn-sm">
                        <Plus size={16} />
                        Nuevo Repartidor
                      </button>
                    </div>
                    <div className="overflow-x-auto">
                      <table className="w-full">
                        <thead>
                          <tr className="border-b">
                            <th className="text-left py-3 px-4 font-medium text-gray-600">Repartidor</th>
                            <th className="text-left py-3 px-4 font-medium text-gray-600">Cédula</th>
                            <th className="text-left py-3 px-4 font-medium text-gray-600">Estado</th>
                            <th className="text-left py-3 px-4 font-medium text-gray-600">Zona</th>
                            <th className="text-left py-3 px-4 font-medium text-gray-600">Envíos Hoy</th>
                            <th className="text-left py-3 px-4 font-medium text-gray-600">Acciones</th>
                          </tr>
                        </thead>
                        <tbody>
                          {repartidores.length > 0 ? repartidores.map((repartidor, idx) => (
                            <tr key={idx} className="border-b hover:bg-gray-50">
                              <td className="py-3 px-4">
                                <div className="flex items-center gap-3">
                                  <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                                    <UserCheck size={16} className="text-blue-600" />
                                  </div>
                                  <span className="font-medium">{repartidor.nombre} {repartidor.apellido}</span>
                                </div>
                              </td>
                              <td className="py-3 px-4">{repartidor.cedula}</td>
                              <td className="py-3 px-4">
                                <span className={`px-2 py-1 text-xs rounded-full ${
                                  repartidor.estado === 'DISPONIBLE' 
                                    ? 'bg-green-100 text-green-800'
                                    : repartidor.estado === 'EN_RUTA'
                                    ? 'bg-blue-100 text-blue-800'
                                    : 'bg-gray-100 text-gray-800'
                                }`}>
                                  {repartidor.estado}
                                </span>
                              </td>
                              <td className="py-3 px-4">{repartidor.zonaAsignada || 'Sin zona'}</td>
                              <td className="py-3 px-4">{repartidor.enviosHoy || 0}</td>
                              <td className="py-3 px-4">
                                <div className="flex gap-2">
                                  <button className="p-1 hover:bg-blue-100 rounded text-blue-600">
                                    <Eye size={16} />
                                  </button>
                                  <button className="p-1 hover:bg-gray-100 rounded text-gray-600">
                                    <Edit size={16} />
                                  </button>
                                </div>
                              </td>
                            </tr>
                          )) : (
                            <tr>
                              <td colSpan="6" className="text-center py-8 text-gray-500">
                                No hay repartidores registrados
                              </td>
                            </tr>
                          )}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          )}
        </main>
      </div>
    </div>
  );
};

export default Dashboard;
