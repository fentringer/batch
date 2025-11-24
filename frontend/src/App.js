import React, { useState, useEffect } from 'react';
import axios from 'axios';
import PersonList from './components/PersonList';
import ETLButton from './components/ETLButton';
import './App.css';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

function App() {
  const [persons, setPersons] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ text: '', type: '' });
  const [etlStats, setEtlStats] = useState(null);

  useEffect(() => {
    fetchPersons();
  }, []);

  const fetchPersons = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_URL}/person/all`);
      setPersons(response.data);
      setMessage({ text: '', type: '' });
    } catch (error) {
      console.error('Error fetching persons:', error);
      setMessage({ text: 'Error fetching persons from database', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const createPerson = async (name) => {
    try {
      await axios.post(`${API_URL}/person/create?name=${encodeURIComponent(name)}`);
      setMessage({ text: '✅ Person created successfully!', type: 'success' });
      fetchPersons();
    } catch (error) {
      console.error('Error creating person:', error);
      setMessage({ text: '❌ Error creating person', type: 'error' });
    }
  };

  const updatePerson = async (id, name) => {
    try {
      await axios.put(`${API_URL}/person/${id}?name=${encodeURIComponent(name)}`);
      setMessage({ text: '✅ Person updated successfully!', type: 'success' });
      fetchPersons();
    } catch (error) {
      console.error('Error updating person:', error);
      setMessage({ text: '❌ Error updating person', type: 'error' });
    }
  };

  const deletePerson = async (id) => {
    try {
      await axios.delete(`${API_URL}/person/${id}`);
      setMessage({ text: '✅ Person deleted successfully!', type: 'success' });
      fetchPersons();
    } catch (error) {
      console.error('Error deleting person:', error);
      setMessage({ text: '❌ Error deleting person', type: 'error' });
    }
  };

  const deleteAllPersons = async () => {
    try {
      await axios.delete(`${API_URL}/person/all`);
      setMessage({ text: `✅ All persons deleted successfully!`, type: 'success' });
      fetchPersons();
    } catch (error) {
      console.error('Error deleting all persons:', error);
      setMessage({ text: '❌ Error deleting all persons', type: 'error' });
    }
  };

  const runETL = async (fileOrNull) => {
    setLoading(true);
    setEtlStats(null);

    try {
      let response;

      if (fileOrNull instanceof File) {
        // File upload mode
        const formData = new FormData();
        formData.append('file', fileOrNull);

        response = await axios.post(`${API_URL}/etl/upload`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });

        const { writeCount = 0, duplicateCount = 0, readCount = 0 } = response.data;
        let messageText = `✅ ETL Job completed! File: ${fileOrNull.name}`;

        if (duplicateCount > 0) {
          messageText += ` | 📊 ${writeCount} saved, ${duplicateCount} duplicates skipped`;
        } else {
          messageText += ` | 📊 ${writeCount} records saved`;
        }

        setMessage({
          text: messageText,
          type: 'success'
        });
      } else {
        // Sample data mode
        response = await axios.post(`${API_URL}/etl/run?file=data`);

        setMessage({
          text: `✅ ETL Job completed! Processed ${response.data.readCount || 0} records from sample data`,
          type: 'success'
        });
      }

      setEtlStats(response.data);
      fetchPersons();
    } catch (error) {
      console.error('Error running ETL:', error);
      setMessage({
        text: error.response?.data?.message || '❌ Error executing ETL Job',
        type: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Person Management System</h1>
        <p>Spring Boot 3 • Spring Batch • Oracle Database • React</p>
      </header>

      <main className="App-main">
        {message.text && (
          <div className={`message ${message.type}`}>
            {message.text}
          </div>
        )}

        <div className="container">

          <div className="section full-width">
            <h2>ETL Process</h2>
            <ETLButton onClick={runETL} loading={loading} stats={etlStats} />
          </div>
        </div>

        <div className="section full-width">
          <h2>Persons List</h2>
          <PersonList
            persons={persons}
            loading={loading}
            onRefresh={fetchPersons}
            onUpdate={updatePerson}
            onDelete={deletePerson}
            onDeleteAll={deleteAllPersons}
          />
        </div>
      </main>

      <footer className="App-footer">
        <p>Built with ❤️ using Spring Batch ETL Pipeline</p>
      </footer>
    </div>
  );
}

export default App;


