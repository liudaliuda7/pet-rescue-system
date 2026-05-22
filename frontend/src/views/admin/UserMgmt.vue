<template>
  <div class="card">
    <div class="page-header">用户管理</div>
    <div class="toolbar">
      <el-input v-model="q.username" placeholder="账号" style="width:200px;" clearable @keyup.enter.native="load(1)"/>
      <el-select v-model="q.role" clearable placeholder="角色" style="width:140px;" @change="load(1)">
        <el-option label="管理员" value="admin"/><el-option label="救助站" value="station"/><el-option label="用户" value="user"/>
      </el-select>
      <el-button type="primary" @click="load(1)">搜索</el-button>
      <span class="spacer"/>
      <el-button type="success" @click="open()" icon="el-icon-plus">新增</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="username" label="账号"/>
      <el-table-column prop="name" label="昵称"/>
      <el-table-column label="角色" width="100">
        <template slot-scope="s"><el-tag size="mini">{{ roleText(s.row.role) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="phone" label="手机"/>
      <el-table-column prop="email" label="邮箱"/>
      <el-table-column label="救助站" width="160">
        <template slot-scope="s">{{ stationName(s.row.stationId) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="s"><el-tag size="mini" :type="s.row.status?'success':'danger'">{{ s.row.status?'正常':'禁用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="s">
          <el-button size="mini" @click="open(s.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="del(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="margin-top:16px;text-align:right;"/>

    <el-dialog :title="form.id?'编辑用户':'新增用户'" :visible.sync="show" width="500px">
      <el-form :model="form" :rules="rules" ref="f" label-width="100px">
        <el-form-item label="账号" prop="username"><el-input v-model="form.username" :disabled="!!form.id"/></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="form.password" :placeholder="form.id?'留空则不修改':'默认 123456'"/></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%;">
            <el-option label="管理员" value="admin"/><el-option label="救助站" value="station"/><el-option label="用户" value="user"/>
          </el-select>
        </el-form-item>
        <el-form-item label="所属救助站" v-if="form.role==='station'">
          <el-select v-model="form.stationId" style="width:100%;" clearable>
            <el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="手机"><el-input v-model="form.phone"/></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email"/></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :label="1">正常</el-radio><el-radio :label="0">禁用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { userApi, stationApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 10, username: '', role: '' },
      list: [], total: 0, stations: [], show: false,
      form: { id: null, username: '', password: '', name: '', role: 'user', phone: '', email: '', status: 1, stationId: null },
      rules: {
        username: [{ required: true, message: '请输入账号' }],
        role: [{ required: true, message: '请选择角色' }]
      }
    }
  },
  mounted() { stationApi.list().then(r => { this.stations = r.data || [] }); this.load() },
  methods: {
    roleText(r) { return { admin: '管理员', station: '救助站', user: '用户' }[r] || r },
    stationName(id) { const s = this.stations.find(x => x.id === id); return s ? s.name : '' },
    load(p) {
      if (p) this.q.current = p
      userApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 })
    },
    open(row) {
      this.form = row ? { ...row, password: '' } : { id: null, username: '', password: '', name: '', role: 'user', phone: '', email: '', status: 1, stationId: null }
      this.show = true
      this.$nextTick(() => this.$refs.f && this.$refs.f.clearValidate())
    },
    save() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        const fn = this.form.id ? userApi.update : userApi.add
        fn(this.form).then(() => { this.$message.success('已保存'); this.show = false; this.load() })
      })
    },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => {
        userApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })
      }).catch(() => {})
    }
  }
}
</script>
