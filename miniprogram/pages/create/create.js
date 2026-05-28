// pages/create/create.js
const { api } = require('../../utils/api');

const TYPES = [
  { key: 'life', name: '生活', icon: '🏠', color: '#E8725A' },
  { key: 'travel', name: '旅行', icon: '✈️', color: '#5A9FD4' },
  { key: 'study', name: '学习', icon: '📚', color: '#5CB870' },
  { key: 'work', name: '工作', icon: '💼', color: '#E5A44D' },
  { key: 'health', name: '健身', icon: '💪', color: '#6BBF8A' },
  { key: 'creative', name: '创作', icon: '✨', color: '#B088C4' },
];

Page({
  data: {
    types: TYPES,
    selectedType: 'life',
    name: '',
    description: '',
    coverUrl: '',
    coverTempPath: '',
    submitting: false,
    isEdit: false,
    editId: null,
  },

  onLoad(options) {
    // 优先从 options（navigateTo 传参）读，兼容非 tabBar 跳转
    // 实际正常流程走 globalData（因为 create 是 tabBar 页面，只能用 switchTab）
    const app = getApp();
    const sagaId = options.id || (app.globalData && app.globalData.editSagaId);
    if (sagaId) {
      this.setData({ isEdit: true, editId: sagaId });
      wx.setNavigationBarTitle({ title: '编辑副本' });
      this.loadSaga(sagaId);
      // 清除 globalData，避免下次误用
      if (app.globalData) app.globalData.editSagaId = null;
    }
  },

  async loadSaga(id) {
    try {
      const response = await api.getSaga(id);
      const saga = response.saga;  // getSaga 返回 { saga: {...}, nodes: [...] }
      this.setData({
        selectedType: saga.type || 'life',
        name: saga.name || '',
        description: saga.description || '',
        coverUrl: saga.coverUrl || '',
      });
    } catch (err) {
      console.error('Load saga for edit failed:', err);
      wx.showToast({ title: '加载副本失败', icon: 'none' });
    }
  },

  selectType(e) {
    this.setData({ selectedType: e.currentTarget.dataset.key });
  },

  chooseCover() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success: (res) => {
        this.setData({ coverTempPath: res.tempFiles[0].tempFilePath, coverUrl: res.tempFiles[0].tempFilePath });
      },
    });
  },

  async createSaga() {
    if (this.data.submitting) return;
    const { selectedType, name, description, coverTempPath, isEdit, editId } = this.data;

    if (!name) {
      wx.showToast({ title: '请输入副本名称', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });
    wx.showLoading({ title: isEdit ? '保存中' : '创建中' });

    try {
      let coverUrl = this.data.coverUrl;
      if (coverTempPath) {
        coverUrl = await api.uploadFile(coverTempPath);
      }

      const payload = {
        name,
        type: selectedType,
        coverUrl,
        description,
      };

      if (isEdit) {
        await api.updateSaga(editId, payload);
        wx.showToast({ title: '修改成功' });
        setTimeout(() => wx.navigateBack(), 1000);
      } else {
        const saga = await api.createSaga(payload);
        wx.showToast({ title: '创建成功' });
        setTimeout(() => {
          wx.redirectTo({ url: `/pages/detail/detail?id=${saga.id}` });
        }, 1000);
      }
    } catch (err) {
      console.error('Save saga failed:', err);
      wx.showToast({ title: err.message || '保存失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
      wx.hideLoading();
    }
  },
});
