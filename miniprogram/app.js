// app.js
const { request, login } = require('./utils/api');

App({
  globalData: {
    userInfo: null,
    token: '',
  },

  onLaunch() {
    this.restoreSession();
  },

  // 恢复登录态
  restoreSession() {
    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      this.globalData.userInfo = wx.getStorageSync('userInfo') || null;
    }
  },

  // 登录
  async doLogin() {
    try {
      const user = await login();
      this.globalData.token = user.token;
      this.globalData.userInfo = user;
      wx.setStorageSync('token', user.token);
      wx.setStorageSync('userInfo', user);
      return user;
    } catch (err) {
      console.error('Login failed:', err);
      throw err;
    }
  },

  // 检查登录态，未登录则自动登录
  async ensureLogin() {
    if (this.globalData.token) return this.globalData.userInfo;
    return await this.doLogin();
  },

  // 退出登录
  logout() {
    this.globalData.token = '';
    this.globalData.userInfo = null;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  },
});
