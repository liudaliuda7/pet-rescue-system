<template>
  <div class="login-bg">
    <div class="login-box">
      <h2>注册账号</h2>
      <el-form :model="form" :rules="rules" ref="f" @submit.native.prevent="submit">
        <el-form-item prop="username"><el-input v-model="form.username" placeholder="账号"/></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" type="password" placeholder="密码" show-password/></el-form-item>
        <el-form-item prop="name"><el-input v-model="form.name" placeholder="昵称"/></el-form-item>
        <el-form-item prop="phone"><el-input v-model="form.phone" placeholder="手机号"/></el-form-item>
        <el-form-item prop="email"><el-input v-model="form.email" placeholder="邮箱"/></el-form-item>
        <el-button type="primary" native-type="submit" style="width:100%;" :loading="loading">注册</el-button>
        <div style="margin-top:12px;text-align:center;">
          <el-link @click="$router.push('/login')">已有账号？去登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { auth } from '@/api'
export default {
  data() {
    return {
      loading: false,
      form: { username: '', password: '', name: '', phone: '', email: '' },
      rules: {
        username: [{ required: true, message: '请输入账号' }],
        password: [{ required: true, message: '请输入密码' }, { min: 6, message: '至少 6 位' }]
      }
    }
  },
  methods: {
    submit() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        this.loading = true
        auth.register(this.form).then(() => {
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
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
</style>
