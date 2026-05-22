<template>
  <div>
    <div class="page-header">数据概览</div>
    <el-row :gutter="20">
      <el-col :span="6"><div class="stat-card" style="background:#409EFF;"><i class="el-icon-user" style="font-size:36px;margin-right:16px;"/><div><div class="num">{{ s.userTotal || 0 }}</div><div class="label">用户总数</div></div></div></el-col>
      <el-col :span="6"><div class="stat-card" style="background:#67C23A;"><i class="el-icon-office-building" style="font-size:36px;margin-right:16px;"/><div><div class="num">{{ s.stationTotal || 0 }}</div><div class="label">救助站</div></div></div></el-col>
      <el-col :span="6"><div class="stat-card" style="background:#E6A23C;"><i class="el-icon-orange" style="font-size:36px;margin-right:16px;"/><div><div class="num">{{ s.animalTotal || 0 }}</div><div class="label">流浪动物</div></div></div></el-col>
      <el-col :span="6"><div class="stat-card" style="background:#F56C6C;"><i class="el-icon-warning-outline" style="font-size:36px;margin-right:16px;"/><div><div class="num">{{ s.helpTotal || 0 }}</div><div class="label">求助信息</div></div></div></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px;">
      <el-col :span="8"><div class="card"><h3 style="margin:0 0 10px;">动物状态</h3>
        <p>待领养：<b style="color:#67C23A;">{{ s.animalAvailable || 0 }}</b></p>
        <p>已领养：<b style="color:#909399;">{{ s.animalAdopted || 0 }}</b></p>
      </div></el-col>
      <el-col :span="8"><div class="card"><h3 style="margin:0 0 10px;">求助处理</h3>
        <p>待处理：<b style="color:#E6A23C;">{{ s.helpPending || 0 }}</b></p>
        <p>累计领养：<b>{{ s.adoptionTotal || 0 }}</b></p>
      </div></el-col>
      <el-col :span="8"><div class="card"><h3 style="margin:0 0 10px;">系统提示</h3>
        <p style="color:#909399;font-size:13px;line-height:1.8;">欢迎使用流浪动物救助管理系统。请通过左侧菜单管理用户、救助站、动物及求助信息。</p>
      </div></el-col>
    </el-row>
  </div>
</template>

<script>
import { statsApi } from '@/api'
export default {
  data() { return { s: {} } },
  mounted() { statsApi.dashboard().then(r => { this.s = r.data || {} }) }
}
</script>
