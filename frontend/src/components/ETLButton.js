import React, { useState, useRef } from 'react';

function ETLButton({ onClick, loading, stats }) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploadMode, setUploadMode] = useState(false);
  const fileInputRef = useRef(null);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      if (!file.name.endsWith('.csv')) {
        alert('Please select a CSV file');
        return;
      }
      setSelectedFile(file);
    }
  };

  const handleRun = () => {
    if (uploadMode && selectedFile) {
      onClick(selectedFile);
    } else {
      onClick(null);
    }
  };

  const handleBrowseClick = () => {
    fileInputRef.current?.click();
  };

  return (
    <div className="etl-section">
      <p style={{ color: '#718096', marginBottom: '1rem', fontSize: '0.95rem', lineHeight: '1.6' }}>
        Execute the ETL process to import data from CSV files.<br/>
        You can use the default sample data or upload your own CSV file.
      </p>

      <div style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
          <button
            onClick={() => { setUploadMode(false); setSelectedFile(null); }}
            className={!uploadMode ? 'btn-mode-active' : 'btn-mode-inactive'}
            style={{
              flex: 1,
              padding: '0.75rem',
              border: !uploadMode ? '2px solid #667eea' : '1px solid #e2e8f0',
              borderRadius: '8px',
              background: !uploadMode ? '#f0f4ff' : 'white',
              color: !uploadMode ? '#667eea' : '#718096',
              fontWeight: '500',
              cursor: 'pointer',
              transition: 'all 0.2s'
            }}
          >
            📊 Use Sample Data
          </button>
          <button
            onClick={() => setUploadMode(true)}
            className={uploadMode ? 'btn-mode-active' : 'btn-mode-inactive'}
            style={{
              flex: 1,
              padding: '0.75rem',
              border: uploadMode ? '2px solid #667eea' : '1px solid #e2e8f0',
              borderRadius: '8px',
              background: uploadMode ? '#f0f4ff' : 'white',
              color: uploadMode ? '#667eea' : '#718096',
              fontWeight: '500',
              cursor: 'pointer',
              transition: 'all 0.2s'
            }}
          >
            📁 Upload CSV File
          </button>
        </div>

        {uploadMode && (
          <div style={{
            border: '2px dashed #cbd5e0',
            borderRadius: '8px',
            padding: '1.5rem',
            textAlign: 'center',
            background: '#f7fafc',
            cursor: 'pointer'
          }}
          onClick={handleBrowseClick}
          >
            <input
              type="file"
              ref={fileInputRef}
              accept=".csv"
              onChange={handleFileChange}
              style={{ display: 'none' }}
            />
            {selectedFile ? (
              <div>
                <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>📄</div>
                <div style={{ color: '#2d3748', fontWeight: '500', marginBottom: '0.25rem' }}>
                  {selectedFile.name}
                </div>
                <div style={{ color: '#718096', fontSize: '0.875rem' }}>
                  {(selectedFile.size / 1024).toFixed(2)} KB
                </div>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    setSelectedFile(null);
                    if (fileInputRef.current) fileInputRef.current.value = '';
                  }}
                  style={{
                    marginTop: '0.75rem',
                    padding: '0.5rem 1rem',
                    background: '#ef4444',
                    color: 'white',
                    border: 'none',
                    borderRadius: '6px',
                    cursor: 'pointer',
                    fontSize: '0.875rem'
                  }}
                >
                  Remove File
                </button>
              </div>
            ) : (
              <div>
                <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>📤</div>
                <div style={{ color: '#2d3748', fontWeight: '500', marginBottom: '0.25rem' }}>
                  Click to browse or drag file here
                </div>
                <div style={{ color: '#718096', fontSize: '0.875rem' }}>
                  CSV files only
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      <button
        onClick={handleRun}
        disabled={loading || (uploadMode && !selectedFile)}
        className="btn-success etl-button"
        style={{
          opacity: loading || (uploadMode && !selectedFile) ? 0.6 : 1,
          cursor: loading || (uploadMode && !selectedFile) ? 'not-allowed' : 'pointer'
        }}
      >
        {loading ? (
          <>
            <div className="spinner" style={{ width: '20px', height: '20px', borderWidth: '2px' }}></div>
            <span>Running ETL...</span>
          </>
        ) : (
          <>
            <span>⚡</span>
            <span>Run ETL Job</span>
          </>
        )}
      </button>

      {stats && (
        <div className="etl-stats">
          <div className="stat-card">
            <div className="stat-label">Status</div>
            <div className="stat-value" style={{ fontSize: '1rem', color: stats.status === 'COMPLETED' ? '#10b981' : '#f59e0b' }}>
              {stats.status || 'N/A'}
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-label">Read</div>
            <div className="stat-value">{stats.readCount || 0}</div>
          </div>
          <div className="stat-card">
            <div className="stat-label">Written</div>
            <div className="stat-value">{stats.writeCount || 0}</div>
          </div>
          <div className="stat-card">
            <div className="stat-label">Skipped</div>
            <div className="stat-value" style={{ color: stats.skipCount > 0 ? '#f59e0b' : '#10b981' }}>
              {stats.skipCount || 0}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ETLButton;



