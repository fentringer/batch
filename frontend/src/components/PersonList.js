import React, { useState } from 'react';

function PersonList({ persons, loading, onRefresh, onDelete, onUpdate, onDeleteAll }) {
  const [editingId, setEditingId] = useState(null);
  const [editName, setEditName] = useState('');

  const startEdit = (person) => {
    setEditingId(person.id);
    setEditName(person.name);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditName('');
  };

  const saveEdit = async (id) => {
    if (editName.trim()) {
      await onUpdate(id, editName.trim());
      setEditingId(null);
      setEditName('');
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
        <span>Loading...</span>
      </div>
    );
  }

  if (persons.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">📭</div>
        <div className="empty-state-text">No persons registered yet</div>
        <p style={{ fontSize: '0.875rem', marginTop: '0.75rem', color: '#a0aec0' }}>
          Add a person using the form or run the ETL to import data
        </p>
      </div>
    );
  }

  return (
    <div className="person-list">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', gap: '0.5rem', flexWrap: 'wrap' }}>
        <span style={{ color: '#718096', fontSize: '0.875rem', fontWeight: '500' }}>
          Total: {persons.length} {persons.length === 1 ? 'person' : 'persons'}
        </span>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button onClick={onRefresh} className="btn-secondary" style={{ padding: '0.5rem 1rem', fontSize: '0.875rem' }}>
            🔄 Refresh
          </button>
          {persons.length > 0 && (
            <button onClick={onDeleteAll} className="btn-delete-all" style={{ padding: '0.5rem 1rem', fontSize: '0.875rem' }}>
              🗑️ Delete All
            </button>
          )}
        </div>
      </div>

      {persons.map((person) => (
        <div key={person.id} className="person-item">
          {editingId === person.id ? (
            <>
              <div className="person-info" style={{ flex: 1 }}>
                <div className="person-id">{person.id}</div>
                <input
                  type="text"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  className="edit-input"
                  autoFocus
                  onKeyPress={(e) => e.key === 'Enter' && saveEdit(person.id)}
                />
              </div>
              <div className="person-actions">
                <button onClick={() => saveEdit(person.id)} className="btn-save" title="Save">
                  ✓
                </button>
                <button onClick={cancelEdit} className="btn-cancel" title="Cancel">
                  ✕
                </button>
              </div>
            </>
          ) : (
            <>
              <div className="person-info">
                <div className="person-id">{person.id}</div>
                <div className="person-name">{person.name}</div>
              </div>
              <div className="person-actions">
                <button onClick={() => startEdit(person)} className="btn-edit" title="Edit">
                  ✏️
                </button>
                <button onClick={() => onDelete(person.id)} className="btn-delete" title="Delete">
                  🗑️
                </button>
              </div>
            </>
          )}
        </div>
      ))}
    </div>
  );
}

export default PersonList;





