import React, { useState } from 'react';

function PersonForm({ onSubmit }) {
  const [name, setName] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (name.trim()) {
      onSubmit(name.trim());
      setName('');
    }
  };

  return (
    <form className="person-form" onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="name">Full Name</label>
        <input
          type="text"
          id="name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Enter person's name"
          required
        />
      </div>
      <button type="submit" className="btn-primary">
        ✨ Add Person
      </button>
    </form>
  );
}

export default PersonForm;



