<template>
  <div class="card">
    <div class="page-header">公告管理</div>
    <div class="toolbar">
      <el-input v-model="q.title" placeholder="标题" style="width:200px;" clearable @keyup.enter.native="load(1)"/>
      <el-button type="primary" @click="load(1)">搜索</el-button>
      <span class="spacer"/>
      <el-button type="success" icon="el-icon-plus" @click="open()">新增公告</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="content" label="内容" show-overflow-tooltip/>
      <el-table-column prop="createTime" label="发布时间" width="170"/>
      <el-table-column label="操作" width="180">
        <template slot-scope="s">
          <el-button size="mini" @click="open(s.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="del(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="margin-top:16px;text-align:right;"/>

    <el-dialog :title="form.id?'编辑':'新增'" :visible.sync="show" width="600px">
      <el-form :model="form" :rules="rules" ref="f" label-width="80px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title"/></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="6"/></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { noticeApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 10, title: '' }, list: [], total: 0, show: false,
      form: { id: null, title: '', content: '' },
      rules: { title: [{ required: true, message: '请输入标题' }], content: [{ required: true, message: '请输入内容' }] }
    }
  },
  mounted() { this.load() },
  methods: {
    load(p) { if (p) this.q.current = p; noticeApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 }) },
    open(row) { this.form = row ? { ...row } : { id: null, title: '', content: '' }; this.show = true; this.$nextTick(() => this.$refs.f && this.$refs.f.clearValidate()) },
    save() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        const fn = this.form.id ? noticeApi.update : noticeApi.add
        fn(this.form).then(() => { this.$message.success('已保存'); this.show = false; this.load() })
      })
    },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => noticeApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })).catch(()=>{})
    }
  }
}
</script>
