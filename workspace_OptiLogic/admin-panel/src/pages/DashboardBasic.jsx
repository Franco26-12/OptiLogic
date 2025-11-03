import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import CrearUsuarioModal from '../components/CrearUsuarioModal';

// Iconos básicos con emojis
const Icons = {
  Package: '📦',
  Alert: '⚠️',
  Truck: '🚚',
  Send: '📮',
  Home: '🏠',
  Box: '📦',
  Tag: '🏷️',
  Users: '👥',
  Chart: '📊',
  Settings: '⚙️',
  LogOut: '🚪',
  Menu: '☰',
  Close: '✖️',
  ChevronDown: '▼',
  ChevronRight: '▶',
  Plus: '➕',
  Search: '🔍',
  Eye: '👁️',
  Edit: '✏️',
  QR: '◾',
  Check: '✅'
};

// Componente de Tarjeta de Estadística
const StatCard = ({ icon, title, value, change, color }) => (
  <div className="card cursor-pointer hover:shadow-xl transition-shadow">
    <div className="flex items-start justify-between">
      <div className="flex-1">
        <p className="text-sm font-medium text-gray-600 mb-1">{title}</p>
        <h3 className="text-2xl font-bold text-gray-800">{value}</h3>
        {change && (
          <p className={`text-sm mt-2 ${change > 0 ? 'text-green-600' : 'text-red-600'}`}>
            {change > 0 ? '↑' : '↓'} {Math.abs(change)}% vs mes anterior
          </p>
        )}
      </div>
      <div className={`p-3 rounded-lg ${color}`} style={{ fontSize: '24px' }}>
        {icon}
      </div>
    </div>
  </div>
);

const DashboardBasic = () => {
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [activeSection, setActiveSection] = useState('dashboard');
  const [expandedMenus, setExpandedMenus] = useState(['productos']);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [modalTipo, setModalTipo] = useState('admin');
  
  // Usuario actual
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  // Estados para datos reales
  const [stats, setStats] = useState({
    totalProductos: 0,
    productosStockBajo: 0,
    totalEnvios: 0,
    enviosPendientes: 0,
    totalRepartidores: 0,
    repartidoresActivos: 0
  });

  const [productos, setProductos] = useState([]);
  const [enviosRecientes, setEnviosRecientes] = useState([]);
  const [repartidores, setRepartidores] = useState([]);

  // Cargar datos del backend
  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    setLoading(true);
    const token = localStorage.getItem('token');
    
    try {
      // Cargar productos
      const prodResponse = await fetch('http://localhost:8080/api/productos', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (prodResponse.ok) {
        const prods = await prodResponse.json();
        setProductos(prods.slice(0, 5));
        setStats(prev => ({ ...prev, totalProductos: prods.length }));
      }

      // Cargar envíos
      const envResponse = await fetch('http://localhost:8080/api/envios', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (envResponse.ok) {
        const envs = await envResponse.json();
        setEnviosRecientes(envs.slice(0, 5));
        setStats(prev => ({ 
          ...prev, 
          totalEnvios: envs.length,
          enviosPendientes: envs.filter(e => e.estado === 'PENDIENTE').length
        }));
      }

      // Cargar repartidores
      const repResponse = await fetch('http://localhost:8080/api/usuarios/repartidores', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (repResponse.ok) {
        const reps = await repResponse.json();
        setRepartidores(reps.slice(0, 5));
        setStats(prev => ({ 
          ...prev, 
          totalRepartidores: reps.length,
          repartidoresActivos: reps.filter(r => r.estado === 'DISPONIBLE').length
        }));
      }
    } catch (error) {
      console.error('Error cargando datos:', error);
    } finally {
      setLoading(false);
    }
  };

  // Menú lateral
  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: Icons.Home },
    {
      id: 'productos',
      label: 'Productos',
      icon: Icons.Box,
      expandable: true,
      subItems: [
        { id: 'productos-lista', label: 'Ver Productos' },
        { id: 'productos-nuevo', label: 'Nuevo Producto' },
        { id: 'productos-stock', label: 'Control Stock', badge: stats.productosStockBajo }
      ]
    },
    {
      id: 'categorias',
      label: 'Categorías',
      icon: Icons.Tag,
      expandable: true,
      subItems: [
        { id: 'categorias-lista', label: 'Ver Categorías' },
        { id: 'categorias-nueva', label: 'Nueva Categoría' }
      ]
    },
    {
      id: 'envios',
      label: 'Envíos',
      icon: Icons.Send,
      expandable: true,
      subItems: [
        { id: 'envios-lista', label: 'Todos los Envíos' },
        { id: 'envios-nuevo', label: 'Crear Envío' },
        { id: 'envios-pendientes', label: 'Pendientes', badge: stats.enviosPendientes }
      ]
    },
    {
      id: 'repartidores',
      label: 'Repartidores',
      icon: Icons.Truck,
      expandable: true,
      subItems: [
        { id: 'repartidores-lista', label: 'Ver Repartidores' },
        { id: 'repartidores-nuevo', label: 'Nuevo Repartidor' }
      ]
    },
    {
      id: 'usuarios',
      label: 'Usuarios',
      icon: Icons.Users,
      expandable: true,
      subItems: [
        { id: 'usuarios-admins', label: 'Administradores' },
        { id: 'usuarios-clientes', label: 'Clientes' }
      ]
    },
    { id: 'reportes', label: 'Reportes', icon: Icons.Chart },
    { id: 'configuracion', label: 'Configuración', icon: Icons.Settings }
  ];

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
      <aside className={`${sidebarOpen ? 'w-72' : 'w-0'} bg-white shadow-xl transition-all duration-300 overflow-hidden`}>
        <div className="p-6 border-b">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-600 rounded-lg text-white text-2xl">
              {Icons.Package}
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
                  <span>{item.icon}</span>
                  <span className="font-medium">{item.label}</span>
                </div>
                <div className="flex items-center gap-2">
                  {item.badge && (
                    <span className="px-2 py-1 text-xs bg-red-500 text-white rounded-full">
                      {item.badge}
                    </span>
                  )}
                  {item.expandable && (
                    <span className="text-xs">
                      {expandedMenus.includes(item.id) ? Icons.ChevronDown : Icons.ChevronRight}
                    </span>
                  )}
                </div>
              </button>
              
              {/* Submenú */}
              {item.expandable && expandedMenus.includes(item.id) && (
                <div className="ml-9 mt-1">
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
                </div>
              )}
            </div>
          ))}
        </nav>

        <div className="absolute bottom-0 w-full p-4 border-t bg-white">
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 p-3 text-red-600 hover:bg-red-50 rounded-lg transition-all"
          >
            <span>{Icons.LogOut}</span>
            <span className="font-medium">Cerrar Sesión</span>
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex-1">
        {/* Header */}
        <header className="bg-white shadow-sm border-b">
          <div className="flex items-center justify-between px-6 py-4">
            <div className="flex items-center gap-4">
              <button
                onClick={() => setSidebarOpen(!sidebarOpen)}
                className="p-2 hover:bg-gray-100 rounded-lg text-xl"
              >
                {sidebarOpen ? Icons.Close : Icons.Menu}
              </button>
              <h2 className="text-xl font-semibold text-gray-800">
                {activeSection === 'dashboard' ? 'Dashboard Principal' : 
                 menuItems.find(m => m.id === activeSection.split('-')[0])?.label || 'Dashboard'}
              </h2>
            </div>
            
            <div className="flex items-center gap-4">
              {/* Barra de búsqueda */}
              <div className="relative flex items-center">
                <span className="absolute left-3 text-gray-400">{Icons.Search}</span>
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
                    {(user.nombre?.[0] || 'U').toUpperCase()}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Main Content Area */}
        <main className="p-6 overflow-y-auto h-[calc(100vh-73px)]">
          {activeSection === 'dashboard' && (
            <div className="space-y-6">
              {/* Estadísticas */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <StatCard
                  icon={Icons.Package}
                  title="Total Productos"
                  value={stats.totalProductos}
                  change={12}
                  color="bg-blue-100"
                />
                <StatCard
                  icon={Icons.Alert}
                  title="Stock Bajo"
                  value={stats.productosStockBajo}
                  change={-5}
                  color="bg-yellow-100"
                />
                <StatCard
                  icon={Icons.Send}
                  title="Envíos Totales"
                  value={stats.totalEnvios}
                  change={18}
                  color="bg-green-100"
                />
                <StatCard
                  icon={Icons.Truck}
                  title="Repartidores Activos"
                  value={`${stats.repartidoresActivos}/${stats.totalRepartidores}`}
                  color="bg-purple-100"
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
                    {productos.map((producto, idx) => (
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
                    ))}
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
                    {enviosRecientes.map((envio, idx) => (
                      <div key={idx} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                        <div>
                          <p className="font-medium text-gray-800">#{envio.numeroSeguimiento}</p>
                          <p className="text-sm text-gray-500">{envio.ciudadDestino}</p>
                        </div>
                        <div>
                          <span className={`px-2 py-1 text-xs rounded-full ${getEstadoColor(envio.estado)}`}>
                            {envio.estado}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* Tabla de repartidores */}
              <div className="card">
                <div className="flex items-center justify-between mb-4">
                  <h3 className="text-lg font-semibold text-gray-800">Estado de Repartidores</h3>
                  <div className="flex gap-2">
                    <button 
                      onClick={() => { setModalTipo('admin'); setShowModal(true); }}
                      className="btn btn-secondary text-sm"
                    >
                      {Icons.Plus} Admin
                    </button>
                    <button 
                      onClick={() => { setModalTipo('repartidor'); setShowModal(true); }}
                      className="btn btn-primary text-sm"
                    >
                      {Icons.Plus} Repartidor
                    </button>
                  </div>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead>
                      <tr className="border-b">
                        <th className="text-left py-3 px-4">Repartidor</th>
                        <th className="text-left py-3 px-4">Cédula</th>
                        <th className="text-left py-3 px-4">Estado</th>
                        <th className="text-left py-3 px-4">Zona</th>
                        <th className="text-left py-3 px-4">Envíos Hoy</th>
                        <th className="text-left py-3 px-4">Acciones</th>
                      </tr>
                    </thead>
                    <tbody>
                      {repartidores.map((repartidor, idx) => (
                        <tr key={idx} className="border-b hover:bg-gray-50">
                          <td className="py-3 px-4">
                            <div className="flex items-center gap-2">
                              <span className="text-lg">{Icons.Check}</span>
                              <span className="font-medium">{repartidor.nombre} {repartidor.apellido}</span>
                            </div>
                          </td>
                          <td className="py-3 px-4">{repartidor.cedula}</td>
                          <td className="py-3 px-4">
                            <span className={`px-2 py-1 text-xs rounded-full ${
                              repartidor.estado === 'DISPONIBLE' 
                                ? 'bg-green-100 text-green-800'
                                : 'bg-blue-100 text-blue-800'
                            }`}>
                              {repartidor.estado}
                            </span>
                          </td>
                          <td className="py-3 px-4">{repartidor.zonaAsignada}</td>
                          <td className="py-3 px-4">{repartidor.enviosHoy}</td>
                          <td className="py-3 px-4">
                            <div className="flex gap-2">
                              <button className="text-blue-600 hover:bg-blue-100 p-1 rounded">
                                {Icons.Eye}
                              </button>
                              <button className="text-gray-600 hover:bg-gray-100 p-1 rounded">
                                {Icons.Edit}
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* Mensaje para otras secciones */}
          {activeSection !== 'dashboard' && (
            <div className="flex items-center justify-center h-64">
              <div className="text-center">
                <div className="text-6xl mb-4">🚧</div>
                <h2 className="text-2xl font-bold text-gray-700 mb-2">Sección en Desarrollo</h2>
                <p className="text-gray-500">Esta sección estará disponible pronto</p>
              </div>
            </div>
          )}
        </main>
      </div>

      {/* Modal para crear usuarios */}
      {showModal && (
        <CrearUsuarioModal
          tipo={modalTipo}
          onClose={() => setShowModal(false)}
          onSuccess={() => {
            cargarDatos();
            alert(`${modalTipo === 'admin' ? 'Administrador' : 'Repartidor'} creado exitosamente`);
          }}
        />
      )}
    </div>
  );
};

export default DashboardBasic;
