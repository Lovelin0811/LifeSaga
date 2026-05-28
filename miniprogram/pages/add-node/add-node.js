// pages/add-node/add-node.js
const { api } = require('../../utils/api');

Page({
  data: {
    sagaId: '',
    nodeId: '',
    isEdit: false,
    title: '',
    date: '',
    time: '',
    location: '',
    latitude: null,
    longitude: null,
    content: '',
    photos: [],
    photoUrls: [],
    isMilestone: false,
    submitting: false,
  },

  onLoad(options) {
    this.setData({ sagaId: options.sagaId });
    if (options.nodeId) {
      this.setData({ nodeId: options.nodeId, isEdit: true });
      this.loadNode(options.nodeId);
    }
  },

  async loadNode(nodeId) {
    try {
      wx.showLoading({ title: '加载中' });
      const node = await api.getNode(this.data.sagaId, nodeId);
      const photos = typeof node.photos === 'string' ? JSON.parse(node.photos || '[]') : (node.photos || []);
      const [datePart, timePart] = (node.nodeTime || '').split('T');
      this.setData({
        title: node.title || '',
        content: node.content || '',
        location: node.location || '',
        latitude: node.latitude || null,
        longitude: node.longitude || null,
        date: datePart || '',
        time: timePart ? timePart.substring(0, 5) : '',
        isMilestone: !!node.milestone,
        photos,
      });
    } catch (err) {
      console.error('Load node failed:', err);
      wx.showToast({ title: '加载节点失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  onTitleInput(e) { this.setData({ title: e.detail.value }); },
  onLocationInput(e) { this.setData({ location: e.detail.value }); },
  onContentInput(e) { this.setData({ content: e.detail.value }); },
  onDateChange(e) { this.setData({ date: e.detail.value }); },
  onTimeChange(e) { this.setData({ time: e.detail.value }); },
  onMilestoneChange(e) { this.setData({ isMilestone: e.detail.value }); },

  chooseLocation() {
    wx.chooseLocation({
      success: (res) => {
        this.setData({
          location: res.name || res.address || '',
          latitude: res.latitude,
          longitude: res.longitude,
        });
      },
      fail: (err) => {
        if (err.errMsg.indexOf('cancel') === -1) {
          wx.showToast({ title: '选地址失败', icon: 'none' });
        }
      },
    });
  },

  choosePhoto() {
    const remaining = 9 - this.data.photos.length;
    wx.chooseMedia({
      count: remaining,
      mediaType: ['image'],
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const newPhotos = res.tempFiles.map(f => f.tempFilePath);
        this.setData({ photos: [...this.data.photos, ...newPhotos] });
      },
    });
  },

  removePhoto(e) {
    const index = e.currentTarget.dataset.index;
    const photos = [...this.data.photos];
    photos.splice(index, 1);
    this.setData({ photos });
  },

  previewPhoto(e) {
    const { urls, current } = e.currentTarget.dataset;
    wx.previewImage({ current, urls });
  },

  async save() {
    if (this.data.submitting) return;
    const { title, content, date, time, location, latitude, longitude, photos, isMilestone, sagaId } = this.data;

    if (!title && !content) {
      wx.showToast({ title: '请填写标题或内容', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });
    wx.showLoading({ title: '保存中' });

    try {
      // 上传照片
      let photoUrls = [];
      for (const photo of photos) {
        if (photo.startsWith('http')) {
          photoUrls.push(photo);
        } else {
          const url = await api.uploadFile(photo);
          photoUrls.push(url);
        }
      }

      // nodeTime 必须用 ISO 格式 (T 分隔)，不能用空格
      const nodeTime = date ? (time ? `${date}T${time}:00` : `${date}T00:00:00`) : null;

      const payload = {
        title,
        content,
        location,
        latitude: latitude || null,
        longitude: longitude || null,
        nodeTime,
        photos: photoUrls.length > 0 ? JSON.stringify(photoUrls) : null,
        milestone: isMilestone,
      };

      if (this.data.isEdit) {
        await api.updateNode(this.data.sagaId, this.data.nodeId, payload);
      } else {
        await api.createNode(this.data.sagaId, payload);
      }

      wx.showToast({ title: '保存成功' });
      setTimeout(() => wx.navigateBack(), 1000);
    } catch (err) {
      console.error('Save node failed:', err);
      wx.showToast({ title: err.message || '保存失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
      wx.hideLoading();
    }
  },
});
