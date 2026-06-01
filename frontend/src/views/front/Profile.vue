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
            <el-table-column prop="createTime" label="申请时间" width="180"/>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { userApi, helpApi, adoptionApi } from '@/api'
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
      helps: [], adoptions: []
    }
  },
  watch: {
    tab(v) {
      if (v === 'help') helpApi.page({ current: 1, size: 50 }).then(r => { this.helps = r.data.records || [] })
      if (v === 'adopt') adoptionApi.page({ current: 1, size: 50 }).then(r => { this.adoptions = r.data.records || [] })
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
