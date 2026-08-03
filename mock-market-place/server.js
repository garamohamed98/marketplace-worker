import { createServer } from 'node:http'
import { readFileSync, writeFileSync, existsSync, mkdirSync } from 'node:fs'
import { join, extname, normalize } from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = fileURLToPath(new URL('.', import.meta.url))
const DATA_FILE = join(__dirname, 'data', 'items.json')
const DIST_DIR = normalize(join(__dirname, 'dist'))
const PORT = process.env.PORT || 3000

const MIME = {
  '.html': 'text/html',
  '.js': 'text/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.png': 'image/png',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
}

const readItems = () => {
  try {
    return JSON.parse(readFileSync(DATA_FILE, 'utf8'))
  } catch {
    return []
  }
}

const writeItems = (items) => {
  mkdirSync(join(__dirname, 'data'), { recursive: true })
  writeFileSync(DATA_FILE, JSON.stringify(items, null, 2))
}

createServer((req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*')
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, DELETE, OPTIONS')
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type')
  if (req.method === 'OPTIONS') {
    res.writeHead(204)
    return res.end()
  }

  const url = new URL(req.url, `http://${req.headers.host}`)

  if (url.pathname === '/api/items') {
    if (req.method === 'GET') {
      res.writeHead(200, { 'Content-Type': 'application/json' })
      return res.end(JSON.stringify(readItems()))
    }
    if (req.method === 'POST') {
      let body = ''
      req.on('data', (chunk) => (body += chunk))
      req.on('end', () => {
        try {
          const item = JSON.parse(body)
          const items = readItems()
          items.push(item)
          writeItems(items)
          res.writeHead(201, { 'Content-Type': 'application/json' })
          res.end(JSON.stringify(item))
        } catch {
          res.writeHead(400, { 'Content-Type': 'application/json' })
          res.end(JSON.stringify({ error: 'Invalid JSON' }))
        }
      })
      return
    }
    res.writeHead(405, { 'Content-Type': 'application/json' })
    return res.end(JSON.stringify({ error: 'Method not allowed' }))
  }

  const deleteMatch = url.pathname.match(/^\/api\/items\/(\d+)$/)
  if (deleteMatch && req.method === 'DELETE') {
    const index = Number(deleteMatch[1])
    const items = readItems()
    if (index < 0 || index >= items.length) {
      res.writeHead(404, { 'Content-Type': 'application/json' })
      return res.end(JSON.stringify({ error: 'Item not found' }))
    }
    items.splice(index, 1)
    writeItems(items)
    res.writeHead(200, { 'Content-Type': 'application/json' })
    return res.end(JSON.stringify({ ok: true }))
  }

  let filePath = join(DIST_DIR, url.pathname === '/' ? 'index.html' : url.pathname)
  if (!existsSync(filePath) || !filePath.startsWith(DIST_DIR)) {
    filePath = join(DIST_DIR, 'index.html')
  }
  const type = MIME[extname(filePath)] || 'application/octet-stream'
  res.writeHead(200, { 'Content-Type': type })
  res.end(readFileSync(filePath))
}).listen(PORT, () => console.log(`Server running at http://localhost:${PORT}`))
