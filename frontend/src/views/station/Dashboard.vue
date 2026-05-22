<template>
  <div>
    <div class="page-header">救助站工作台</div>
    <el-row :gutter="20">
      <el-col :span="6"><div class="stat-card" style="background:#409EFF;"><i class="el-icon-orange" style="font-size:36px;margin-right:16px;"/><div><div class="num">{{ s.animalTotal || 0 }}</div><div class="label">本站动物</div></div></div></el-col>
      <el-col :span="6"><div class="stat-card" style="background:#67C23A;"><i class="el-icon-thumb" style="font-size:36px;margin-right:16px;"/><div><div class="num">{{ s.animalAvailable || 0 }}</div><div class="label">待领养</div></div></div></el-col>
      <el-col :span="6"><div class="stat-card" style="background:#E6A23C;"><i class="el-icon-warning-outline" style="font-size:36px;margin-right:16px;"/><div><div class="num">{{ s.helpPending || 0 }}</div><div class="label">待处理求助</div></div></div></el-col>
      <el-col :span="6"><div class="stat-card" style="background:#909399;"><i class="el-icon-circle-check" style="font-size:36px;margin-right:16px;"/><div><div class="num">{{ s.animalAdopted || 0 }}</div><div class="label">已领养</div></div></div></el-col>
    </el-row>
    <div class="card" style="margin-top:20px;">
      <h3 style="margin:0 0 10px;">操作指引</h3>
      <p style="color:#606266;line-height:2;">1. 在【求助信息管理】中查看待处理的求助，添加处理记录。</p>
      <p style="color:#606266;line-height:2;">2. 在【流浪动物管理】中维护本站动物档案。</p>
      <p style="color:#606266;line-height:2;">3. 在【宠物领养管理】中审核领养申请。</p>
      <p style="color:#606266;line-height:2;">4. 定期开展【回访档案】记录领养后情况。</p>
    </div>
  </div>
</template>

<script>
import { statsApi } from '@/api'
export default {
  data() { return { s: {} } },
  mounted() { statsApi.dashboard().then(r => { this.s = r.data || {} }) }
}
</script>
