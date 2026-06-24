const { api } = require('../../utils/api');
const { SAGA_TYPES, RARITY_MAP, formatDate, enumKey, classKey } = require('../../utils/util');

const COVER_BGS = {
  LIFE: 'linear-gradient(135deg, #FFF0EB 0%, #FFE5DD 100%)',
  TRAVEL: 'linear-gradient(135deg, #EBF3FA 0%, #D6E9F8 100%)',
  STUDY: 'linear-gradient(135deg, #EDF7EF 0%, #D6F0DC 100%)',
  WORK: 'linear-gradient(135deg, #FFF6E8 0%, #FFEDD0 100%)',
  HEALTH: 'linear-gradient(135deg, #EDF8F1 0%, #D6F0E4 100%)',
  CREATIVE: 'linear-gradient(135deg, #F5EDF8 0%, #E8D8F0 100%)',
};

Page({
  data: {
    loading: true,
    sagas: [],
  },

  onLoad() {},

  onShow() {
    this.loadPublicSagas();
  },

  onPullDownRefresh() {
    this.loadPublicSagas().finally(() => wx.stopPullDownRefresh());
  },

  async loadPublicSagas() {
    this.setData({ loading: true });
    try {
      await getApp().ensureLogin();
      const sagas = await api.getPublicSagas();
      const list = sagas.map(s => {
        const typeKey = enumKey(s.type, 'LIFE');
        const rarityKey = enumKey(s.rarity, 'COMMON');
        const type = SAGA_TYPES[typeKey] || SAGA_TYPES.LIFE;
        const rarity = RARITY_MAP[rarityKey] || RARITY_MAP.COMMON;
        return {
          ...s,
          type: typeKey,
          typeClass: classKey(typeKey),
          typeName: type.name,
          typeIcon: type.icon,
          coverBg: COVER_BGS[typeKey] || COVER_BGS.LIFE,
          rarityName: rarity.name,
          rarity: rarityKey,
          rarityClass: classKey(rarityKey),
          startedAtText: s.startedAt ? formatDate(s.startedAt, 'YYYY.MM.DD') : '',
        };
      });
      this.setData({ sagas: list, loading: false });
    } catch (err) {
      console.error('Load public sagas failed:', err);
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` });
  },
});
