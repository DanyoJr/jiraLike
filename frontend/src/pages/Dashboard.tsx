import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { KanbanSquare, Plus, LogOut } from 'lucide-react';

interface Project {
  id: number;
  name: string;
  description: string;
}

export default function Dashboard() {
  const [projects, setProjects] = useState<Project[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const data = await api.get<Project[]>('/api/projects');
        setProjects(data);
      } catch (err) {
        console.error('Failed to load projects', err);
      }
    };
    fetchProjects();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <>
      <nav className="navbar">
        <div className="navbar-brand">
          <KanbanSquare size={24} color="var(--primary-color)" />
          JiraGest
        </div>
        <div className="navbar-nav">
          <button onClick={handleLogout} className="btn btn-secondary" style={{ padding: '0.5rem 1rem' }}>
            <LogOut size={18} />
            Logout
          </button>
        </div>
      </nav>

      <div className="container animate-fade-in">
        <div className="flex justify-between items-center" style={{ marginBottom: '2rem' }}>
          <h2>Your Projects</h2>
          <button className="btn btn-primary">
            <Plus size={18} />
            New Project
          </button>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.5rem' }}>
          {projects.map((project) => (
            <div 
              key={project.id} 
              className="glass" 
              style={{ padding: '1.5rem', cursor: 'pointer', transition: 'transform 0.2s', display: 'flex', flexDirection: 'column', gap: '1rem' }}
              onClick={() => navigate(`/board/${project.id}`)}
              onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-4px)'}
              onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}
            >
              <h3 style={{ color: 'var(--primary-color)' }}>{project.name}</h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>{project.description}</p>
            </div>
          ))}

          {projects.length === 0 && (
            <div className="glass" style={{ padding: '2rem', gridColumn: '1 / -1', textAlign: 'center', color: 'var(--text-secondary)' }}>
              No projects found. Create one to get started!
            </div>
          )}
        </div>
      </div>
    </>
  );
}
