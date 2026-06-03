<template>
  <div class="card">
    <div class="page-header">认领审核管理</div>
    <div class="toolbar">
      <el-select v-model="q.status" clearable placeholder="状态筛选" style="width:140px;" @change="load(1)">
        <el-option label="待审核" value="pending"/>
        <el-option label="已通过" value="approved"/>
        <el-option label="已拒绝" value="rejected"/>
      </el-select>
      <el-button type="primary" @click="load(1)">搜索</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column label="动物" width="120">
        <template slot-scope="s">
          <div style="display:flex;align-items:center;">
            <img v-if="s.row.animalImage" :src="s.row.animalImage" class="thumb"/>
            <span style="margin-left:6px;">{{ s.row.animalName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="申请人"/>
      <el-table-column prop="userPhone" label="联系电话" width="120"/>
      <el-table-column label="证明描述" show-overflow-tooltip>
        <template slot-scope="s">{{ s.row.proofDesc }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="s">
          <el-tag size="mini" :type="s.row.status==='pending'?'warning':s.row.status==='approved'?'success':'danger'">
            {{ statusText(s.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="160"/>
      <el-table-column label="操作" width="180">
        <template slot-scope="s">
          <el-button size="mini" @click="viewDetail(s.row)">详情</el-button>
          <el-button size="mini" type="success" v-if="s.row.status==='pending'" @click="audit(s.row, 'approved')">通过</el-button>
          <el-button size="mini" type="danger" v-if="s.row.status==='pending'" @click="audit(s.row, 'rejected')">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="margin-top:16px;text-align:right;"/>

    <el-dialog title="认领详情" :visible.sync="showDetail" width="600px">
      <div v-if="detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="动物名称">{{ detail.animalName }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ detail.userName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.userPhone || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ detail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="mini" :type="detail.status==='pending'?'warning':detail.status==='approved'?'success':'danger'">{{ statusText(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="证明描述">{{ detail.proofDesc }}</el-descriptions-item>
          <el-descriptions-item label="备注" v-if="detail.remark">{{ detail.remark }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detail.proofImages && detail.proofImages.length > 0" style="margin-top:16px;">
          <div style="font-weight:600;margin-bottom:8px;">证明图片：</div>
          <div style="display:flex;flex-wrap:wrap;gap:8px;">
            <el-image v-for="(img, idx) in detail.proofImages.split(',').filter(x => x)" :key="idx"
              :src="img" :preview-src-list="detail.proofImages.split(',').filter(x => x)"
              style="width:120px;height:120px;border-radius:4px;" fit="cover"/>
          </div>
        </div>
      </div>
      <span slot="footer" v-if="detail && detail.status==='pending'">
        <el-button type="success" @click="audit(detail, 'approved')">审核通过</el-button>
        <el-button type="danger" @click="audit(detail, 'rejected')">审核拒绝</el-button>
      </span>
    </el-dialog>

    <el-dialog title="审核" :visible.sync="showAudit" width="400px">
      <el-form label-width="60px">
        <el-form-item label="备注"><el-input v-model="auditForm.remark" type="textarea" :rows="3" placeholder="可填写审核备注"/></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showAudit=false">取消</el-button>
        <el-button type="primary" :loading="auditing" @click="doAudit">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { animalClaimApi } from '@/api'
export default {
  data() {
    return {
      q: { current: 1, size: 10, status: '' },
      list: [], total: 0,
      showDetail: false, detail: null,
      showAudit: false, auditing: false,
      auditForm: { id: null, status: '', remark: '' }
    }
  },
  mounted() { this.load() },
  methods: {
    statusText(s) { return { pending: '待审核', approved: '已通过', rejected: '已拒绝' }[s] || s },
    load(p) {
      if (p) this.q.current = p
      animalClaimApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 })
    },
    viewDetail(row) {
      animalClaimApi.get(row.id).then(r => {
        this.detail = r.data
        this.showDetail = true
      })
    },
    audit(row, status) {
      this.auditForm = { id: row.id, status, remark: '' }
      this.showAudit = true
    },
    doAudit() {
      this.auditing = true
      animalClaimApi.audit(this.auditForm).then(() => {
        this.$message.success('审核完成')
        this.showAudit = false
        this.showDetail = false
        this.load()
      }).finally(() => { this.auditing = false })
    }
  }
}
</script>

<style scoped>
.thumb { width: 36px; height: 36px; border-radius: 4px; object-fit: cover; }
</style>
