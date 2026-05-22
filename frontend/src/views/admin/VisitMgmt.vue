<template>
  <div class="card">
    <div class="page-header">回访档案管理</div>
    <div class="toolbar">
      <span class="spacer"/>
      <el-button type="success" icon="el-icon-plus" @click="open()">新增回访</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="adoptionId" label="领养ID" width="80"/>
      <el-table-column prop="animalName" label="动物" width="100"/>
      <el-table-column prop="adopterName" label="领养人" width="100"/>
      <el-table-column prop="visitor" label="回访人" width="100"/>
      <el-table-column prop="status" label="状况" width="100"/>
      <el-table-column prop="content" label="回访内容" show-overflow-tooltip/>
      <el-table-column prop="createTime" label="时间" width="170"/>
      <el-table-column label="操作" width="180">
        <template slot-scope="s">
          <el-button size="mini" @click="open(s.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="del(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="margin-top:16px;text-align:right;"/>

    <el-dialog :title="form.id?'编辑':'新增'" :visible.sync="show" width="500px">
      <el-form :model="form" :rules="rules" ref="f" label-width="100px">
        <el-form-item label="领养记录" prop="adoptionId">
          <el-select v-model="form.adoptionId" filterable style="width:100%;">
            <el-option v-for="a in adoptions" :key="a.id" :label="'#'+a.id+' '+(a.animalName||'')+' / '+(a.userName||'')" :value="a.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="回访人"><el-input v-model="form.visitor"/></el-form-item>
        <el-form-item label="状况"><el-input v-model="form.status" placeholder="如：良好/需关注"/></el-form-item>
        <el-form-item label="回访内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4"/></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { visitApi, adoptionApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 10 }, list: [], total: 0, show: false, adoptions: [],
      form: { id: null, adoptionId: null, content: '', visitor: '', status: '良好' },
      rules: { adoptionId: [{ required: true, message: '请选择领养记录' }], content: [{ required: true, message: '请输入回访内容' }] }
    }
  },
  mounted() { adoptionApi.page({ current: 1, size: 200, status: 'approved' }).then(r => { this.adoptions = r.data.records || [] }); this.load() },
  methods: {
    load(p) { if (p) this.q.current = p; visitApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 }) },
    open(row) { this.form = row ? { ...row } : { id: null, adoptionId: null, content: '', visitor: '', status: '良好' }; this.show = true; this.$nextTick(() => this.$refs.f && this.$refs.f.clearValidate()) },
    save() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        const fn = this.form.id ? visitApi.update : visitApi.add
        fn(this.form).then(() => { this.$message.success('已保存'); this.show = false; this.load() })
      })
    },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => visitApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })).catch(()=>{})
    }
  }
}
</script>
