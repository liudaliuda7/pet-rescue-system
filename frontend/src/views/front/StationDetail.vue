<template>
  <div class="card" v-loading="loading">
    <div v-if="notFound" style="text-align:center;padding:60px 0;">
      <el-result icon="warning" title="救助站不存在" sub-title="您访问的救助站不存在或已被删除">
        <template slot="extra">
          <el-button type="primary" @click="$router.back()">返回上一页</el-button>
        </template>
      </el-result>
    </div>
    <div v-else-if="station">
      <el-row :gutter="30">
        <el-col :span="8">
          <img :src="stationImage" style="width:100%;border-radius:6px;"/>
        </el-col>
        <el-col :span="16">
          <h2 style="margin:0 0 10px;">{{ station.name }}</h2>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="地址">{{ station.address || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ station.contact || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ station.phone || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="简介">{{ station.description || '暂无简介' }}</el-descriptions-item>
          </el-descriptions>
          <div style="margin-top:16px;">
            <el-button @click="$router.back()">返回</el-button>
          </div>
        </el-col>
      </el-row>

      <div style="margin-top:30px;padding-top:20px;border-top:1px solid #ebeef5;">
        <h3 style="font-size:18px;font-weight:600;margin-bottom:20px;color:#303133;">正在救助的动物</h3>
        <el-row :gutter="16" v-if="animals.length">
          <el-col :span="6" v-for="a in animals" :key="a.id">
            <el-card :body-style="{ padding: 0 }" shadow="hover" style="margin-bottom:16px;cursor:pointer;" @click.native="$router.push('/animal/' + a.id)">
              <img :src="a.image || animalPlaceholder" class="cover"/>
              <div style="padding:12px;">
                <div style="display:flex;justify-content:space-between;align-items:center;">
                  <span style="font-weight:600;">{{ a.name }}</span>
                  <el-tag size="mini" :type="statusType(a.status)">{{ statusLabel(a.status) }}</el-tag>
                </div>
                <div style="color:#909399;font-size:12px;margin-top:6px;">{{ a.typeName }} · {{ a.gender }} · {{ a.age }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-empty v-else description="该救助站暂无可领养动物"/>
        <el-pagination v-if="total > q.size" background layout="prev, pager, next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="loadAnimals()" style="text-align:center;margin-top:20px;"/>
      </div>
    </div>
  </div>
</template>

<script>
import { stationApi, animalApi } from '@/api'
export default {
  data() {
    return {
      station: null, loading: true, notFound: false, animals: [], total: 0,
      q: { current: 1, size: 12 },
      placeholder: 'https://via.placeholder.com/400x300/cccccc/666666?text=No+Image',
      animalPlaceholder: 'https://via.placeholder.com/400x240/cccccc/666666?text=No+Image'
    }
  },
  computed: {
    stationImage() {
      return (this.station && this.station.image && this.station.image.trim()) ? this.station.image : this.placeholder
    }
  },
  mounted() {
    const id = this.$route.params.id
    stationApi.publicGet(id).then(r => {
      if (!r.data) {
        this.notFound = true
      } else {
        this.station = r.data
        this.loadAnimals()
      }
    }).catch(() => {
      this.notFound = true
    }).finally(() => { this.loading = false })
  },
  methods: {
    statusType(s) { return { available: 'success', adopted: 'info', treatment: 'warning' }[s] || '' },
    statusLabel(s) { return { available: '待领养', adopted: '已领养', treatment: '治疗中' }[s] || s || '未知' },
    loadAnimals() {
      animalApi.publicPage({ ...this.q, stationId: this.$route.params.id }).then(r => {
        this.animals = r.data.records || []
        this.total = r.data.total || 0
      })
    }
  }
}
</script>

<style scoped>
.cover { width: 100%; height: 180px; object-fit: cover; display: block; }
</style>
