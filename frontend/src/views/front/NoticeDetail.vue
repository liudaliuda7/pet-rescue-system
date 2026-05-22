<template>
  <div class="card" v-loading="loading">
    <div v-if="n">
      <h2 style="margin:0 0 6px;">{{ n.title }}</h2>
      <div style="color:#909399;font-size:12px;margin-bottom:20px;">发布时间：{{ n.createTime }}</div>
      <div style="white-space:pre-wrap;line-height:1.8;">{{ n.content }}</div>
      <el-button style="margin-top:30px;" @click="$router.back()">返回</el-button>
    </div>
  </div>
</template>

<script>
import { noticeApi } from '@/api'
export default {
  data() { return { n: null, loading: true } },
  mounted() { noticeApi.publicGet(this.$route.params.id).then(r => { this.n = r.data }).finally(() => { this.loading = false }) }
}
</script>
