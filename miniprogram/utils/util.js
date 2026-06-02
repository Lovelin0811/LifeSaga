// utils/util.js - 通用工具函数

// 格式化日期
function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hour = String(d.getHours()).padStart(2, '0');
  const min = String(d.getMinutes()).padStart(2, '0');
  const sec = String(d.getSeconds()).padStart(2, '0');

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', min)
    .replace('ss', sec);
}

// 相对时间
function timeAgo(date) {
  if (!date) return '';
  const now = Date.now();
  const past = new Date(date).getTime();
  const diff = now - past;

  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const month = 30 * day;
  const year = 365 * day;

  if (diff < minute) return '刚刚';
  if (diff < hour) return `${Math.floor(diff / minute)} 分钟前`;
  if (diff < day) return `${Math.floor(diff / hour)} 小时前`;
  if (diff < month) return `${Math.floor(diff / day)} 天前`;
  if (diff < year) return `${Math.floor(diff / month)} 个月前`;
  return `${Math.floor(diff / year)} 年前`;
}

function parsePhotos(photos) {
  if (!photos) return [];
  if (Array.isArray(photos)) return photos;
  if (typeof photos !== 'string') return [];
  const value = photos.trim();
  if (!value || value === '[]') return [];
  try {
    const parsed = JSON.parse(value);
    if (Array.isArray(parsed)) return parsed.filter(Boolean);
    return typeof parsed === 'string' && parsed ? [parsed] : [];
  } catch (err) {
    return [value];
  }
}

// 副本类型配置
const SAGA_TYPES = {
  life: { name: '生活副本', icon: '🏠', color: '#E8725A' },
  travel: { name: '旅行副本', icon: '✈️', color: '#5A9FD4' },
  study: { name: '学习副本', icon: '📚', color: '#5CB870' },
  work: { name: '工作副本', icon: '💼', color: '#E5A44D' },
  health: { name: '健身副本', icon: '💪', color: '#6BBF8A' },
  creative: { name: '创作副本', icon: '✨', color: '#B088C4' },
};

// 稀有度配置
const RARITY_MAP = {
  common: { name: '普通', color: '#A8B0B8', class: 'rarity-common' },
  uncommon: { name: '优秀', color: '#5CB870', class: 'rarity-uncommon' },
  rare: { name: '稀有', color: '#5A9FD4', class: 'rarity-rare' },
  epic: { name: '史诗', color: '#B088C4', class: 'rarity-epic' },
  legendary: { name: '传说', color: '#E5A44D', class: 'rarity-legendary' },
  mythic: { name: '神话', color: '#E8725A', class: 'rarity-mythic' },
};

module.exports = {
  formatDate,
  timeAgo,
  parsePhotos,
  SAGA_TYPES,
  RARITY_MAP,
};
