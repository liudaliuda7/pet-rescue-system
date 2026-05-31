<template>
  <div class="card">
    <div class="page-header">宠物领养管理</div>
    <div class="toolbar">
      <el-select v-model="q.status" clearable placeholder="状态" style="width:140px;" @change="load(1)">
        <el-option label="审核中" value="pending"/><el-option label="已通过" value="approved"/><el-option label="已拒绝" value="rejected"/>
      </el-select>
      <el-button type="primary" @click="load(1)">搜索</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column label="动物" width="80">
        <template slot-scope="s"><img v-if="s.row.animalImage" :src="s.row.animalImage" class="thumb"/></template>
      </el-table-column>
      <el-table-column prop="animalName" label="名字" width="100"/>
      <el-table-column prop="userName" label="申请人" width="100"/>
      <el-table-column prop="contact" label="联系" width="120"/>
      <el-table-column prop="address" label="地址"/>
      <el-table-column prop="reason" label="申请理由" show-overflow-tooltip/>
      <el-table-column label="回访记录数" width="110">
        <template slot-scope="s">
          <el-button type="text" @click="goVisit(s.row)">{{ s.row.visitCount || 0 }} 条</el-button>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="s"><el-tag size="mini" :type="t(s.row.status)">{{ st(s.row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170"/>
      <el-table-column label="操作" width="200">
        <template slot-scope="s">
          <el-button size="mini" type="primary" @click="open(s.row)" v-if="s.row.status==='pending'">审核</el-button>
          <el-button size="mini" type="danger" @click="del(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="margin-top:16px;text-align:right;"/>

    <el-dialog title="审核领养申请" :visible.sync="show" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="审核结果"><el-radio-group v-model="form.status"><el-radio label="approved">通过</el-radio><el-radio label="rejected">拒绝</el-radio></el-radio-group></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3"/></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">提交</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { adoptionApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 10, status: '' },
      list: [], total: 0, show: false,
      form: { id: null, status: 'approved', remark: '' }
    }
  },
  mounted() { this.load() },
  methods: {
    st(s) { return { pending: '审核中', approved: '已通过', rejected: '已拒绝' }[s] || s },
    t(s) { return { pending: 'warning', approved: 'success', rejected: 'danger' }[s] || '' },
    load(p) { if (p) this.q.current = p; adoptionApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 }) },
    open(row) { this.form = { id: row.id, status: 'approved', remark: '' }; this.show = true },
    save() { adoptionApi.audit(this.form).then(() => { this.$message.success('审核完成'); this.show = false; this.load() }) },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => adoptionApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })).catch(()=>{})
    },
    goVisit(row) {
      const base = this.$route.path.startsWith('/station') ? '/station' : '/admin'
      this.$router.push({ path: base + '/visit', query: { adoptionId: row.id } })
    }
  }
}
</script>
