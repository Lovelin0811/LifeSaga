// pages/node-detail/node-detail.js
const { api } = require('../../utils/api');
const { formatDate } = require('../../utils/util');

Page({
  data: {
    loading: true,
    node: null,
    sagaId: '',
    nodeId: '',
    statusBarHeight: 44,
  },

  onLoad(options) {
    // 获取状态栏高度用于自定义导航
    const systemInfo = wx.getSystemInfoSync();
    this.setData({ statusBarHeight: systemInfo.statusBarHeight || 44 });

    this.sagaId = options.sagaId;
    this.nodeId = options.id;
    this.loadNode();
  },

  async loadNode() {
    this.setData({ loading: true });
    try {
      const node = await api.getNode(this.sagaId, this.nodeId);
      this.setData({
        node: {
          ...node,
          nodeTimeText: node.nodeTime ? formatDate(node.nodeTime, 'YYYY年MM月DD日 HH:mm') : '',
          photos: typeof node.photos === 'string' ? JSON.parse(node.photos || '[]') : (node.photos || []),
        },
        loading: false,
      });
    } catch (err) {
      console.error('Load node failed:', err);
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  goBack() {
    const pages = getCurrentPages();
    if (pages.length > 1) {
      wx.navigateBack();
    } else {
      wx.switchTab({ url: '/pages/home/home' });
    }
  },

  showMenu() {
    wx.showActionSheet({
      itemList: ['编辑节点', '删除节点'],
      success: (res) => {
        if (res.tapIndex === 0) {
          this.editNode();
        } else if (res.tapIndex === 1) {
          this.deleteNode();
        }
      },
    });
  },

  previewPhoto(e) {
    const { urls, current } = e.currentTarget.dataset;
    wx.previewImage({ current, urls });
  },

  editNode() {
    wx.navigateTo({
      url: `/pages/add-node/add-node?sagaId=${this.sagaId}&nodeId=${this.nodeId}`,
    });
  },

  deleteNode() {
    wx.showModal({
      title: '确认删除',
      content: '删除后无法恢复，确定要删除这个节点吗？',
      confirmColor: '#E05A5A',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.deleteNode(this.sagaId, this.nodeId);
            wx.showToast({ title: '已删除' });
            setTimeout(() => wx.navigateBack(), 1000);
          } catch (err) {
            wx.showToast({ title: '删除失败', icon: 'none' });
          }
        }
      },
    });
  },

  toggleFavorite() {
    if (!this.data.node) return;
    wx.showLoading({ title: '处理中' });
    api.toggleNodeFavorite(this.sagaId, this.nodeId)
      .then((data) => {
        this.setData({ 'node.favorited': !!data.favorited });
        wx.showToast({ title: data.favorited ? '已收藏' : '已取消', icon: 'none' });
      })
      .catch((err) => {
        wx.showToast({ title: err.message || '操作失败', icon: 'none' });
      })
      .finally(() => wx.hideLoading());
  },

  toggleMilestone() {
    if (!this.data.node) return;
    wx.showLoading({ title: '处理中' });
    api.toggleNodeMilestone(this.sagaId, this.nodeId)
      .then((node) => {
        this.setData({ node: { ...node, photos: this.data.node.photos } });
        wx.showToast({ title: node.milestone ? '已设为里程碑' : '已取消里程碑', icon: 'none' });
      })
      .catch((err) => {
        wx.showToast({ title: err.message || '操作失败', icon: 'none' });
      })
      .finally(() => wx.hideLoading());
  },

  shareNode() {
    wx.showShareMenu({ withShareTicket: true });
    wx.showToast({ title: '点击右上角分享', icon: 'none' });
  },

  onShareAppMessage() {
    const node = this.data.node;
    return {
      title: node?.title || '节点分享',
      path: `/pages/node-detail/node-detail?id=${this.nodeId}&sagaId=${this.sagaId}`,
    };
  },
});
