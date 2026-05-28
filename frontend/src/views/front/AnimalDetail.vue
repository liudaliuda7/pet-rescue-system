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
            <el-descriptions-item label="所属救助站">{{ a.stationName || '未指派' }}</el-descriptions-item>
            <el-descriptions-item label="描述">{{ a.description }}</el-descriptions-item>
          </el-descriptions>
          <div style="margin-top:20px;">
            <el-button type="primary" :disabled="a.status !== 'available'" @click="apply">申请领养</el-button>
            <el-button @click="$router.back()">返回</el-button>
          </div>
        </el-col>
      </el-row>

      <div style="margin-top:30px;">
        <h3 style="margin:0 0 16px;">健康档案</h3>
        <el-timeline v-if="records.length">
          <el-timeline-item v-for="r in records" :key="r.id" :timestamp="r.recordDate" placement="top">
            <el-card shadow="never" style="padding:4px 0;">
              <div style="font-weight:600;">{{ r.doctor }}</div>
              <div style="margin-top:6px;color:#606266;">{{ r.content }}</div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无健康记录" :image-size="80"/>
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
      a: null, loading: true, show: false, submitting: false, records: [],
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
    const id = Number(this.$route.params.id)
    if (!id || isNaN(id)) { this.$router.replace('/animal'); return }
    animalApi.publicGet(id).then(r => { this.a = r.data }).finally(() => { this.loading = false })
    healthApi.publicList({ animalId: id }).then(r => { this.records = r.data || [] }).catch(() => {})
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
