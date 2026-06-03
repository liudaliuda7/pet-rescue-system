<template>
  <div class="card" v-loading="loading">
    <div v-if="a">
      <el-row :gutter="30">
        <el-col :span="10">
          <img :src="a.image || placeholder" style="width:100%;border-radius:6px;"/>
        </el-col>
        <el-col :span="14">
          <h2 style="margin:0 0 10px;">{{ a.name }}
            <el-tag :type="a.status==='available'?'success':a.status==='adopted'?'info':a.status==='claimed'?'':'warning'">
              {{ a.status==='available'?'待领养':a.status==='adopted'?'已领养':a.status==='claimed'?'已认领':'治疗中' }}
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
            <el-button type="warning" @click="applyClaim">认领</el-button>
            <el-button @click="$router.back()">返回</el-button>
          </div>
        </el-col>
      </el-row>

      <!-- 领养流程 -->
      <div class="process-section">
        <h3 class="section-title">领养流程</h3>
        <el-steps :active="adoptionStep" :process-status="adoptionProcessStatus" finish-status="success" align-center>
          <el-step title="浏览动物" icon="el-icon-view"></el-step>
          <el-step title="提交申请" icon="el-icon-edit-outline"></el-step>
          <el-step title="等待审核" icon="el-icon-time"></el-step>
          <el-step :title="myAdoption && myAdoption.status === 'rejected' ? '审核未通过' : '审核通过'" :icon="myAdoption && myAdoption.status === 'rejected' ? 'el-icon-close' : 'el-icon-check'"></el-step>
          <el-step title="完成领养" icon="el-icon-trophy"></el-step>
        </el-steps>
        <div v-if="myAdoption" class="adoption-status-tip">
          <el-alert
            :title="adoptionStatusText"
            :type="adoptionAlertType"
            show-icon
            :closable="false"
          />
        </div>
      </div>

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

    <el-dialog title="认领申请" :visible.sync="showClaim" width="500px">
      <el-form :model="claimForm" :rules="claimRules" ref="claimF" label-width="100px">
        <el-form-item label="证明描述" prop="proofDesc"><el-input v-model="claimForm.proofDesc" type="textarea" :rows="4" placeholder="请描述您的认领证明，如宠物特征、丢失时间地点等"/></el-form-item>
        <el-form-item label="证明图片">
          <el-upload action="" :http-request="uploadClaimImage" :on-remove="onClaimRemove" list-type="picture-card" :limit="5" ref="claimUpload" :file-list="claimFileList">
            <i class="el-icon-plus"></i>
          </el-upload>
          <div class="el-upload__tip">支持上传最多5张证明图片</div>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showClaim=false">取消</el-button>
        <el-button type="primary" :loading="claimSubmitting" @click="submitClaim">提交认领</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { animalApi, adoptionApi, healthApi, animalClaimApi } from '@/api'
import request from '@/utils/request'
import { isLoggedIn, getUser } from '@/utils/auth'
export default {
  data() {
    return {
      a: null, loading: true, show: false, submitting: false, healthRecords: [],
      myAdoption: null,
      form: { animalId: null, contact: '', address: '', reason: '' },
      rules: {
        contact: [{ required: true, message: '请填写联系电话' }],
        address: [{ required: true, message: '请填写地址' }],
        reason: [{ required: true, message: '请说明领养理由' }]
      },
      showClaim: false, claimSubmitting: false, claimImages: [], claimFileList: [],
      claimForm: { animalId: null, proofDesc: '', proofImages: '' },
      claimRules: { proofDesc: [{ required: true, message: '请填写证明描述' }] },
      myClaim: null,
      placeholder: 'https://via.placeholder.com/600x400/cccccc/666666?text=No+Image'
    }
  },
  computed: {
    adoptionStep() {
      if (!this.myAdoption) return 0
      const s = this.myAdoption.status
      if (s === 'pending') return 2
      if (s === 'approved') return 4
      if (s === 'rejected') return 3
      return 1
    },
    adoptionProcessStatus() {
      if (this.myAdoption && this.myAdoption.status === 'rejected') return 'error'
      return 'process'
    },
    adoptionStatusText() {
      if (!this.myAdoption) return ''
      const s = this.myAdoption.status
      if (s === 'pending') return '您的领养申请已提交，正在等待审核...'
      if (s === 'approved') return '恭喜！您的领养申请已通过审核，领养完成！'
      if (s === 'rejected') return '很遗憾，您的领养申请未通过。' + (this.myAdoption.remark ? '原因：' + this.myAdoption.remark : '')
      return ''
    },
    adoptionAlertType() {
      if (!this.myAdoption) return 'info'
      const s = this.myAdoption.status
      if (s === 'pending') return 'warning'
      if (s === 'approved') return 'success'
      if (s === 'rejected') return 'error'
      return 'info'
    }
  },
  mounted() {
    animalApi.publicGet(this.$route.params.id).then(r => {
      this.a = r.data
      if (this.a) {
        healthApi.publicList({ animalId: this.a.id }).then(hr => {
          this.healthRecords = hr.data || []
        })
        this.loadMyAdoption()
        this.loadMyClaim()
      }
    }).finally(() => { this.loading = false })
  },
  methods: {
    loadMyAdoption() {
      if (!isLoggedIn()) return
      const u = getUser()
      if (u.role !== 'user') return
      adoptionApi.my({ animalId: this.a.id }).then(r => {
        this.myAdoption = r.data || null
      })
    },
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
          this.loadMyAdoption()
        }).finally(() => { this.submitting = false })
      })
    },
    applyClaim() {
      if (!isLoggedIn()) {
        this.$message.warning('请先登录'); this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } }); return
      }
      const u = getUser()
      if (u.role !== 'user') { this.$message.warning('请使用普通用户账号申请认领'); return }
      this.claimForm.animalId = this.a.id
      this.claimForm.proofDesc = ''
      this.claimForm.proofImages = ''
      this.claimImages = []
      this.claimFileList = []
      this.showClaim = true
    },
    uploadClaimImage(param) {
      const formData = new FormData()
      formData.append('file', param.file)
      request.post('/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(res => {
        const url = res.data.url
        this.claimImages.push(url)
        this.claimForm.proofImages = this.claimImages.join(',')
        param.onSuccess({ url })
      }).catch(err => {
        this.$message.error('图片上传失败')
        param.onError(err)
      })
    },
    onClaimRemove(file) {
      const url = file.response ? file.response.url : file.url
      this.claimImages = this.claimImages.filter(i => i !== url)
      this.claimForm.proofImages = this.claimImages.join(',')
    },
    submitClaim() {
      this.$refs.claimF.validate(ok => {
        if (!ok) return
        this.claimSubmitting = true
        animalClaimApi.add(this.claimForm).then(() => {
          this.$message.success('认领申请已提交，请等待审核')
          this.showClaim = false
          this.loadMyClaim()
        }).finally(() => { this.claimSubmitting = false })
      })
    },
    loadMyClaim() {
      if (!isLoggedIn()) return
      const u = getUser()
      if (u.role !== 'user') return
      animalClaimApi.my({ animalId: this.a.id }).then(r => {
        this.myClaim = r.data || null
      })
    }
  }
}
</script>

<style scoped>
.process-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
.adoption-status-tip {
  margin-top: 16px;
}
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
