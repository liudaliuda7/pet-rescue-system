<template>
  <el-popover placement="bottom" width="320" trigger="click" @show="loadMessages">
    <div class="notify-panel">
      <div class="notify-header">
        <span>消息通知</span>
        <el-button type="text" size="mini" @click="markAllRead" :disabled="!unread">全部已读</el-button>
      </div>
      <div class="notify-list" v-if="messages.length">
        <div v-for="m in messages" :key="m.id" class="notify-item" :class="{ unread: !m.isRead }" @click="readMsg(m)">
          <div class="notify-title">{{ m.title }}</div>
          <div class="notify-content">{{ m.content }}</div>
          <div class="notify-time">{{ m.createTime }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无消息" :image-size="60"/>
      <div class="notify-footer" v-if="messages.length">
        <el-button type="text" @click="goAll">查看全部</el-button>
      </div>
    </div>
    <el-badge :value="unread" :hidden="!unread" :max="99" slot="reference" class="notify-badge">
      <i class="el-icon-bell" style="font-size:20px;cursor:pointer;"></i>
    </el-badge>
  </el-popover>
</template>

<script>
import { messageApi } from '@/api'
export default {
  props: {
    allPath: { type: String, default: '/profile' }
  },
  data() { return { unread: 0, messages: [], timer: null } },
  mounted() {
    this.loadUnread()
    this.timer = setInterval(this.loadUnread, 30000)
  },
  beforeDestroy() { if (this.timer) clearInterval(this.timer) },
  methods: {
    loadUnread() {
      messageApi.unreadCount().then(r => { this.unread = r.data || 0 }).catch(() => {})
    },
    loadMessages() {
      messageApi.page({ current: 1, size: 5 }).then(r => { this.messages = r.data.records || [] })
    },
    readMsg(m) {
      if (!m.isRead) {
        messageApi.read(m.id).then(() => { m.isRead = 1; this.unread = Math.max(0, this.unread - 1) })
      }
    },
    markAllRead() {
      messageApi.readAll().then(() => { this.unread = 0; this.messages.forEach(m => m.isRead = 1) })
    },
    goAll() { this.$router.push(this.allPath) }
  }
}
</script>

<style scoped>
.notify-panel { max-height: 400px; }
.notify-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 8px; border-bottom: 1px solid #ebeef5; margin-bottom: 8px; }
.notify-list { max-height: 300px; overflow-y: auto; }
.notify-item { padding: 10px 0; border-bottom: 1px solid #f0f0f0; cursor: pointer; }
.notify-item.unread { background: #f0f9ff; margin: 0 -12px; padding: 10px 12px; }
.notify-item:hover { background: #ecf5ff; }
.notify-title { font-size: 14px; font-weight: 500; }
.notify-content { font-size: 12px; color: #909399; margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.notify-time { font-size: 11px; color: #c0c4cc; margin-top: 4px; }
.notify-footer { text-align: center; padding-top: 8px; border-top: 1px solid #ebeef5; }
.notify-badge { line-height: normal; }
</style>
