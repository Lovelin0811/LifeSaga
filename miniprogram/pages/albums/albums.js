const { api } = require('../../utils/api');
const { formatDate } = require('../../utils/util');

Page({
  data: {
    loading: true,
    photos: [],
    photoUrls: [],
  },

  onLoad() {
    this.loadAlbums();
  },

  onPullDownRefresh() {
    this.loadAlbums().finally(() => wx.stopPullDownRefresh());
  },

  async loadAlbums() {
    this.setData({ loading: true });
    try {
      await getApp().ensureLogin();
      const photos = (await api.getAlbums()).map((item) => ({
        url: item.url,
        title: item.title || '未命名节点',
        sagaName: item.sagaName,
        timeText: item.nodeTime ? formatDate(item.nodeTime, 'YYYY.MM.DD') : '',
      })).filter(item => item.url);
      this.setData({ photos, photoUrls: photos.map(item => item.url), loading: false });
    } catch (err) {
      console.error('Load albums failed:', err);
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  previewPhoto(e) {
    const { current, urls } = e.currentTarget.dataset;
    wx.previewImage({ current, urls });
  },
});
