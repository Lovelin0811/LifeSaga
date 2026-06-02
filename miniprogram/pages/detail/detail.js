// pages/detail/detail.js
const { api } = require('../../utils/api');
const { SAGA_TYPES, RARITY_MAP, formatDate, parsePhotos } = require('../../utils/util');

const COVER_BGS = {
  life: 'linear-gradient(135deg, #FFF0EB 0%, #FFE5DD 100%)',
  travel: 'linear-gradient(135deg, #EBF3FA 0%, #D6E9F8 100%)',
  study: 'linear-gradient(135deg, #EDF7EF 0%, #D6F0DC 100%)',
  work: 'linear-gradient(135deg, #FFF6E8 0%, #FFEDD0 100%)',
  health: 'linear-gradient(135deg, #EDF8F1 0%, #D6F0E4 100%)',
  creative: 'linear-gradient(135deg, #F5EDF8 0%, #E8D8F0 100%)',
};

Page({
  data: {
    loading: true,
    saga: null,
    nodes: [],
    isOwner: false,
    showActionSheet: false,
    statusBarHeight: 44,
    navBarHeight: 88,
  },

  sagaId: null,

  onLoad(options) {
    const systemInfo = wx.getSystemInfoSync();
    const statusBarHeight = systemInfo.statusBarHeight || 44;
    const navBarHeight = statusBarHeight + 44;
    this.setData({ statusBarHeight, navBarHeight });
    this.sagaId = options.id;
    this.loadData();
  },

  onShow() {
    if (this.data.saga) {
      if (this.data.isOwner) {
        this.loadNodes();
      }
      this.refreshSaga();
    }
  },

  async loadData() {
    this.setData({ loading: true });
    try {
      const response = await api.getSaga(this.sagaId);
      // 后端返回 { saga: {...}, nodes: [...] }
      const saga = response.saga;
      const currentUserId = this.getCurrentUserId();
      const isOwner = !!currentUserId && Number(saga.userId) === Number(currentUserId);
      const type = SAGA_TYPES[saga.type] || SAGA_TYPES.life;
      const rarity = RARITY_MAP[saga.rarity] || RARITY_MAP.common;

      const processedSaga = {
        ...saga,
        typeName: type.name,
        typeIcon: type.icon,
        coverBg: COVER_BGS[saga.type] || COVER_BGS.life,
        rarityName: rarity.name,
        rarityClass: rarity.class,
        rarity: saga.rarity || 'common',
        startedAtText: saga.startedAt ? formatDate(saga.startedAt, 'YYYY.MM.DD') : '',
        level: Math.floor(((saga.nodeCount || 0) + (response.nodes || []).length) / 10) + 1,
        xpPercent: Math.min(100, ((saga.nodeCount || 0) / ((Math.floor((saga.nodeCount || 0) / 10) + 1) * 10)) * 100),
      };

      const nodes = (response.nodes || []).map(n => ({
        ...n,
        nodeTimeText: n.nodeTime ? formatDate(n.nodeTime, 'YYYY.MM.DD') : '',
        photos: parsePhotos(n.photos),
      }));

      this.setData({ saga: processedSaga, nodes, isOwner, loading: false });
    } catch (err) {
      console.error('Load saga failed:', err);
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  getCurrentUserId() {
    const app = getApp();
    const userInfo = app?.globalData?.userInfo;
    if (!userInfo) return null;
    if (userInfo.id != null) return userInfo.id;
    if (userInfo.user && userInfo.user.id != null) return userInfo.user.id;
    return null;
  },

  async loadNodes() {
    try {
      const nodes = await api.getNodes(this.sagaId);
      const processed = nodes.map(n => ({
        ...n,
        nodeTimeText: n.nodeTime ? formatDate(n.nodeTime, 'YYYY.MM.DD') : '',
        photos: parsePhotos(n.photos),
      }));
      this.setData({ nodes: processed });

      // 更新 saga nodeCount
      const saga = this.data.saga;
      if (saga) {
        this.setData({ 'saga.nodeCount': nodes.length });
      }
    } catch (err) {
      console.error('Refresh nodes failed:', err);
    }
  },

  async refreshSaga() {
    try {
      const response = await api.getSaga(this.sagaId);
      const saga = response.saga;
      const type = SAGA_TYPES[saga.type] || SAGA_TYPES.life;
      const rarity = RARITY_MAP[saga.rarity] || RARITY_MAP.common;
      this.setData({
        saga: {
          ...saga,
          typeName: type.name,
          typeIcon: type.icon,
          coverBg: COVER_BGS[saga.type] || COVER_BGS.life,
          rarityName: rarity.name,
          rarityClass: rarity.class,
          rarity: saga.rarity || 'common',
          startedAtText: saga.startedAt ? formatDate(saga.startedAt, 'YYYY.MM.DD') : '',
          level: Math.floor((saga.nodeCount || 0) / 10) + 1,
          xpPercent: Math.min(100, ((saga.nodeCount || 0) / ((Math.floor((saga.nodeCount || 0) / 10) + 1) * 10)) * 100),
        },
      });
    } catch (err) {
      // silent
    }
  },

  goAddNode() {
    if (!this.data.isOwner) return;
    wx.navigateTo({ url: `/pages/add-node/add-node?sagaId=${this.sagaId}` });
  },

  goBack() {
    const pages = getCurrentPages();
    if (pages.length > 1) {
      wx.navigateBack();
    } else {
      wx.switchTab({ url: '/pages/home/home' });
    }
  },

  showActions() {
    if (!this.data.isOwner) return;
    this.setData({ showActionSheet: true });
  },

  hideActions() {
    this.setData({ showActionSheet: false });
  },

  completeSaga() {
    if (!this.data.saga || this.data.saga.status === 'completed') {
      this.hideActions();
      return;
    }
    wx.showModal({
      title: '完成副本',
      content: '完成后副本会标记为已完成，后续可在首页查看完成统计。',
      confirmText: '完成',
      confirmColor: '#E05A5A',
      success: async (res) => {
        if (!res.confirm) return;
        try {
          await api.completeSaga(this.sagaId);
          wx.showToast({ title: '已完成' });
          this.hideActions();
          await this.loadData();
        } catch (err) {
          wx.showToast({ title: err?.message || '操作失败', icon: 'none' });
        }
      },
    });
  },

  goNodeDetail(e) {
    if (!this.data.isOwner) return;
    const { id, sagaId } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/node-detail/node-detail?id=${id}&sagaId=${sagaId}` });
  },

  previewPhoto(e) {
    const { urls, current } = e.currentTarget.dataset;
    wx.previewImage({ current, urls });
  },

  editSaga() {
    // create 是 tabBar 页面，不能用 navigateTo，必须用 switchTab
    // 通过 globalData 传递编辑的 saga ID
    const app = getApp();
    app.globalData = app.globalData || {};
    app.globalData.editSagaId = this.sagaId;
    wx.switchTab({ url: '/pages/create/create' });
  },

  deleteSaga() {
    wx.showModal({
      title: '确认删除',
      content: '删除副本将同时删除所有节点，此操作不可撤销。',
      confirmText: '删除',
      confirmColor: '#E05A5A',
      success: async (res) => {
        if (!res.confirm) return;
        try {
          await api.deleteSaga(this.sagaId);
          wx.showToast({ title: '已删除' });
          setTimeout(() => wx.navigateBack(), 1000);
        } catch (err) {
          wx.showToast({ title: '删除失败', icon: 'none' });
        }
      },
    });
  },

  onShareAppMessage() {
    const { saga } = this.data;
    return {
      title: saga?.name || '人生副本',
      path: `/pages/detail/detail?id=${this.sagaId}`,
    };
  },
});
