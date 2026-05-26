<template>
  <div class="card">
    <div class="page-header">消息管理</div>
    <div class="toolbar">
      <el-select v-model="q.type" placeholder="消息类型" clearable style="width:150px;">
        <el-option label="领养通过" value="adoption_approved"/>
        <el-option label="领养拒绝" value="adoption_rejected"/>
        <el-option label="求助指派" value="help_assigned"/>
        <el-option label="新求助任务" value="help_new"/>
      </el-select>
      <el-select v-model="q.isRead" placeholder="阅读状态" clearable style="width:120px;margin-left:10px;">
        <el-option label="未读" :value="0"/>
        <el-option label="已读" :value="1"/>
      </el-select>
      <el-button type="primary" @click="load(1)" style="margin-left:10px;">搜索</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="userId" label="用户ID" width="80"/>
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="content" label="内容" show-overflow-tooltip/>
      <el-table-column label="类型" width="120">
        <template slot-scope="s"><el-tag size="mini">{{ typeText(s.row.type) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="s">
          <el-tag size="mini" :type="s.row.isRead ? 'info' : 'warning'">{{ s.row.isRead ? '已读' : '未读' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发送时间" width="170"/>
    </el-table>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="margin-top:16px;text-align:right;"/>
  </div>
</template>

<script>
import { messageApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 10, type: '', isRead: null },
      list: [], total: 0
    }
  },
  mounted() { this.load() },
  methods: {
    load(p) {
      if (p) this.q.current = p
      messageApi.adminPage(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 })
    },
    typeText(t) {
      return { adoption_approved: '领养通过', adoption_rejected: '领养拒绝', help_assigned: '求助指派', help_new: '新求助任务' }[t] || t
    }
  }
}
</script>
