<template>
  <div class="card">
    <div class="page-header">求助信息
      <el-button type="primary" size="small" style="float:right;" @click="$router.push('/help/submit')">我要求助</el-button>
    </div>
    <div class="toolbar">
      <el-input v-model="q.title" placeholder="按标题搜索" style="width:240px;" clearable @keyup.enter.native="load(1)"/>
      <el-button type="primary" @click="load(1)">搜索</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column type="index" width="50"/>
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="location" label="地点"/>
      <el-table-column prop="userName" label="发布人" width="120"/>
      <el-table-column prop="stationName" label="救助站" width="160"/>
      <el-table-column label="状态" width="100">
        <template slot-scope="s">
          <el-tag size="mini" :type="tagType(s.row.status)">{{ statusText(s.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180"/>
    </el-table>
    <el-pagination background layout="prev, pager, next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="text-align:center;margin-top:20px;"/>
  </div>
</template>

<script>
import { helpApi } from '@/api'
export default {
  data() { return { q: { current: 1, size: 10, title: '' }, list: [], total: 0 } },
  mounted() { this.load() },
  methods: {
    statusText(s) { return { pending:'待处理', processing:'处理中', done:'已完成', closed:'已关闭' }[s] || s },
    tagType(s) { return { pending:'warning', processing:'primary', done:'success', closed:'info' }[s] || '' },
    load(p) {
      if (p) this.q.current = p
      helpApi.publicPage(this.q).then(r => {
        this.list = r.data.records || []
        this.total = r.data.total || 0
      })
    }
  }
}
</script>
