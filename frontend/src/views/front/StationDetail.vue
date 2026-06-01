<template>
  <div class="card" v-loading="loading">
    <div v-if="station">
      <div class="station-info">
        <div class="station-header">
          <img :src="station.image || placeholder" class="station-img"/>
          <div class="station-meta">
            <h2 style="margin:0 0 12px;">{{ station.name }}</h2>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="地址">{{ station.address || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="联系人">{{ station.contact || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ station.phone || '暂无' }}</el-descriptions-item>
              <el-descriptions-item label="简介">{{ station.description || '暂无简介' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </div>

      <div class="animal-section">
        <h3 class="section-title">正在救助的动物</h3>
        <el-row :gutter="16" v-if="animals.length">
          <el-col :span="6" v-for="a in animals" :key="a.id">
            <el-card :body-style="{ padding: 0 }" shadow="hover" style="margin-bottom:16px;cursor:pointer;" @click.native="$router.push('/animal/' + a.id)">
              <img :src="a.image || animalPlaceholder" class="cover"/>
              <div style="padding:12px;">
                <div style="display:flex;justify-content:space-between;align-items:center;">
                  <span style="font-weight:600;">{{ a.name }}</span>
                  <el-tag size="mini" :type="a.status==='available'?'success':a.status==='adopted'?'info':'warning'">
                    {{ a.status==='available'?'待领养':a.status==='adopted'?'已领养':'治疗中' }}
                  </el-tag>
                </div>
                <div style="color:#909399;font-size:12px;margin-top:6px;">{{ a.typeName }} · {{ a.gender }} · {{ a.age }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-empty v-else description="暂无正在救助的动物"/>
        <el-pagination v-if="total > q.size" background layout="prev, pager, next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="loadAnimals()" style="text-align:center;margin-top:20px;"/>
      </div>
    </div>
    <el-empty v-if="!loading && !station" description="救助站不存在"/>
  </div>
</template>

<script>
import { stationApi, animalApi } from '@/api'
export default {
  data() {
    return {
      station: null, loading: true, animals: [], total: 0,
      q: { current: 1, size: 12 },
      placeholder: 'https://via.placeholder.com/200x200/cccccc/666666?text=Station',
      animalPlaceholder: 'https://via.placeholder.com/400x240/cccccc/666666?text=No+Image'
    }
  },
  mounted() {
    const id = this.$route.params.id
    stationApi.publicGet(id).then(r => {
      this.station = r.data
      if (this.station) this.loadAnimals()
    }).finally(() => { this.loading = false })
  },
  methods: {
    loadAnimals() {
      animalApi.publicByStation({ stationId: this.$route.params.id, current: this.q.current, size: this.q.size }).then(r => {
        this.animals = r.data.records || []
        this.total = r.data.total || 0
      })
    }
  }
}
</script>

<style scoped>
.station-header {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.station-img {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}
.station-meta {
  flex: 1;
}
.animal-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
}
.cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
  display: block;
}
</style>
