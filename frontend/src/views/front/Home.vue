<template>
  <div>
    <div class="banner">
      <h1>给每一个生命一个家</h1>
      <p>为流浪动物提供专业救助、健康照护与领养服务</p>
      <div>
        <el-button type="primary" @click="$router.push('/animal')">查看可领养动物</el-button>
        <el-button @click="$router.push('/help/submit')">我要求助</el-button>
      </div>
    </div>

    <el-row :gutter="20" style="margin-top:30px;">
      <el-col :span="16">
        <div class="card">
          <div class="page-header">
            🐾 最新可领养动物
            <el-link type="primary" style="float:right;" @click="$router.push('/animal')">查看全部 →</el-link>
          </div>
          <el-row :gutter="16" v-if="animals.length">
            <el-col :span="8" v-for="a in animals" :key="a.id">
              <el-card :body-style="{ padding: 0 }" shadow="hover" style="margin-bottom:16px;cursor:pointer;" @click.native="$router.push('/animal/' + a.id)">
                <img :src="a.image || placeholder" class="cover" />
                <div style="padding:12px;">
                  <div style="font-weight:600;">{{ a.name }}</div>
                  <div style="color:#909399;font-size:12px;">{{ a.typeName }} · {{ a.gender }} · {{ a.age }}</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-else description="暂无数据"/>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card">
          <div class="page-header">📢 最新公告</div>
          <ul class="notice-list">
            <li v-for="n in notices" :key="n.id" @click="$router.push('/notice/' + n.id)">
              <span class="dot">·</span>{{ n.title }}
            </li>
            <li v-if="!notices.length" style="color:#909399;">暂无公告</li>
          </ul>
        </div>
        <div class="card" style="margin-top:16px;">
          <div class="page-header">📊 数据概览</div>
          <el-row :gutter="10">
            <el-col :span="12"><div class="mini-stat blue"><div class="num">{{ stats.available || 0 }}</div><div class="lbl">待领养</div></div></el-col>
            <el-col :span="12"><div class="mini-stat green"><div class="num">{{ stats.adopted || 0 }}</div><div class="lbl">已领养</div></div></el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { animalApi, noticeApi } from '@/api'
export default {
  data() {
    return {
      animals: [], notices: [], stats: {},
      placeholder: 'https://via.placeholder.com/400x240/cccccc/666666?text=No+Image'
    }
  },
  mounted() {
    animalApi.publicPage({ current: 1, size: 6 }).then(r => { this.animals = r.data.records || [] }).catch(() => {})
    noticeApi.latest().then(r => { this.notices = r.data || [] }).catch(() => {})
    animalApi.stats().then(r => { this.stats = r.data || {} }).catch(() => {})
  }
}
</script>

<style scoped lang="scss">
.banner { background: linear-gradient(135deg,#667eea,#764ba2); color:#fff; padding: 60px 40px; border-radius: 8px; text-align:center;
  h1 { font-size: 36px; margin: 0 0 12px; }
  p { font-size: 16px; opacity: 0.9; margin: 0 0 20px; }
}
.cover { width: 100%; height: 160px; object-fit: cover; display: block; }
.notice-list { list-style: none; padding: 0; margin: 0; li { padding: 8px 0; border-bottom: 1px dashed #ebeef5; cursor: pointer; &:hover { color: #409EFF; } .dot { color: #409EFF; margin-right: 6px; font-weight: bold; } } li:last-child { border-bottom: 0; } }
.mini-stat { padding: 16px; border-radius: 6px; color: #fff; text-align: center; .num { font-size: 22px; font-weight: 700; } .lbl { font-size: 12px; opacity: 0.9; } }
.mini-stat.blue { background: #409EFF; }
.mini-stat.green { background: #67C23A; }
</style>
