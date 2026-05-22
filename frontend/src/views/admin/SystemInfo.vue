<template>
  <div v-loading="loading">
    <div class="page-header">系统信息</div>
    <el-row :gutter="20">
      <el-col :span="14">
        <div class="card">
          <h3 style="margin:0 0 16px;">运行环境</h3>
          <el-descriptions :column="1" border size="medium">
            <el-descriptions-item label="应用名称">{{ info.appName }}</el-descriptions-item>
            <el-descriptions-item label="版本号">{{ info.version }}</el-descriptions-item>
            <el-descriptions-item label="Java 版本">{{ info.javaVersion }}</el-descriptions-item>
            <el-descriptions-item label="操作系统">{{ info.os }}</el-descriptions-item>
            <el-descriptions-item label="CPU 核数">{{ info.processors }}</el-descriptions-item>
            <el-descriptions-item label="运行时长">{{ info.uptime }}</el-descriptions-item>
            <el-descriptions-item label="内存使用">
              <el-progress :percentage="memPct" :status="memPct > 80 ? 'exception' : 'success'"/>
              <span style="color:#909399;font-size:12px;">{{ info.memoryUsedMB }} MB / {{ info.memoryTotalMB }} MB （上限 {{ info.memoryMaxMB }} MB）</span>
            </el-descriptions-item>
          </el-descriptions>
          <div style="margin-top:16px;text-align:right;">
            <el-button size="small" icon="el-icon-refresh" @click="load">刷新</el-button>
          </div>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="card">
          <h3 style="margin:0 0 16px;">数据规模</h3>
          <el-table :data="info.tables || []" border size="small">
            <el-table-column prop="label" label="模块"/>
            <el-table-column prop="table" label="数据表"/>
            <el-table-column prop="count" label="记录数" width="100" align="right"/>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { systemApi } from '@/api'
export default {
  data() { return { loading: false, info: {} } },
  computed: {
    memPct() {
      if (!this.info.memoryTotalMB) return 0
      return Math.round((this.info.memoryUsedMB / this.info.memoryTotalMB) * 100)
    }
  },
  mounted() { this.load() },
  methods: {
    load() {
      this.loading = true
      systemApi.info().then(r => { this.info = r.data || {} }).finally(() => { this.loading = false })
    }
  }
}
</script>
