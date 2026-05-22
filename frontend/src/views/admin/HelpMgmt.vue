<template>
  <div class="card">
    <div class="page-header">求助信息管理</div>
    <div class="toolbar">
      <el-input v-model="q.title" placeholder="标题" style="width:200px;" clearable @keyup.enter.native="load(1)"/>
      <el-select v-model="q.status" clearable placeholder="状态" style="width:140px;" @change="load(1)">
        <el-option label="待处理" value="pending"/><el-option label="处理中" value="processing"/><el-option label="已完成" value="done"/><el-option label="已关闭" value="closed"/>
      </el-select>
      <el-button type="primary" @click="load(1)">搜索</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="userName" label="发布人" width="100"/>
      <el-table-column prop="location" label="地点"/>
      <el-table-column prop="contact" label="联系" width="120"/>
      <el-table-column prop="stationName" label="处理站" width="140"/>
      <el-table-column label="状态" width="90">
        <template slot-scope="s"><el-tag size="mini" :type="tagType(s.row.status)">{{ statusText(s.row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170"/>
      <el-table-column label="操作" width="240">
        <template slot-scope="s">
          <el-button size="mini" @click="view(s.row)">详情</el-button>
          <el-button size="mini" type="primary" @click="openAssign(s.row)" v-if="role==='admin'">指派</el-button>
          <el-button size="mini" type="success" @click="changeStatus(s.row, 'done')" v-if="s.row.status!=='done'">标记完成</el-button>
          <el-button size="mini" type="danger" @click="del(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="margin-top:16px;text-align:right;"/>

    <el-dialog title="求助详情" :visible.sync="vshow" width="600px">
      <el-descriptions :column="1" border v-if="cur">
        <el-descriptions-item label="标题">{{ cur.title }}</el-descriptions-item>
        <el-descriptions-item label="发布人">{{ cur.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ cur.contact }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ cur.location }}</el-descriptions-item>
        <el-descriptions-item label="处理救助站">{{ cur.stationName || '未指派' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(cur.status) }}</el-descriptions-item>
        <el-descriptions-item label="详细描述">{{ cur.content }}</el-descriptions-item>
        <el-descriptions-item label="图片"><img v-if="cur.image" :src="cur.image" style="max-width:200px;"/></el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog title="指派救助站" :visible.sync="ashow" width="400px">
      <el-form label-width="80px">
        <el-form-item label="救助站">
          <el-select v-model="assignForm.stationId" style="width:100%;">
            <el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.id"/>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="ashow=false">取消</el-button><el-button type="primary" @click="assign">确定</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { helpApi, stationApi } from '@/api'
import { getUser } from '@/utils/auth'
export default {
  data() {
    return {
      q: { current: 1, size: 10, title: '', status: '' },
      list: [], total: 0, stations: [], role: getUser().role,
      vshow: false, ashow: false, cur: null,
      assignForm: { id: null, stationId: null }
    }
  },
  mounted() { stationApi.list().then(r => { this.stations = r.data || [] }); this.load() },
  methods: {
    statusText(s) { return { pending: '待处理', processing: '处理中', done: '已完成', closed: '已关闭' }[s] || s },
    tagType(s) { return { pending: 'warning', processing: 'primary', done: 'success', closed: 'info' }[s] || '' },
    load(p) { if (p) this.q.current = p; helpApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 }) },
    view(row) { this.cur = row; this.vshow = true },
    openAssign(row) { this.assignForm = { id: row.id, stationId: row.stationId }; this.ashow = true },
    assign() { helpApi.assign(this.assignForm).then(() => { this.$message.success('已指派'); this.ashow = false; this.load() }) },
    changeStatus(row, status) {
      helpApi.update({ id: row.id, status }).then(() => { this.$message.success('已更新'); this.load() })
    },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => helpApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })).catch(()=>{})
    }
  }
}
</script>
