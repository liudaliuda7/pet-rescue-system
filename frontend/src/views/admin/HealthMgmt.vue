<template>
  <div class="card">
    <div class="page-header">健康档案管理</div>
    <div class="toolbar">
      <el-select v-model="q.animalId" clearable filterable placeholder="按动物筛选" style="width:200px;" @change="load(1)">
        <el-option v-for="a in animals" :key="a.id" :label="a.name" :value="a.id"/>
      </el-select>
      <el-button type="primary" @click="load(1)">搜索</el-button>
      <span class="spacer"/>
      <el-button type="success" icon="el-icon-plus" @click="open()">新增档案</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="animalName" label="动物" width="120"/>
      <el-table-column prop="doctor" label="医生" width="120"/>
      <el-table-column prop="recordDate" label="就诊日期" width="120"/>
      <el-table-column prop="content" label="健康内容" show-overflow-tooltip/>
      <el-table-column prop="createTime" label="录入时间" width="170"/>
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
        <el-form-item label="动物" prop="animalId">
          <el-select v-model="form.animalId" filterable style="width:100%;"><el-option v-for="a in animals" :key="a.id" :label="a.name" :value="a.id"/></el-select>
        </el-form-item>
        <el-form-item label="医生"><el-input v-model="form.doctor"/></el-form-item>
        <el-form-item label="就诊日期"><el-date-picker v-model="form.recordDate" value-format="yyyy-MM-dd" style="width:100%;"/></el-form-item>
        <el-form-item label="健康内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4"/></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { healthApi, animalApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 10, animalId: undefined }, list: [], total: 0, show: false, animals: [],
      form: { id: null, animalId: null, content: '', doctor: '', recordDate: null },
      rules: { animalId: [{ required: true, message: '请选择动物' }], content: [{ required: true, message: '请输入内容' }] }
    }
  },
  mounted() {
    animalApi.page({ current: 1, size: 500 }).then(r => { this.animals = r.data.records || [] })
    this.load()
  },
  methods: {
    load(p) { if (p) this.q.current = p; healthApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 }) },
    open(row) { this.form = row ? { ...row } : { id: null, animalId: null, content: '', doctor: '', recordDate: null }; this.show = true; this.$nextTick(() => this.$refs.f && this.$refs.f.clearValidate()) },
    save() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        const fn = this.form.id ? healthApi.update : healthApi.add
        fn(this.form).then(() => { this.$message.success('已保存'); this.show = false; this.load() })
      })
    },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => healthApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })).catch(()=>{})
    }
  }
}
</script>
