const { api } = require('../../utils/api');

Page({
  data: {
    loading: true,
    stats: {
      sagas: 0,
      nodes: 0,
      completed: 0,
      publicSagas: 0,
      avgNodes: 0,
    },
    typeStats: [],
  },

  onLoad() {
    this.loadStats();
  },

  onPullDownRefresh() {
    this.loadStats().finally(() => wx.stopPullDownRefresh());
  },

  async loadStats() {
    this.setData({ loading: true });
    try {
      await getApp().ensureLogin();
      const sagas = await api.getSagas();
      const completed = sagas.filter(s => s.status === 'completed').length;
      const publicSagas = sagas.filter(s => s.isPublic).length;
      const nodes = sagas.reduce((sum, saga) => sum + (saga.nodeCount || 0), 0);
      const typeMap = {};
      sagas.forEach((saga) => {
        typeMap[saga.type] = (typeMap[saga.type] || 0) + 1;
      });
      const typeStats = Object.entries(typeMap).map(([type, count]) => ({
        type,
        typeName: (SAGA_TYPES[type] && SAGA_TYPES[type].name) || type,
        count,
      }));
      this.setData({
        stats: {
          sagas: sagas.length,
          nodes,
          completed,
          publicSagas,
          avgNodes: sagas.length ? Math.round(nodes / sagas.length) : 0,
        },
        typeStats,
        loading: false,
      });
    } catch (err) {
      console.error('Load stats failed:', err);
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },
});
const { SAGA_TYPES } = require('../../utils/util');
