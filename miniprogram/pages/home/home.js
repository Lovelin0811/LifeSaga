// pages/home/home.js
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
    sagas: [],
    userInfo: null,
    stats: { activeCount: 0, completedCount: 0, totalNodes: 0 },
    statusBarHeight: 44,
  },

  onShow() {
    const systemInfo = wx.getSystemInfoSync();
    this.setData({ statusBarHeight: systemInfo.statusBarHeight || 44 });
    this.loadSagas();
  },

  async loadSagas() {
    this.setData({ loading: true });
    try {
      const app = getApp();
      await app.ensureLogin();

      const sagas = await api.getSagas();
      const active = sagas.filter(s => s.status === 'active');
      const completed = sagas.filter(s => s.status === 'completed');
      const totalNodes = sagas.reduce((sum, s) => sum + (s.nodeCount || 0), 0);

      const list = sagas.map(s => {
        const type = SAGA_TYPES[s.type] || SAGA_TYPES.life;
        const rarity = RARITY_MAP[s.rarity] || RARITY_MAP.common;
        return {
          ...s,
          typeName: type.name,
          typeIcon: type.icon,
          coverBg: COVER_BGS[s.type] || COVER_BGS.life,
          rarityName: rarity.name,
          rarityClass: rarity.class,
          rarity: s.rarity || 'common',
          startedAtText: s.startedAt ? formatDate(s.startedAt, 'YYYY.MM.DD') : '',
          xpPercent: Math.min(100, ((s.nodeCount || 0) / 30) * 100),
          level: Math.floor((s.nodeCount || 0) / 10) + 1,
        };
      });

      // 按更新时间排序
      list.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));

      this.setData({
        sagas: list,
        userInfo: app.globalData.userInfo,
        stats: { activeCount: active.length, completedCount: completed.length, totalNodes },
        loading: false,
      });
    } catch (err) {
      console.error('Load sagas failed:', JSON.stringify(err));
      this.setData({ loading: false });
      const msg = err.message || err.errMsg || '加载失败';
      wx.showToast({ title: msg, icon: 'none', duration: 3000 });
    }
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` });
  },

  goCreate() {
    wx.switchTab({ url: '/pages/create/create' });
  },

  onSearchTap() {
    wx.showToast({ title: '搜索功能开发中', icon: 'none' });
  },

  onShareAppMessage() {
    return {
      title: '人生副本 - 记录你的每一段旅程',
      path: '/pages/home/home',
    };
  },
});
