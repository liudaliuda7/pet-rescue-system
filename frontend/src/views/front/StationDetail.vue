<template>
  <div v-loading="loading">
    <div class="card" v-if="station">
      <div class="page-header">救助站详情</div>
      <el-row :gutter="30">
        <el-col :span="8">
          <img :src="station.image || placeholder" style="width:100%;border-radius:6px;"/>
        </el-col>
        <el-col :span="16">
          <h2 style="margin:0 0 16px;">{{ station.name }}</h2>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="地址">{{ station.address || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ station.contact || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ station.phone || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="简介">{{ station.description || '暂无介绍' }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
    </div>

    <div class="card" style="margin-top:20px;" v-if="station">
      <div class="page-header">正在救助的动物</div>
      <el-row :gutter="16" v-if="animals.length">
        <el-col :span="6" v-for="a in animals" :key="a.id">
          <el-card :body-style="{ padding: 0 }" shadow="hover" style="margin-bottom:16px;cursor:pointer;" @click.native="$router.push('/animal/' + a.id)">
            <img :src="a.image || animalPlaceholder" class="cover"/>
            <div style="padding:12px;">
              <div style="display:flex;justify-content:space-between;align-items:center;">
                <span style="font-weight:600;">{{ a.name }}</span>
                <el-tag size="mini" type="success">待领养</el-tag>
              </div>
              <div style="color:#909399;font-size:12px;margin-top:6px;">{{ a.typeName }} · {{ a.gender }} · {{ a.age }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="该救助站暂无可领养动物"/>
      <el-pagination v-if="total > q.size" background layout="prev, pager, next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="loadAnimals()" style="text-align:center;margin-top:20px;"/>
    </div>

    <div style="margin-top:20px;">
      <el-button @click="$router.back()">返回</el-button>
    </div>
  </div>
</template>

<script>
import { stationApi, animalApi } from '@/api'
export default {
  data() {
    return {
      station: null,
      loading: true,
      animals: [],
      total: 0,
      q: { current: 1, size: 12 },
      placeholder: 'https://via.placeholder.com/600x400/cccccc/666666?text=No+Image',
      animalPlaceholder: 'https://via.placeholder.com/400x240/cccccc/666666?text=No+Image'
    }
  },
  mounted() {
    const id = this.$route.params.id
    stationApi.publicGet(id).then(r => { this.station = r.data }).finally(() => { this.loading = false })
    this.loadAnimals()
  },
  methods: {
    loadAnimals(p) {
      if (p) this.q.current = p
      const id = this.$route.params.id
      animalApi.publicPage({ current: this.q.current, size: this.q.size, stationId: id }).then(r => {
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
