<template>
  <div class="card">
    <div class="page-header">公告信息</div>
    <ul class="list">
      <li v-for="n in list" :key="n.id" @click="$router.push('/notice/' + n.id)">
        <div class="t">{{ n.title }}</div>
        <div class="d">{{ n.createTime }}</div>
      </li>
      <li v-if="!list.length" style="text-align:center;color:#909399;padding:40px;">暂无公告</li>
    </ul>
    <el-pagination background layout="prev, pager, next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="text-align:center;margin-top:20px;"/>
  </div>
</template>

<script>
import { noticeApi } from '@/api'
export default {
  data() { return { q: { current: 1, size: 10 }, list: [], total: 0 } },
  mounted() { this.load() },
  methods: {
    load() {
      noticeApi.publicPage(this.q).then(r => {
        this.list = r.data.records || []
        this.total = r.data.total || 0
      })
    }
  }
}
</script>

<style scoped lang="scss">
.list { list-style: none; padding: 0; margin: 0;
  li { padding: 16px; border-bottom: 1px solid #ebeef5; cursor: pointer; display:flex; justify-content:space-between;
    &:hover { background: #f5f7fa; }
    .t { font-size: 15px; }
    .d { color: #909399; font-size: 12px; }
  }
}
</style>
