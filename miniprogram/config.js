// config.js
let local = {};
try { local = require('./config.local.js'); } catch (e) {}
const API_BASE = local.API_BASE || 'https://lovelin.com.cn';
console.log('[Config] API_BASE:', API_BASE);
module.exports = { API_BASE };
