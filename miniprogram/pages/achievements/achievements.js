// pages/achievements/achievements.js
const { api } = require('../../utils/api');
const { RARITY_MAP } = require('../../utils/util');

Page({
  data: {
    loading: true,
    achievements: [],
    userInfo: null,
    unlockedCount: 0,
    xpPercent: 0,
    nextLevelXp: 100,
  },

  onShow() {
    this.loadData();
  },

  async loadData() {
    this.setData({ loading: true });
    try {
      await getApp().ensureLogin();
      const [achievements, userInfo] = await Promise.all([
        api.getAchievements(),
        api.getUser(),
      ]);

      const unlockedCount = achievements.filter(a => a.unlocked).length;
      const list = achievements.map(a => {
        const rarity = RARITY_MAP[a.rarity] || RARITY_MAP.common;
        return { ...a, rarityName: rarity.name };
      });

      const level = userInfo.level || 1;
      const xp = userInfo.xp || 0;
      const levelXp = level * 100;
      const xpPercent = Math.min(100, (xp / levelXp) * 100);

      this.setData({
        achievements: list,
        userInfo,
        unlockedCount,
        xpPercent,
        nextLevelXp: levelXp - (xp % levelXp),
        loading: false,
      });
    } catch (err) {
      console.error('Load achievements failed:', err);
      this.setData({ loading: false });
    }
  },

  showAchievementDetail(e) {
    const achievement = e.currentTarget.dataset.item;
    if (!achievement) return;
    wx.showModal({
      title: achievement.name,
      content: `${achievement.description}\n\n稀有度：${achievement.rarityName}\n奖励：${achievement.xpReward || 0} XP${achievement.unlocked && achievement.unlockedAt ? `\n解锁于：${achievement.unlockedAt}` : ''}`,
      showCancel: false,
      confirmText: '知道了',
    });
  },
});
