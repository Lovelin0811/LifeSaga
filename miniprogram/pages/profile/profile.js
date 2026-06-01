// pages/profile/profile.js
const { api } = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    stats: { sagas: 0, nodes: 0, achievements: 0 },
    editingProfile: false,
    profileForm: { nickname: '', avatarUrl: '' },
    reminderEnabled: false,
    theme: 'light',
  },

  onShow() {
    this.setData({
      reminderEnabled: !!wx.getStorageSync('lifesaga_reminder_enabled'),
      theme: wx.getStorageSync('lifesaga_theme') || 'light',
    });
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
        profileForm: {
          nickname: userInfo.nickname || '',
          avatarUrl: userInfo.avatarUrl || '',
        },
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
    wx.navigateTo({ url: '/pages/albums/albums' });
  },

  goStats() {
    wx.navigateTo({ url: '/pages/stats/stats' });
  },

  goReminders() {
    const reminderEnabled = !this.data.reminderEnabled;
    this.setData({ reminderEnabled });
    wx.setStorageSync('lifesaga_reminder_enabled', reminderEnabled);
    wx.showToast({
      title: reminderEnabled ? '已开启提醒' : '已关闭提醒',
      icon: 'none',
    });
  },

  goTheme() {
    const currentTheme = this.data.theme || 'light';
    const nextTheme = currentTheme === 'light' ? 'dark' : 'light';
    this.setData({ theme: nextTheme });
    wx.setStorageSync('lifesaga_theme', nextTheme);
    const app = getApp();
    if (app && typeof app.setTheme === 'function') {
      app.setTheme(nextTheme);
    }
    wx.showToast({ title: nextTheme === 'light' ? '已切回浅色' : '已切到深色', icon: 'none' });
  },

  startEditProfile() {
    const { userInfo } = this.data;
    this.setData({
      editingProfile: true,
      profileForm: {
        nickname: userInfo?.nickname || '',
        avatarUrl: userInfo?.avatarUrl || '',
      },
    });
  },

  onAvatarTap() {
    if (!this.data.editingProfile) {
      this.startEditProfile();
      return;
    }
    this.chooseAvatar();
  },

  cancelEditProfile() {
    this.setData({ editingProfile: false });
  },

  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const filePath = res.tempFiles[0].tempFilePath;
        this.setData({ 'profileForm.avatarUrl': filePath });
      },
    });
  },

  onNicknameInput(e) {
    this.setData({ 'profileForm.nickname': e.detail.value });
  },

  async saveProfile() {
    const { profileForm } = this.data;
    if (!profileForm.nickname.trim()) {
      wx.showToast({ title: '请输入昵称', icon: 'none' });
      return;
    }
    try {
      wx.showLoading({ title: '保存中' });
      let avatarUrl = profileForm.avatarUrl;
      if (avatarUrl && !/^https?:\/\//i.test(avatarUrl)) {
        avatarUrl = await api.uploadFile(avatarUrl);
      }
      const userInfo = await api.updateUser({
        nickname: profileForm.nickname.trim(),
        avatarUrl,
      });
      const app = getApp();
      app.globalData.userInfo = userInfo;
      wx.setStorageSync('userInfo', userInfo);
      this.setData({ userInfo, editingProfile: false });
      wx.showToast({ title: '已保存' });
    } catch (err) {
      wx.showToast({ title: err.message || '保存失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
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
