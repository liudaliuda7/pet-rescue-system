<template>
  <div>
    <div class="card">
      <div class="page-header">流浪动物</div>
      <div class="toolbar">
        <el-input v-model="q.name" placeholder="按名字搜索" style="width:200px;" clearable @keyup.enter.native="load(1)"/>
        <el-select v-model="q.typeId" placeholder="种类" clearable style="width:140px;" @change="load(1)">
          <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id"/>
        </el-select>
        <el-button type="primary" @click="load(1)">搜索</el-button>
      </div>
      <el-row :gutter="16" v-if="list.length">
        <el-col :span="6" v-for="a in list" :key="a.id">
          <el-card :body-style="{ padding: 0 }" shadow="hover" style="margin-bottom:16px;cursor:pointer;" @click.native="$router.push('/animal/' + a.id)">
            <img :src="a.image || placeholder" class="cover"/>
            <div style="padding:12px;">
              <div style="display:flex;justify-content:space-between;align-items:center;">
                <span style="font-weight:600;">{{ a.name }}</span>
                <el-tag size="mini" :type="a.status==='available'?'success':a.status==='adopted'?'info':'warning'">
                  {{ a.status==='available'?'待领养': a.status==='adopted'?'已领养':'治疗中' }}
                </el-tag>
              </div>
              <div style="color:#909399;font-size:12px;margin-top:6px;">{{ a.typeName }} · {{ a.gender }} · {{ a.age }}</div>
              <div style="color:#909399;font-size:12px;">📍 {{ a.stationName || '未指派' }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无可领养动物"/>
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="text-align:center;margin-top:20px;"/>
    </div>
  </div>
</template>

<script>
import { animalApi, animalTypeApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 12, name: '', typeId: undefined },
      list: [], total: 0, types: [],
      placeholder: 'https://via.placeholder.com/400x240/cccccc/666666?text=No+Image'
    }
  },
  mounted() {
    animalTypeApi.publicList().then(r => { this.types = r.data || [] }).catch(() => {})
    this.load()
  },
  methods: {
    load(p) {
      if (p) this.q.current = p
      animalApi.publicPage(this.q).then(r => {
        this.list = r.data.records || []
        this.total = r.data.total || 0
      })
    }
  }
}
</script>

<style scoped>
.cover { width: 100%; height: 180px; object-fit: cover; display: block; }
</style>
