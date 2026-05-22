<template>
  <div class="login-bg">
    <div class="login-box">
      <h2>流浪动物救助与领养系统</h2>
      <el-form :model="form" :rules="rules" ref="f" @submit.native.prevent="submit">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="账号" prefix-icon="el-icon-user" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" placeholder="密码" type="password" prefix-icon="el-icon-lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-radio-group v-model="form.role" size="small">
            <el-radio-button label="user">普通用户</el-radio-button>
            <el-radio-button label="station">救助站</el-radio-button>
            <el-radio-button label="admin">管理员</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-button type="primary" native-type="submit" style="width:100%;" :loading="loading">登录</el-button>
        <div style="margin-top:12px;text-align:center;">
          <el-link type="primary" @click="$router.push('/register')">注册账号</el-link>
          <span style="margin:0 10px;color:#ccc;">|</span>
          <el-link @click="$router.push('/')">返回首页</el-link>
        </div>
        <div class="hint">
          <p>测试账号（密码均为 <b>123456</b>）：</p>
          <p>管理员: admin · 救助站: station1 / station2 · 用户: user1 / user2</p>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { auth } from '@/api'
import { setAuth } from '@/utils/auth'
export default {
  data() {
    return {
      loading: false,
      form: { username: '', password: '', role: 'user' },
      rules: {
        username: [{ required: true, message: '请输入账号' }],
        password: [{ required: true, message: '请输入密码' }]
      }
    }
  },
  methods: {
    submit() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        this.loading = true
        auth.login(this.form).then(r => {
          setAuth(r.data.token, r.data.user)
          this.$message.success('登录成功')
          const redirect = this.$route.query.redirect
          if (redirect) return this.$router.push(redirect)
          const role = r.data.user.role
          if (role === 'admin') this.$router.push('/admin')
          else if (role === 'station') this.$router.push('/station')
          else this.$router.push('/')
        }).finally(() => { this.loading = false })
      })
    }
  }
}
</script>

<style scoped lang="scss">
.login-bg { min-height: 100vh; background: linear-gradient(135deg,#667eea,#764ba2); display:flex; align-items:center; justify-content:center; }
.login-box { width: 420px; background:#fff; padding: 40px 30px; border-radius:8px; box-shadow:0 8px 30px rgba(0,0,0,0.2); }
h2 { text-align:center; margin: 0 0 30px; color:#303133; }
.hint { margin-top: 16px; color: #909399; font-size: 12px; line-height: 1.6; background:#f4f4f5; padding:10px; border-radius:4px; p { margin: 0; } }
</style>
