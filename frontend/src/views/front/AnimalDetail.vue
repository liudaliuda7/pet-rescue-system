<template>
  <div class="card" v-loading="loading">
    <div v-if="a">
      <el-row :gutter="30">
        <el-col :span="10">
          <img :src="a.image || placeholder" style="width:100%;border-radius:6px;"/>
        </el-col>
        <el-col :span="14">
          <h2 style="margin:0 0 10px;">{{ a.name }}
            <el-tag :type="a.status==='available'?'success':a.status==='adopted'?'info':'warning'">
              {{ a.status==='available'?'待领养':a.status==='adopted'?'已领养':'治疗中' }}
            </el-tag>
          </h2>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="种类">{{ a.typeName }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ a.gender }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ a.age }}</el-descriptions-item>
            <el-descriptions-item label="毛色">{{ a.color }}</el-descriptions-item>
            <el-descriptions-item label="健康">{{ a.healthStatus }}</el-descriptions-item>
            <el-descriptions-item label="所属救助站">
              <router-link v-if="a.stationId" :to="'/station/' + a.stationId" style="color:#409EFF;text-decoration:none;">{{ a.stationName || '未知救助站' }}</router-link>
              <span v-else>未指派</span>
            </el-descriptions-item>
            <el-descriptions-item label="描述">{{ a.description }}</el-descriptions-item>
          </el-descriptions>
          <div style="margin-top:20px;">
            <el-button type="primary" :disabled="a.status !== 'available'" @click="apply">申请领养</el-button>
            <el-button @click="$router.back()">返回</el-button>
          </div>
        </el-col>
      </el-row>

      <!-- 健康档案时间轴 -->
      <div class="health-section">
        <h3 class="section-title">健康档案</h3>
        <el-timeline v-if="healthRecords.length > 0">
          <el-timeline-item
            v-for="record in healthRecords"
            :key="record.id"
            :timestamp="record.recordDate"
            placement="top"
            :type="record.recordDate && new Date(record.recordDate) > new Date(Date.now() - 30*24*60*60*1000) ? 'primary' : ''"
          >
            <div class="record-card">
              <div class="record-header">
                <span class="record-doctor"><i class="el-icon-user"></i> {{ record.doctor || '未知医生' }}</span>
                <span class="record-date">{{ record.recordDate }}</span>
              </div>
              <div class="record-content">{{ record.content }}</div>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无健康档案记录" :image-size="80"/>
      </div>
    </div>

    <el-dialog title="领养申请" :visible.sync="show" width="500px">
      <el-form :model="form" :rules="rules" ref="f" label-width="80px">
        <el-form-item label="联系电话" prop="contact"><el-input v-model="form.contact"/></el-form-item>
        <el-form-item label="居住地址" prop="address"><el-input v-model="form.address"/></el-form-item>
        <el-form-item label="领养理由" prop="reason"><el-input v-model="form.reason" type="textarea" :rows="4"/></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="show=false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">提交申请</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { animalApi, adoptionApi, healthApi } from '@/api'
import { isLoggedIn, getUser } from '@/utils/auth'
export default {
  data() {
    return {
      a: null, loading: true, show: false, submitting: false, healthRecords: [],
      form: { animalId: null, contact: '', address: '', reason: '' },
      rules: {
        contact: [{ required: true, message: '请填写联系电话' }],
        address: [{ required: true, message: '请填写地址' }],
        reason: [{ required: true, message: '请说明领养理由' }]
      },
      placeholder: 'https://via.placeholder.com/600x400/cccccc/666666?text=No+Image'
    }
  },
  mounted() {
    animalApi.publicGet(this.$route.params.id).then(r => {
      this.a = r.data
      if (this.a) {
        healthApi.publicList({ animalId: this.a.id }).then(hr => {
          this.healthRecords = hr.data || []
        })
      }
    }).finally(() => { this.loading = false })
  },
  methods: {
    apply() {
      if (!isLoggedIn()) {
        this.$message.warning('请先登录'); this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } }); return
      }
      const u = getUser()
      if (u.role !== 'user') { this.$message.warning('请使用普通用户账号申请领养'); return }
      this.form.animalId = this.a.id
      this.form.contact = u.phone || ''
      this.show = true
    },
    submit() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        this.submitting = true
        adoptionApi.add(this.form).then(() => {
          this.$message.success('申请已提交，请等待审核')
          this.show = false
        }).finally(() => { this.submitting = false })
      })
    }
  }
}
</script>

<style scoped>
.health-section {
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
.record-card {
  background: #f5f7fa;
  border-radius: 6px;
  padding: 12px 16px;
}
.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.record-doctor {
  color: #409EFF;
  font-weight: 500;
}
.record-doctor i {
  margin-right: 4px;
}
.record-date {
  color: #909399;
  font-size: 13px;
}
.record-content {
  color: #606266;
  line-height: 1.6;
}
</style>
