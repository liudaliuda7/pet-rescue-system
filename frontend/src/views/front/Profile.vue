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
      <el-tab-pane label="我的收藏" name="fav">
        <div class="card">
          <el-row :gutter="16" v-if="favorites.length">
            <el-col :span="6" v-for="f in favorites" :key="f.id">
              <el-card :body-style="{ padding: 0 }" shadow="hover" style="margin-bottom:16px;">
                <img :src="f.animalImage || placeholder" style="width:100%;height:160px;object-fit:cover;display:block;cursor:pointer;" @click="$router.push('/animal/' + f.animalId)"/>
                <div style="padding:12px;">
                  <div style="display:flex;justify-content:space-between;align-items:center;">
                    <span style="font-weight:600;cursor:pointer;" @click="$router.push('/animal/' + f.animalId)">{{ f.animalName }}</span>
                    <el-tag size="mini" :type="f.animalStatus==='available'?'success':f.animalStatus==='adopted'?'info':'warning'">
                      {{ f.animalStatus==='available'?'待领养':f.animalStatus==='adopted'?'已领养':'治疗中' }}
                    </el-tag>
                  </div>
                  <div style="color:#909399;font-size:12px;margin-top:6px;">{{ f.typeName || '' }} · {{ f.stationName || '未指派' }}</div>
                  <el-button type="text" size="mini" style="color:#F56C6C;padding:4px 0 0;" @click="removeFav(f)">取消收藏</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-else description="暂无收藏"/>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我的消息" name="msg">
        <div class="card">
          <div style="margin-bottom:12px;text-align:right;">
            <el-button size="mini" @click="markAllMsgRead" :disabled="!msgUnread">全部标记已读</el-button>
          </div>
          <el-table :data="messages" border>
            <el-table-column prop="title" label="标题"/>
            <el-table-column prop="content" label="内容" show-overflow-tooltip/>
            <el-table-column label="类型" width="120">
              <template slot-scope="s"><el-tag size="mini">{{ msgTypeText(s.row.type) }}</el-tag></template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template slot-scope="s">
                <el-tag size="mini" :type="s.row.isRead ? 'info' : 'warning'">{{ s.row.isRead ? '已读' : '未读' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="180"/>
            <el-table-column label="操作" width="100">
              <template slot-scope="s">
                <el-button size="mini" type="text" @click="readMsg(s.row)" :disabled="!!s.row.isRead">标记已读</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { userApi, helpApi, adoptionApi, favoriteApi, messageApi } from '@/api'
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
      helps: [], adoptions: [], favorites: [], messages: [], msgUnread: 0,
      placeholder: 'https://via.placeholder.com/400x240/cccccc/666666?text=No+Image'
    }
  },
  watch: {
    tab(v) {
      if (v === 'help') helpApi.page({ current: 1, size: 50 }).then(r => { this.helps = r.data.records || [] })
      if (v === 'adopt') adoptionApi.page({ current: 1, size: 50 }).then(r => { this.adoptions = r.data.records || [] })
      if (v === 'fav') this.loadFavorites()
      if (v === 'msg') this.loadMessages()
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
    loadFavorites() {
      favoriteApi.my().then(r => { this.favorites = r.data || [] })
    },
    removeFav(f) {
      favoriteApi.remove(f.animalId).then(() => {
        this.$message.success('已取消收藏')
        this.favorites = this.favorites.filter(i => i.id !== f.id)
      })
    },
    loadMessages() {
      messageApi.page({ current: 1, size: 50 }).then(r => {
        this.messages = r.data.records || []
        this.msgUnread = this.messages.filter(m => !m.isRead).length
      })
    },
    readMsg(m) {
      messageApi.read(m.id).then(() => { m.isRead = 1; this.msgUnread = Math.max(0, this.msgUnread - 1) })
    },
    markAllMsgRead() {
      messageApi.readAll().then(() => { this.messages.forEach(m => m.isRead = 1); this.msgUnread = 0 })
    },
    msgTypeText(t) {
      return { adoption_approved: '领养通过', adoption_rejected: '领养拒绝', help_assigned: '求助指派', help_new: '新求助任务' }[t] || t
    }
  }
}
</script>
