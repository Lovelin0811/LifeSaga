const { api } = require('../../utils/api');
const { SAGA_TYPES, enumKey, classKey } = require('../../utils/util');

Page({
  data: {
    loading: true,
    stats: {
      sagas: 0,
      nodes: 0,
      completed: 0,
      avgNodes: 0,
      active: 0,
      completionRate: 0,
    },
    typeStats: [],
    topType: null,
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
      const completed = sagas.filter(s => enumKey(s.status) === 'COMPLETED').length;
      const nodes = sagas.reduce((sum, saga) => sum + (saga.nodeCount || 0), 0);
      const typeMap = {};
      sagas.forEach((saga) => {
        const typeKey = enumKey(saga.type, 'LIFE');
        typeMap[typeKey] = (typeMap[typeKey] || 0) + 1;
      });
      const maxTypeCount = Math.max(...Object.values(typeMap), 1);
      const typeStats = Object.entries(typeMap)
        .map(([type, count]) => {
          const config = SAGA_TYPES[type] || SAGA_TYPES.LIFE;
          return {
            type,
            typeClass: classKey(type),
            typeName: config.name || type,
            icon: config.icon || '◦',
            count,
            percent: Math.round((count / maxTypeCount) * 100),
          };
        })
        .sort((a, b) => b.count - a.count);
      this.setData({
        stats: {
          sagas: sagas.length,
          nodes,
          completed,
          active: Math.max(sagas.length - completed, 0),
          avgNodes: sagas.length ? Math.round(nodes / sagas.length) : 0,
          completionRate: sagas.length ? Math.round((completed / sagas.length) * 100) : 0,
        },
        typeStats,
        topType: typeStats[0] || null,
        loading: false,
      });
    } catch (err) {
      console.error('Load stats failed:', err);
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },
});
