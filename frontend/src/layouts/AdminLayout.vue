<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="aside">
      <div class="logo">🐾 流浪动物救助系统</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF" unique-opened>
        <el-menu-item index="/admin/dashboard"><i class="el-icon-s-home"/><span>主页</span></el-menu-item>
        <el-menu-item index="/admin/user"><i class="el-icon-user"/><span>用户管理</span></el-menu-item>
        <el-menu-item index="/admin/station"><i class="el-icon-office-building"/><span>救助站管理</span></el-menu-item>
        <el-menu-item index="/admin/help"><i class="el-icon-warning-outline"/><span>求助信息管理</span></el-menu-item>
        <el-menu-item index="/admin/help-record"><i class="el-icon-document"/><span>求助记录管理</span></el-menu-item>
        <el-menu-item index="/admin/animal-type"><i class="el-icon-collection-tag"/><span>动物种类管理</span></el-menu-item>
        <el-menu-item index="/admin/animal"><i class="el-icon-orange"/><span>流浪动物管理</span></el-menu-item>
        <el-submenu index="adopt">
          <template slot="title"><i class="el-icon-view"/><span>回访领养管理</span></template>
          <el-menu-item index="/admin/adoption">领养管理</el-menu-item>
          <el-menu-item index="/admin/visit">回访管理</el-menu-item>
        </el-submenu>
        <el-menu-item index="/admin/health"><i class="el-icon-first-aid-kit"/><span>健康档案管理</span></el-menu-item>
        <el-submenu index="sys">
          <template slot="title"><i class="el-icon-setting"/><span>系统管理</span></template>
          <el-menu-item index="/admin/notice">公告管理</el-menu-item>
          <el-menu-item index="/admin/message">消息管理</el-menu-item>
          <el-menu-item index="/admin/system">系统信息</el-menu-item>
        </el-submenu>
        <el-menu-item index="/admin/profile"><i class="el-icon-user"/><span>个人中心</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="hdr">
        <div>欢迎使用流浪动物救助管理后台</div>
        <div>
          <el-button type="text" @click="$router.push('/')">返回前台</el-button>
          <notify-bell all-path="/admin/message" style="vertical-align:middle;margin-right:8px;"/>
          <el-dropdown @command="onCmd">
            <span class="user-bar">
              <i class="el-icon-user-solid"></i>
              {{ user ? (user.name || user.username) : '' }}
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script>
import { getUser, clearAuth } from '@/utils/auth'
import { auth } from '@/api'
import NotifyBell from '@/components/NotifyBell.vue'
export default {
  components: { NotifyBell },
  data() { return { user: getUser() } },
  methods: {
    onCmd(cmd) {
      if (cmd === 'profile') this.$router.push('/admin/profile')
      else if (cmd === 'logout') {
        auth.logout().catch(()=>{}).finally(() => {
          clearAuth(); this.$router.push('/login'); this.$message.success('已退出')
        })
      }
    }
  }
}
</script>

<style scoped lang="scss">
.admin-layout { height: 100vh; }
.aside { background: #304156; color: #fff; overflow-y: auto; }
.logo { padding: 20px 16px; color: #fff; font-size: 16px; font-weight: 600; border-bottom: 1px solid #1f2d3d; }
.el-menu { border-right: 0; }
.el-menu-item i, .el-submenu i { margin-right: 8px; color: #bfcbd9; }
.hdr { background: #fff; box-shadow: 0 1px 4px rgba(0,21,41,.08); display: flex; justify-content: space-between; align-items: center; }
.user-bar { cursor: pointer; padding: 0 10px; }
</style>
