// pages/detail/detail.js
const { api } = require('../../utils/api');
const { SAGA_TYPES, RARITY_MAP, formatDate } = require('../../utils/util');

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
    showActionSheet: false,
    statusBarHeight: 44,
    navBarHeight: 88,
    capsuleRight: 16,
  },

  sagaId: null,

  onLoad(options) {
    const systemInfo = wx.getSystemInfoSync();
    const statusBarHeight = systemInfo.statusBarHeight || 44;
    const navBarHeight = statusBarHeight + 44;
    // 获取胶囊按钮位置，把 more 按钮放到胶囊左侧
    try {
      const rect = wx.getMenuButtonBoundingClientRect();
      this.setData({ statusBarHeight, navBarHeight, capsuleRight: rect.right + 8 });
    } catch (e) {
      this.setData({ statusBarHeight, navBarHeight, capsuleRight: systemInfo.windowWidth - 100 });
    }
    this.sagaId = options.id;
    this.loadData();
  },

  onShow() {
    if (this.data.saga) {
      this.loadNodes(); // 返回时刷新节点列表
      this.refreshSaga(); // 编辑回来后刷新头部信息
    }
  },

  async loadData() {
    this.setData({ loading: true });
    try {
      const response = await api.getSaga(this.sagaId);
      // 后端返回 { saga: {...}, nodes: [...] }
      const saga = response.saga;
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
        photos: typeof n.photos === 'string' ? JSON.parse(n.photos || '[]') : (n.photos || []),
      }));

      this.setData({ saga: processedSaga, nodes, loading: false });
    } catch (err) {
      console.error('Load saga failed:', err);
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  async loadNodes() {
    try {
      const nodes = await api.getNodes(this.sagaId);
      const processed = nodes.map(n => ({
        ...n,
        nodeTimeText: n.nodeTime ? formatDate(n.nodeTime, 'YYYY.MM.DD') : '',
        photos: typeof n.photos === 'string' ? JSON.parse(n.photos || '[]') : (n.photos || []),
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
    this.setData({ showActionSheet: true });
  },

  hideActions() {
    this.setData({ showActionSheet: false });
  },

  goNodeDetail(e) {
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
