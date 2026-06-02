<template>
  <div>
    <el-tabs v-model="tab">
      <el-tab-pane label="基本信息" name="info">
        <div class="card">
          <el-form :model="user" label-width="100px" style="max-width:500px;">
            <el-form-item label="账号"><el-input v-model="user.username" disabled/></el-form-item>
            <el-form-item label="昵称"><el-input v-model="user.name"/></el-form-item>
            <el-form-item label="手机号"><el-input v-model="user.phone"/></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="user.email"/></el-form-item>
            <el-form-item><el-button type="primary" @click="saveInfo">保存</el-button></el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="修改密码" name="pwd">
        <div class="card">
          <el-form :model="pwd" :rules="pwdRules" ref="pf" label-width="100px" style="max-width:500px;">
            <el-form-item label="原密码" prop="oldPassword"><el-input v-model="pwd.oldPassword" type="password" show-password/></el-form-item>
            <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwd.newPassword" type="password" show-password/></el-form-item>
            <el-form-item><el-button type="primary" @click="savePwd">修改</el-button></el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我的求助" name="help">
        <div class="card">
          <el-table :data="helps" border>
            <el-table-column prop="title" label="标题"/>
            <el-table-column prop="location" label="位置"/>
            <el-table-column prop="stationName" label="处理救助站"/>
            <el-table-column label="状态" width="100">
              <template slot-scope="s"><el-tag size="mini">{{ statusTxt(s.row.status) }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="180"/>
          </el-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我的领养" name="adopt">
        <div class="card">
          <el-table :data="adoptions" border>
            <el-table-column prop="animalName" label="动物"/>
            <el-table-column label="封面" width="90">
              <template slot-scope="s"><img v-if="s.row.animalImage" :src="s.row.animalImage" class="thumb"/></template>
            </el-table-column>
            <el-table-column label="救助站" width="140">
              <template slot-scope="s">
                <a v-if="s.row.stationId" class="station-link" @click="$router.push('/station/' + s.row.stationId)">{{ s.row.stationName || '—' }}</a>
                <span v-else>—</span>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="申请理由" show-overflow-tooltip/>
            <el-table-column label="状态" width="100">
              <template slot-scope="s">
                <el-tag size="mini" :type="adoptType(s.row.status)">{{ adoptTxt(s.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注"/>
            <el-table-column label="最新健康记录" width="220">
              <template slot-scope="s">
                <div v-if="s.row.latestHealthTime">
                  <div style="font-size:12px;color:#909399;">{{ s.row.latestHealthTime.replace('T',' ').substring(0,16) }}</div>
                  <div style="font-size:12px;margin-top:2px;">{{ s.row.latestHealthContent || '—' }}</div>
                </div>
                <span v-else style="color:#c0c4cc;">暂无</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="180"/>
            <el-table-column label="操作" width="160">
              <template slot-scope="s">
                <el-button size="mini" type="warning" @click="openTrack(s.row)">流程跟踪</el-button>
                <el-button v-if="s.row.status==='approved' && !reviewedStations[s.row.stationId]" size="mini" type="primary" @click="openReview(s.row)">评价</el-button>
                <el-tag v-else-if="s.row.status==='approved' && reviewedStations[s.row.stationId]" size="mini" type="info">已评价</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-dialog title="评价救助站" :visible.sync="reviewShow" width="500px">
          <el-form :model="reviewForm" :rules="reviewRules" ref="rf" label-width="80px">
            <el-form-item label="评分" prop="rating">
              <el-rate v-model="reviewForm.rating" show-score score-template="{value}分"/>
            </el-form-item>
            <el-form-item label="评价内容" prop="content">
              <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="请输入您的评价"/>
            </el-form-item>
          </el-form>
          <span slot="footer">
            <el-button @click="reviewShow=false">取消</el-button>
            <el-button type="primary" :loading="reviewLoading" @click="submitReview">提交</el-button>
          </span>
        </el-dialog>

        <el-dialog title="领养流程跟踪" :visible.sync="trackShow" width="600px">
          <div v-if="trackRow" style="padding: 20px 0;">
            <div style="margin-bottom: 16px; text-align: center; color: #606266;">
              <span style="font-weight:600;">{{ trackRow.animalName }}</span>
              <el-tag size="mini" :type="adoptType(trackRow.status)" style="margin-left:8px;">{{ adoptTxt(trackRow.status) }}</el-tag>
            </div>
            <el-steps :active="getTrackStep(trackRow)" :process-status="trackRow.status === 'rejected' ? 'error' : 'process'" finish-status="success" align-center>
              <el-step title="浏览动物" icon="el-icon-view"></el-step>
              <el-step title="提交申请" icon="el-icon-edit-outline"></el-step>
              <el-step title="等待审核" icon="el-icon-time"></el-step>
              <el-step :title="trackRow.status === 'rejected' ? '审核未通过' : '审核通过'" :icon="trackRow.status === 'rejected' ? 'el-icon-close' : 'el-icon-check'"></el-step>
              <el-step title="完成领养" icon="el-icon-trophy"></el-step>
            </el-steps>
            <div style="margin-top: 20px; text-align: center;">
              <el-alert
                :title="getTrackTip(trackRow)"
                :type="adoptAlertType(trackRow.status)"
                show-icon
                :closable="false"
              />
            </div>
            <div v-if="trackRow.remark" style="margin-top: 12px; color: #909399; text-align: center;">
              备注：{{ trackRow.remark }}
            </div>
          </div>
        </el-dialog>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { userApi, helpApi, adoptionApi, reviewApi } from '@/api'
import { getUser, setAuth } from '@/utils/auth'
export default {
  data() {
    return {
      tab: 'info',
      user: { ...(getUser() || {}) },
      pwd: { oldPassword: '', newPassword: '' },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入原密码' }],
        newPassword: [{ required: true, message: '请输入新密码' }, { min: 6, message: '至少 6 位' }]
      },
      helps: [], adoptions: [],
      reviewShow: false, reviewLoading: false,
      reviewForm: { rating: 5, content: '', adoptionId: null },
      reviewRules: {
        rating: [{ required: true, message: '请选择评分' }],
        content: [{ required: true, message: '请输入评价内容' }]
      },
      reviewedStations: {},
      trackShow: false, trackRow: null
    }
  },
  watch: {
    tab(v) {
      if (v === 'help') helpApi.page({ current: 1, size: 50 }).then(r => { this.helps = r.data.records || [] })
      if (v === 'adopt') adoptionApi.page({ current: 1, size: 50 }).then(r => {
        this.adoptions = r.data.records || []
        this.checkReviewedStations()
      })
    }
  },
  methods: {
    statusTxt(s) { return { pending:'待处理', processing:'处理中', done:'已完成', closed:'已关闭' }[s] || s },
    adoptTxt(s) { return { pending:'审核中', approved:'已通过', rejected:'已拒绝' }[s] || s },
    adoptType(s) { return { pending:'warning', approved:'success', rejected:'danger' }[s] || '' },
    saveInfo() {
      userApi.updateProfile(this.user).then(() => {
        const u = { ...getUser(), ...this.user }
        setAuth(localStorage.getItem('token'), u)
        this.$message.success('已保存')
      })
    },
    savePwd() {
      this.$refs.pf.validate(ok => {
        if (!ok) return
        userApi.changePassword(this.pwd).then(() => {
          this.$message.success('密码已修改')
          this.pwd = { oldPassword: '', newPassword: '' }
        })
      })
    },
    checkReviewedStations() {
      const stationIds = [...new Set(this.adoptions.filter(a => a.status === 'approved' && a.stationId).map(a => a.stationId))]
      stationIds.forEach(sid => {
        reviewApi.check(sid).then(r => {
          this.$set(this.reviewedStations, sid, r.data)
        })
      })
    },
    openReview(row) {
      this.reviewForm = { rating: 5, content: '', adoptionId: row.id }
      this.reviewShow = true
      this.$nextTick(() => this.$refs.rf && this.$refs.rf.clearValidate())
    },
    openTrack(row) {
      this.trackRow = row
      this.trackShow = true
    },
    getTrackStep(row) {
      if (!row) return 0
      if (row.status === 'pending') return 2
      if (row.status === 'approved') return 4
      if (row.status === 'rejected') return 3
      return 1
    },
    getTrackTip(row) {
      if (!row) return ''
      if (row.status === 'pending') return '您的领养申请已提交，正在等待审核...'
      if (row.status === 'approved') return '恭喜！您的领养申请已通过，领养完成！'
      if (row.status === 'rejected') return '很遗憾，您的领养申请未通过审核。'
      return ''
    },
    adoptAlertType(s) {
      return { pending: 'warning', approved: 'success', rejected: 'error' }[s] || 'info'
    },
    submitReview() {
      this.$refs.rf.validate(ok => {
        if (!ok) return
        this.reviewLoading = true
        reviewApi.add(this.reviewForm).then(() => {
          this.$message.success('评价成功')
          this.reviewShow = false
          this.checkReviewedStations()
        }).finally(() => { this.reviewLoading = false })
      })
    }
  }
}
</script>

<style scoped>
.station-link {
  color: #409eff;
  cursor: pointer;
}
.station-link:hover {
  text-decoration: underline;
}
</style>
