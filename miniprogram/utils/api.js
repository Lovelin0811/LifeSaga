// utils/api.js - 统一请求封装
const { API_BASE } = require('../config');

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token') || '';
    wx.request({
      url: `${API_BASE}${options.url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header,
      },
      success: (res) => {
        if (res.statusCode === 401) {
          // token 过期，清除登录态
          const app = getApp();
          app.logout();
          app.doLogin().then(() => {
            // 重新登录后重试一次
            return request(options).then(resolve).catch(reject);
          }).catch(reject);
          return;
        }
        if (res.statusCode >= 200 && res.statusCode < 300) {
          const data = res.data;
          if (data && data.code === 200) {
            resolve(data.data);
          } else {
            reject({ code: data?.code || res.statusCode, message: data?.message || '请求失败' });
          }
        } else {
          reject({ code: res.statusCode, message: res.data?.message || '请求失败' });
        }
      },
      fail: (err) => {
        reject({ code: -1, message: '网络错误', detail: err });
      },
    });
  });
};

const joinBaseUrl = (baseUrl, path) => {
  if (!path) return path;
  if (/^https?:\/\//i.test(path)) return path;
  return `${baseUrl}${path.startsWith('/') ? '' : '/'}${path}`;
};

// 上传文件
const upload = (options) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token') || '';
    wx.uploadFile({
      url: `${API_BASE}${options.url}`,
      filePath: options.filePath,
      name: options.name || 'file',
      formData: options.formData || {},
      header: {
        'Authorization': token ? `Bearer ${token}` : '',
      },
      success: (res) => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          const data = JSON.parse(res.data);
          if (data && data.code === 200) {
            resolve(data.data);
          } else {
            reject({ code: data?.code, message: data?.message || '上传失败' });
          }
        } else {
          reject({ code: res.statusCode, message: '上传失败' });
        }
      },
      fail: (err) => {
        reject({ code: -1, message: '上传失败', detail: err });
      },
    });
  });
};

// 下载文件
const download = (url) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token') || '';
    wx.downloadFile({
      url: `${API_BASE}${url}`,
      header: {
        'Authorization': token ? `Bearer ${token}` : '',
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.tempFilePath);
        } else {
          reject({ code: res.statusCode, message: '下载失败' });
        }
      },
      fail: (err) => {
        reject({ code: -1, message: '下载失败', detail: err });
      },
    });
  });
};

// 微信登录
const login = async () => {
  const { code } = await wx.login();
  const data = await request({
    url: '/api/auth/wechat-login',
    method: 'POST',
    data: { code },
  });
  return data; // { token, user: { id, nickname, avatarUrl, ... } }
};

// ====== 业务 API ======

const api = {
  // 副本
  getSagas: (keyword) => request({ url: keyword ? `/api/sagas?keyword=${encodeURIComponent(keyword)}` : '/api/sagas' }),
  getPublicSagas: (keyword) => request({ url: keyword ? `/api/sagas/public?keyword=${encodeURIComponent(keyword)}` : '/api/sagas/public' }),
  getSaga: (id) => request({ url: `/api/sagas/${id}` }),
  createSaga: (data) => request({ url: '/api/sagas', method: 'POST', data }),
  updateSaga: (id, data) => request({ url: `/api/sagas/${id}`, method: 'PUT', data }),
  completeSaga: (id) => request({ url: `/api/sagas/${id}/complete`, method: 'PUT' }),
  deleteSaga: (id) => request({ url: `/api/sagas/${id}`, method: 'DELETE' }),

  // 节点
  getNodes: (sagaId) => request({ url: `/api/sagas/${sagaId}/nodes` }),
  getNode: (sagaId, nodeId) => request({ url: `/api/sagas/${sagaId}/nodes/${nodeId}` }),
  createNode: (sagaId, data) => request({ url: `/api/sagas/${sagaId}/nodes`, method: 'POST', data }),
  updateNode: (sagaId, nodeId, data) => request({ url: `/api/sagas/${sagaId}/nodes/${nodeId}`, method: 'PUT', data }),
  deleteNode: (sagaId, nodeId) => request({ url: `/api/sagas/${sagaId}/nodes/${nodeId}`, method: 'DELETE' }),
  toggleNodeMilestone: (sagaId, nodeId) => request({ url: `/api/sagas/${sagaId}/nodes/${nodeId}/toggle-milestone`, method: 'PUT' }),
  toggleNodeFavorite: (sagaId, nodeId) => request({ url: `/api/sagas/${sagaId}/nodes/${nodeId}/favorite`, method: 'PUT' }),

  // 成就
  getAchievements: () => request({ url: '/api/achievements' }),
  getMyAchievements: () => request({ url: '/api/achievements/my' }),

  // 用户
  getUser: () => request({ url: '/api/users/me' }),
  updateUser: (data) => request({ url: '/api/users/me', method: 'PUT', data }),
  getAlbums: () => request({ url: '/api/users/me/albums' }),

  // 文件上传
  uploadFile: async (filePath, formData) => {
    const res = await upload({ url: '/api/upload', filePath, name: 'file', formData });
    return joinBaseUrl(API_BASE, res.url);
  },
};

module.exports = { request, upload, download, login, api };
