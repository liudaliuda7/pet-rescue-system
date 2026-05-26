<template>
  <div class="front-layout">
    <el-header class="hdr">
      <div class="container hdr-inner">
        <div class="logo" @click="$router.push('/')">🐾 流浪动物救助与领养</div>
        <el-menu mode="horizontal" :default-active="active" router background-color="transparent" text-color="#fff" active-text-color="#ffd04b">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/help">求助信息</el-menu-item>
          <el-menu-item index="/animal">流浪动物</el-menu-item>
          <el-menu-item index="/notice">公告信息</el-menu-item>
          <el-menu-item index="/profile" v-if="user">个人中心</el-menu-item>
        </el-menu>
        <div class="user-area">
          <template v-if="user">
            <notify-bell all-path="/profile" style="margin-right:12px;vertical-align:middle;"/>
            <span style="margin-right:10px;">{{ user.name || user.username }}</span>
            <el-button size="mini" @click="goBackend" v-if="user.role !== 'user'">后台</el-button>
            <el-button size="mini" type="text" style="color:#fff;" @click="logout">退出</el-button>
          </template>
          <template v-else>
            <el-button size="mini" type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button size="mini" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    <div class="main">
      <div class="container">
        <router-view />
      </div>
    </div>
    <div class="footer">© 2026 流浪动物救助与领养系统</div>
  </div>
</template>

<script>
import { getUser, clearAuth } from '@/utils/auth'
import { auth } from '@/api'
import NotifyBell from '@/components/NotifyBell.vue'
export default {
  components: { NotifyBell },
  data() { return { user: getUser() } },
  computed: { active() { return '/' + (this.$route.path.split('/')[1] || '') } },
  watch: {
    '$route'() { this.user = getUser() }
  },
  methods: {
    logout() {
      auth.logout().catch(()=>{}).finally(() => {
        clearAuth(); this.user = null; this.$router.push('/'); this.$message.success('已退出')
      })
    },
    goBackend() {
      if (this.user.role === 'admin') this.$router.push('/admin')
      else if (this.user.role === 'station') this.$router.push('/station')
    }
  }
}
</script>

<style scoped lang="scss">
.front-layout { min-height: 100vh; display: flex; flex-direction: column; background: #f5f7fa; }
.hdr { background: #409EFF; color: #fff; height: 60px !important; line-height: 60px; padding: 0; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.hdr-inner { display: flex; align-items: center; height: 60px; }
.logo { font-size: 18px; font-weight: 600; cursor: pointer; margin-right: 30px; white-space: nowrap; }
.el-menu { flex: 1; border-bottom: 0 !important; }
::v-deep .el-menu--horizontal > .el-menu-item { color: #fff !important; border-bottom: 0 !important; height: 60px; line-height: 60px; }
::v-deep .el-menu--horizontal > .el-menu-item.is-active { color: #ffd04b !important; }
.user-area { white-space: nowrap; }
.main { flex: 1; padding: 20px 0; }
.footer { text-align: center; padding: 20px; color: #909399; font-size: 13px; }
</style>
