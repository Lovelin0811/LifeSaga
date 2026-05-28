// pages/profile/profile.js
const { api } = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    stats: { sagas: 0, nodes: 0, achievements: 0 },
  },

  onShow() {
    this.loadData();
  },

  async loadData() {
    try {
      await getApp().ensureLogin();
      const [userInfo, achievements] = await Promise.all([
        api.getUser(),
        api.getAchievements(),
      ]);

      const sagas = await api.getSagas();
      const totalNodes = sagas.reduce((sum, s) => sum + (s.nodeCount || 0), 0);
      const unlockedCount = achievements.filter(a => a.unlocked).length;

      this.setData({
        userInfo,
        stats: {
          sagas: sagas.length,
          nodes: totalNodes,
          achievements: unlockedCount,
        },
      });
    } catch (err) {
      console.error('Load profile failed:', err);
    }
  },

  goAchievements() {
    wx.switchTab({ url: '/pages/achievements/achievements' });
  },

  goAlbums() {
    wx.showToast({ title: '我的相册 - 开发中', icon: 'none' });
  },

  goStats() {
    wx.showToast({ title: '数据统计 - 开发中', icon: 'none' });
  },

  goReminders() {
    wx.showToast({ title: '提醒设置 - 开发中', icon: 'none' });
  },

  goTheme() {
    wx.showToast({ title: '主题与外观 - 开发中', icon: 'none' });
  },

  about() {
    wx.showModal({
      title: '关于人生副本',
      content: '记录你的每一段旅程。\n\n用「副本」的概念来标记人生中重要的时间线，留下珍贵的回忆。',
      showCancel: false,
      confirmText: '知道了',
    });
  },
});
