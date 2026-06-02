<template>
  <div class="card" v-loading="loading">
    <div v-if="station">
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-value">{{ stats.animalTotal || 0 }}</div>
          <div class="stat-label">累计救助动物</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.animalAvailable || 0 }}</div>
          <div class="stat-label">当前待领养</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.animalAdopted || 0 }}</div>
          <div class="stat-label">累计领养成功</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.helpTotal || 0 }}</div>
          <div class="stat-label">累计处理求助</div>
        </div>
      </div>

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

      <div class="activity-section">
        <h3 class="section-title">最新动态</h3>
        <div v-if="activities.length" class="activity-timeline">
          <div v-for="(act, idx) in activities" :key="idx" class="activity-item" @click="goActivity(act)">
            <div class="activity-dot" :class="'dot-' + act.type"></div>
            <div class="activity-content">
              <div class="activity-title">{{ act.title }}</div>
              <div class="activity-time">{{ act.time | fmtTime }}</div>
            </div>
            <el-tag size="mini" :type="actTagType(act.type)">{{ actLabel(act.type) }}</el-tag>
          </div>
        </div>
        <el-empty v-else description="暂无最近动态"/>
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
import { stationApi, animalApi, statsApi } from '@/api'
export default {
  data() {
    return {
      station: null, loading: true, animals: [], total: 0,
      stats: {},
      activities: [],
      q: { current: 1, size: 12 },
      placeholder: 'https://via.placeholder.com/200x200/cccccc/666666?text=Station',
      animalPlaceholder: 'https://via.placeholder.com/400x240/cccccc/666666?text=No+Image'
    }
  },
  filters: {
    fmtTime(v) {
      if (!v) return ''
      return v.replace('T', ' ').substring(0, 16)
    }
  },
  mounted() {
    const id = this.$route.params.id
    stationApi.publicGet(id).then(r => {
      this.station = r.data
      if (this.station) {
        this.loadAnimals()
        this.loadStats()
        this.loadActivities()
      }
    }).finally(() => { this.loading = false })
  },
  methods: {
    loadAnimals() {
      animalApi.publicByStation({ stationId: this.$route.params.id, current: this.q.current, size: this.q.size }).then(r => {
        this.animals = r.data.records || []
        this.total = r.data.total || 0
      })
    },
    loadStats() {
      statsApi.station(this.$route.params.id).then(r => {
        this.stats = r.data || {}
      })
    },
    loadActivities() {
      stationApi.activities(this.$route.params.id).then(r => {
        this.activities = r.data || []
      })
    },
    actTagType(type) {
      return { new_animal: 'success', adoption: '', help_request: 'warning', health_record: 'info' }[type] || ''
    },
    actLabel(type) {
      return { new_animal: '新动物', adoption: '领养', help_request: '求助', health_record: '健康' }[type] || ''
    },
    goActivity(act) {
      if (act.type === 'new_animal' || act.type === 'health_record') {
        this.$router.push('/animal/' + act.relatedId)
      } else if (act.type === 'help_request') {
        this.$router.push('/help')
      }
    }
  }
}
</script>

<style scoped>
.stats-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
.stat-card {
  flex: 1;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e9f2 100%);
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #409eff;
}
.stat-label {
  font-size: 13px;
  color: #606266;
  margin-top: 6px;
}
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
.activity-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
.activity-timeline {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}
.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px dashed #ebeef5;
  cursor: pointer;
  transition: background .2s;
  border-radius: 6px;
}
.activity-item:hover {
  background: #f5f7fa;
}
.activity-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 12px;
  flex-shrink: 0;
}
.dot-new_animal { background: #67c23a; }
.dot-adoption { background: #409eff; }
.dot-help_request { background: #e6a23c; }
.dot-health_record { background: #909399; }
.activity-content {
  flex: 1;
}
.activity-title {
  font-size: 14px;
  color: #303133;
}
.activity-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
