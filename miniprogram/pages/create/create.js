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
    isPublic: false,
    submitting: false,
    isEdit: false,
    editId: null,
  },

  onShow() {
    const app = getApp();
    const sagaId = (app.globalData && app.globalData.editSagaId) || null;
    if (sagaId && sagaId !== this.data.editId) {
      this.setData({ isEdit: true, editId: sagaId });
      wx.setNavigationBarTitle({ title: '编辑副本' });
      this.loadSaga(sagaId);
      app.globalData.editSagaId = null;
    }
  },

  onLoad(options) {
    const app = getApp();
    const sagaId = options.id || (app.globalData && app.globalData.editSagaId);
    if (sagaId) {
      this.setData({ isEdit: true, editId: sagaId });
      wx.setNavigationBarTitle({ title: '编辑副本' });
      this.loadSaga(sagaId);
      if (app.globalData) app.globalData.editSagaId = null;
    }
  },

  async loadSaga(id) {
    try {
      const response = await api.getSaga(id);
      const saga = response.saga;
      this.setData({
        selectedType: saga.type || 'life',
        name: saga.name || '',
        description: saga.description || '',
        coverUrl: saga.coverUrl || '',
        isPublic: !!saga.isPublic,
      });
    } catch (err) {
      console.error('Load saga for edit failed:', err);
      wx.showToast({ title: '加载副本失败', icon: 'none' });
    }
  },

  resetForm() {
    this.setData({
      selectedType: 'life',
      name: '',
      description: '',
      coverUrl: '',
      coverTempPath: '',
      isPublic: false,
      isEdit: false,
      editId: null,
    });
    wx.setNavigationBarTitle({ title: '添加副本' });
  },

  selectType(e) {
    this.setData({ selectedType: e.currentTarget.dataset.key });
  },

  togglePublic(e) {
    this.setData({ isPublic: !!e.detail.value });
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
    const { selectedType, name, description, coverTempPath, isEdit, editId, isPublic } = this.data;

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
        isPublic,
      };

      if (isEdit) {
        await api.updateSaga(editId, payload);
        wx.showToast({ title: '修改成功' });
        this.resetForm();
        setTimeout(() => wx.switchTab({ url: '/pages/home/home' }), 1000);
      } else {
        const saga = await api.createSaga(payload);
        wx.showToast({ title: '创建成功' });
        this.resetForm();
        const app = getApp();
        app.globalData = app.globalData || {};
        app.globalData.pendingDetailId = saga.id;
        setTimeout(() => wx.switchTab({ url: '/pages/home/home' }), 1000);
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
