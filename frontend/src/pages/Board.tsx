import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { KanbanSquare, ArrowLeft, Plus } from 'lucide-react';

interface Issue {
  id: number;
  title: string;
  description: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  priority: string;
  type: string;
}

export default function Board() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [issues, setIssues] = useState<Issue[]>([]);
  const [projectName, setProjectName] = useState('Project Board');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const project = await api.get<{ name: string }>(`/api/projects/${id}`);
        setProjectName(project.name);
        const issuesData = await api.get<Issue[]>(`/api/projects/${id}/issues`);
        setIssues(issuesData);
      } catch (err) {
        console.error('Failed to load board data', err);
      }
    };
    fetchData();
  }, [id]);

  const updateIssueStatus = async (issueId: number, newStatus: 'TODO' | 'IN_PROGRESS' | 'DONE') => {
    try {
      await api.patch(`/api/issues/${issueId}/status`, { status: newStatus });
      setIssues(issues.map(i => i.id === issueId ? { ...i, status: newStatus } : i));
    } catch (err) {
      console.error('Failed to update status', err);
    }
  };

  const getStatusBadgeClass = (status: string) => {
    switch(status) {
      case 'TODO': return 'badge-todo';
      case 'IN_PROGRESS': return 'badge-in-progress';
      case 'DONE': return 'badge-done';
      default: return 'badge-todo';
    }
  };

  const renderColumn = (status: 'TODO' | 'IN_PROGRESS' | 'DONE', title: string) => {
    const columnIssues = issues.filter(i => i.status === status);
    
    return (
      <div className="board-column glass-panel" style={{ padding: '1rem' }}>
        <div className="flex justify-between items-center" style={{ marginBottom: '1rem', paddingBottom: '0.5rem', borderBottom: '1px solid var(--surface-border)' }}>
          <h3 style={{ fontSize: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            {title}
            <span style={{ fontSize: '0.75rem', background: 'rgba(255,255,255,0.1)', padding: '0.1rem 0.5rem', borderRadius: '1rem' }}>
              {columnIssues.length}
            </span>
          </h3>
        </div>
        
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', flex: 1 }}>
          {columnIssues.map(issue => (
            <div key={issue.id} className="glass issue-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                <span className={`badge ${getStatusBadgeClass(issue.status)}`}>{issue.type}</span>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>{issue.priority}</span>
              </div>
              <h4 style={{ marginBottom: '0.5rem' }}>{issue.title}</h4>
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginBottom: '1rem', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                {issue.description}
              </p>
              
              <div className="flex gap-2" style={{ marginTop: 'auto' }}>
                {status !== 'TODO' && (
                  <button 
                    onClick={() => updateIssueStatus(issue.id, status === 'DONE' ? 'IN_PROGRESS' : 'TODO')}
                    style={{ fontSize: '0.75rem', padding: '0.25rem 0.5rem', background: 'rgba(255,255,255,0.1)', border: 'none', borderRadius: '4px', color: 'white', cursor: 'pointer' }}
                  >
                    Move Back
                  </button>
                )}
                {status !== 'DONE' && (
                  <button 
                    onClick={() => updateIssueStatus(issue.id, status === 'TODO' ? 'IN_PROGRESS' : 'DONE')}
                    style={{ fontSize: '0.75rem', padding: '0.25rem 0.5rem', background: 'var(--primary-color)', border: 'none', borderRadius: '4px', color: 'white', cursor: 'pointer', marginLeft: 'auto' }}
                  >
                    Move Forward
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  };

  return (
    <>
      <nav className="navbar">
        <div className="flex items-center gap-4">
          <button onClick={() => navigate('/dashboard')} className="btn btn-secondary" style={{ padding: '0.5rem', borderRadius: '50%' }}>
            <ArrowLeft size={18} />
          </button>
          <div className="navbar-brand">
            <KanbanSquare size={24} color="var(--primary-color)" />
            {projectName}
          </div>
        </div>
        <div className="navbar-nav">
          <button className="btn btn-primary" style={{ padding: '0.5rem 1rem' }}>
            <Plus size={18} />
            Create Issue
          </button>
        </div>
      </nav>

      <div className="container animate-fade-in" style={{ maxWidth: '100%' }}>
        <div className="board-container">
          {renderColumn('TODO', 'To Do')}
          {renderColumn('IN_PROGRESS', 'In Progress')}
          {renderColumn('DONE', 'Done')}
        </div>
      </div>
    </>
  );
}
