<template>
  <div class="card">
    <div class="page-header">求助记录管理</div>
    <div class="toolbar">
      <span class="spacer"/>
      <el-button type="success" icon="el-icon-plus" @click="open()">新增记录</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="requestTitle" label="关联求助"/>
      <el-table-column prop="stationName" label="救助站" width="160"/>
      <el-table-column prop="handler" label="处理人" width="120"/>
      <el-table-column prop="content" label="处理内容" show-overflow-tooltip/>
      <el-table-column prop="result" label="处理结果" width="140"/>
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
        <el-form-item label="关联求助" prop="requestId">
          <el-select v-model="form.requestId" filterable style="width:100%;">
            <el-option v-for="r in requests" :key="r.id" :label="'#'+r.id+' '+r.title" :value="r.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="处理人"><el-input v-model="form.handler"/></el-form-item>
        <el-form-item label="处理内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4"/></el-form-item>
        <el-form-item label="处理结果"><el-input v-model="form.result"/></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { helpRecordApi, helpApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 10 }, list: [], total: 0, show: false, requests: [],
      form: { id: null, requestId: null, content: '', handler: '', result: '' },
      rules: { requestId: [{ required: true, message: '请选择求助' }], content: [{ required: true, message: '请输入处理内容' }] }
    }
  },
  mounted() { helpApi.page({ current: 1, size: 200 }).then(r => { this.requests = r.data.records || [] }); this.load() },
  methods: {
    load(p) { if (p) this.q.current = p; helpRecordApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 }) },
    open(row) { this.form = row ? { ...row } : { id: null, requestId: null, content: '', handler: '', result: '' }; this.show = true; this.$nextTick(() => this.$refs.f && this.$refs.f.clearValidate()) },
    save() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        const fn = this.form.id ? helpRecordApi.update : helpRecordApi.add
        fn(this.form).then(() => { this.$message.success('已保存'); this.show = false; this.load() })
      })
    },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => helpRecordApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })).catch(()=>{})
    }
  }
}
</script>
