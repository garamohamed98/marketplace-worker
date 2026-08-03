import { useEffect, useState } from 'react'

export default function App() {
  const [items, setItems] = useState([])
  const [form, setForm] = useState({ title: '', price: '', description: '' })

  useEffect(() => {
    fetch('/api/items')
      .then((r) => r.json())
      .then(setItems)
      .catch(() => {})
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    const newItem = {
      title: form.title.trim(),
      price: Number(form.price),
      description: form.description.trim(),
    }
    await fetch('/api/items', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newItem),
    })
    setItems([...items, newItem])
    setForm({ title: '', price: '', description: '' })
  }

  const handleDelete = async (index) => {
    await fetch(`/api/items/${index}`, { method: 'DELETE' })
    setItems(items.filter((_, i) => i !== index))
  }

  return (
    <div className="container">
      <h1>List an Item</h1>

      <form onSubmit={handleSubmit}>
        <label htmlFor="title">Title</label>
        <input
          id="title"
          value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })}
          required
        />

        <label htmlFor="price">Price</label>
        <input
          id="price"
          type="number"
          step="0.01"
          min="0"
          value={form.price}
          onChange={(e) => setForm({ ...form, price: e.target.value })}
          required
        />

        <label htmlFor="description">Description</label>
        <textarea
          id="description"
          rows="3"
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
          required
        />

        <button type="submit">Save</button>
      </form>

      <h2>Saved Items ({items.length})</h2>
      <table>
        <thead>
          <tr>
            <th>Title</th>
            <th>Price</th>
            <th>Description</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {items.map((item, i) => (
            <tr key={i}>
              <td>{item.title}</td>
              <td>${item.price.toFixed(2)}</td>
              <td>{item.description}</td>
              <td>
                <button type="button" onClick={() => handleDelete(i)}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
