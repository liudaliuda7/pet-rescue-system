<template>
  <div>
    <div class="page-header">个人中心</div>
    <el-row :gutter="20">
      <el-col :span="14">
        <div class="card">
          <h3 style="margin:0 0 16px;">基本信息</h3>
          <el-form :model="user" label-width="100px">
            <el-form-item label="账号"><el-input v-model="user.username" disabled/></el-form-item>
            <el-form-item label="角色">
              <el-tag>{{ roleText(user.role) }}</el-tag>
            </el-form-item>
            <el-form-item label="昵称"><el-input v-model="user.name"/></el-form-item>
            <el-form-item label="手机号"><el-input v-model="user.phone"/></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="user.email"/></el-form-item>
            <el-form-item><el-button type="primary" @click="save">保存</el-button></el-form-item>
          </el-form>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="card">
          <h3 style="margin:0 0 16px;">修改密码</h3>
          <el-form :model="pwd" :rules="rules" ref="pf" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword"><el-input v-model="pwd.oldPassword" type="password" show-password/></el-form-item>
            <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwd.newPassword" type="password" show-password/></el-form-item>
            <el-form-item><el-button type="primary" @click="savePwd">修改</el-button></el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { userApi } from '@/api'
import { getUser, setAuth } from '@/utils/auth'
export default {
  data() {
    return {
      user: { ...(getUser() || {}) },
      pwd: { oldPassword: '', newPassword: '' },
      rules: {
        oldPassword: [{ required: true, message: '请输入原密码' }],
        newPassword: [{ required: true, message: '请输入新密码' }, { min: 6, message: '至少 6 位' }]
      }
    }
  },
  methods: {
    roleText(r) { return { admin: '管理员', station: '救助站', user: '普通用户' }[r] || r },
    save() {
      userApi.updateProfile(this.user).then(() => {
        const u = { ...getUser(), ...this.user }
        setAuth(localStorage.getItem('token'), u)
        this.$message.success('已保存')
      })
    },
    savePwd() {
      this.$refs.pf.validate(ok => {
        if (!ok) return
        userApi.changePassword(this.pwd).then(() => { this.$message.success('密码已修改'); this.pwd = { oldPassword: '', newPassword: '' } })
      })
    }
  }
}
</script>
