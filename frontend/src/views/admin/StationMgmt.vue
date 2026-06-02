<template>
  <div class="card">
    <div class="page-header">救助站管理</div>
    <div class="toolbar">
      <el-input v-model="q.name" placeholder="名称" style="width:200px;" clearable @keyup.enter.native="load(1)"/>
      <el-button type="primary" @click="load(1)">搜索</el-button>
      <span class="spacer"/>
      <el-button type="success" icon="el-icon-plus" @click="open()">新增</el-button>
    </div>
    <el-table :data="list" border @sort-change="handleSort">
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="name" label="名称"/>
      <el-table-column prop="contact" label="联系人" width="120"/>
      <el-table-column prop="phone" label="电话" width="140"/>
      <el-table-column prop="address" label="地址"/>
      <el-table-column prop="animalTotal" label="累计救助" width="100" sortable="custom"/>
      <el-table-column prop="animalAvailable" label="待领养" width="90" sortable="custom"/>
      <el-table-column prop="adoptedTotal" label="领养成功" width="100" sortable="custom"/>
      <el-table-column prop="helpTotal" label="处理求助" width="100" sortable="custom"/>
      <el-table-column prop="latestActivityTime" label="最近动态时间" width="170">
        <template slot-scope="s">{{ s.row.latestActivityTime ? s.row.latestActivityTime.replace('T', ' ').substring(0, 16) : '—' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="s">
          <el-button size="mini" @click="open(s.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="del(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="margin-top:16px;text-align:right;"/>

    <el-dialog :title="form.id?'编辑':'新增'" :visible.sync="show" width="500px">
      <el-form :model="form" :rules="rules" ref="f" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="联系人"><el-input v-model="form.contact"/></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone"/></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address"/></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.description" type="textarea" :rows="4"/></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { stationApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 10, name: '', sortField: '', sortOrder: '' },
      list: [], total: 0, show: false,
      form: { id: null, name: '', contact: '', phone: '', address: '', description: '' },
      rules: { name: [{ required: true, message: '请输入名称' }] }
    }
  },
  mounted() { this.load() },
  methods: {
    load(p) {
      if (p) this.q.current = p
      stationApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 })
    },
    handleSort({ prop, order }) {
      this.q.sortField = order ? prop : ''
      this.q.sortOrder = order === 'ascending' ? 'asc' : order === 'descending' ? 'desc' : ''
      this.load(1)
    },
    open(row) {
      this.form = row ? { ...row } : { id: null, name: '', contact: '', phone: '', address: '', description: '' }
      this.show = true; this.$nextTick(() => this.$refs.f && this.$refs.f.clearValidate())
    },
    save() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        const fn = this.form.id ? stationApi.update : stationApi.add
        fn(this.form).then(() => { this.$message.success('已保存'); this.show = false; this.load() })
      })
    },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => stationApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })).catch(()=>{})
    }
  }
}
</script>
