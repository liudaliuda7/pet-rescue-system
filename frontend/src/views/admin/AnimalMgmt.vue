<template>
  <div class="card">
    <div class="page-header">流浪动物管理</div>
    <div class="toolbar">
      <el-input v-model="q.name" placeholder="名字" style="width:160px;" clearable @keyup.enter.native="load(1)"/>
      <el-select v-model="q.typeId" clearable placeholder="种类" style="width:120px;" @change="load(1)">
        <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id"/>
      </el-select>
      <el-select v-model="q.status" clearable placeholder="状态" style="width:120px;" @change="load(1)">
        <el-option label="待领养" value="available"/><el-option label="已领养" value="adopted"/><el-option label="已认领" value="claimed"/><el-option label="治疗中" value="treatment"/>
      </el-select>
      <el-button type="primary" @click="load(1)">搜索</el-button>
      <span class="spacer"/>
      <el-button type="success" icon="el-icon-plus" @click="open()">新增</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column label="封面" width="80">
        <template slot-scope="s"><img v-if="s.row.image" :src="s.row.image" class="thumb"/></template>
      </el-table-column>
      <el-table-column prop="name" label="名字"/>
      <el-table-column prop="typeName" label="种类" width="80"/>
      <el-table-column prop="gender" label="性别" width="60"/>
      <el-table-column prop="age" label="年龄" width="80"/>
      <el-table-column prop="stationName" label="救助站"/>
      <el-table-column prop="healthStatus" label="健康"/>
      <el-table-column label="健康记录" width="100" align="center">
        <template slot-scope="s">
          <el-button type="text" @click="viewHealth(s.row)">{{ s.row.healthRecordCount || 0 }}</el-button>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="s"><el-tag size="mini" :type="s.row.status==='available'?'success':s.row.status==='adopted'?'info':s.row.status==='claimed'?'':'warning'">{{ statusText(s.row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="s">
          <el-button size="mini" @click="open(s.row)">编辑</el-button>
          <el-button size="mini" type="warning" @click="viewClaim(s.row)">认领审核</el-button>
          <el-button size="mini" type="danger" @click="del(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background layout="total,prev,pager,next" :total="total" :page-size="q.size" :current-page.sync="q.current" @current-change="load()" style="margin-top:16px;text-align:right;"/>

    <el-dialog :title="form.id?'编辑':'新增'" :visible.sync="show" width="600px">
      <el-form :model="form" :rules="rules" ref="f" label-width="100px">
        <el-form-item label="名字" prop="name"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="种类" prop="typeId">
          <el-select v-model="form.typeId" style="width:100%;"><el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id"/></el-select>
        </el-form-item>
        <el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio label="公"/><el-radio label="母"/></el-radio-group></el-form-item>
        <el-form-item label="年龄"><el-input v-model="form.age"/></el-form-item>
        <el-form-item label="毛色"><el-input v-model="form.color"/></el-form-item>
        <el-form-item label="健康"><el-input v-model="form.healthStatus"/></el-form-item>
        <el-form-item label="所属救助站" v-if="role==='admin'">
          <el-select v-model="form.stationId" style="width:100%;" clearable><el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.id"/></el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio label="available">待领养</el-radio><el-radio label="adopted">已领养</el-radio><el-radio label="claimed">已认领</el-radio><el-radio label="treatment">治疗中</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="封面图">
          <el-upload :action="uploadUrl" :headers="headers" :show-file-list="false" :on-success="onUp">
            <el-button icon="el-icon-upload">上传图片</el-button>
          </el-upload>
          <el-input v-model="form.image" placeholder="或填入图片URL" style="margin-top:6px;"/>
          <img v-if="form.image" :src="form.image" style="margin-top:8px;max-width:160px;max-height:120px;border-radius:4px;"/>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3"/></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span>
    </el-dialog>

    <el-dialog title="认领审核" :visible.sync="showClaim" width="600px">
      <div v-if="claimList.length === 0" style="text-align:center;color:#909399;padding:20px;">暂无认领申请</div>
      <div v-for="claim in claimList" :key="claim.id" style="border:1px solid #ebeef5;border-radius:6px;padding:16px;margin-bottom:12px;">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="申请人">{{ claim.userName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ claim.userPhone || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ claim.createTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="mini" :type="claim.status==='pending'?'warning':claim.status==='approved'?'success':'danger'">{{ claimStatusText(claim.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="证明描述" :span="2">{{ claim.proofDesc }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="claim.proofImages" style="margin-top:10px;">
          <div style="font-size:13px;color:#606266;margin-bottom:6px;">证明图片：</div>
          <div style="display:flex;flex-wrap:wrap;gap:8px;">
            <el-image v-for="(img, idx) in claim.proofImages.split(',')" :key="idx" :src="img" :preview-src-list="claim.proofImages.split(',')" style="width:80px;height:80px;border-radius:4px;" fit="cover"/>
          </div>
        </div>
        <div v-if="claim.remark" style="margin-top:8px;font-size:13px;color:#909399;">备注：{{ claim.remark }}</div>
        <div v-if="claim.status==='pending'" style="margin-top:12px;text-align:right;">
          <el-button size="mini" type="success" @click="auditClaim(claim, 'approved')">通过</el-button>
          <el-button size="mini" type="danger" @click="auditClaim(claim, 'rejected')">拒绝</el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog title="审核确认" :visible.sync="showClaimAudit" width="400px">
      <el-form label-width="60px">
        <el-form-item label="备注"><el-input v-model="claimAuditForm.remark" type="textarea" :rows="3" placeholder="可填写审核备注"/></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showClaimAudit=false">取消</el-button>
        <el-button type="primary" :loading="claimAuditing" @click="doClaimAudit">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { animalApi, animalTypeApi, stationApi, animalClaimApi, uploadUrl } from '@/api'
import { getUser } from '@/utils/auth'
export default {
  data() {
    return {
      uploadUrl, headers: { Authorization: 'Bearer ' + (localStorage.getItem('token') || '') },
      q: { current: 1, size: 10, name: '', typeId: undefined, status: '' },
      list: [], total: 0, types: [], stations: [], show: false, role: getUser().role,
      form: { id: null, name: '', typeId: null, gender: '公', age: '', color: '', healthStatus: '健康', stationId: null, status: 'available', image: '', description: '' },
      rules: { name: [{ required: true, message: '请输入名字' }], typeId: [{ required: true, message: '请选择种类' }] },
      showClaim: false, claimList: [],
      showClaimAudit: false, claimAuditing: false,
      claimAuditForm: { id: null, status: '', remark: '' }
    }
  },
  mounted() {
    animalTypeApi.list().then(r => { this.types = r.data || [] })
    stationApi.list().then(r => { this.stations = r.data || [] })
    this.load()
  },
  methods: {
    statusText(s) { return { available: '待领养', adopted: '已领养', claimed: '已认领', treatment: '治疗中' }[s] || s },
    onUp(r) { if (r.code === 200) { this.form.image = r.data.url; this.$message.success('上传成功') } },
    load(p) { if (p) this.q.current = p; animalApi.page(this.q).then(r => { this.list = r.data.records || []; this.total = r.data.total || 0 }) },
    open(row) {
      this.form = row ? { ...row } : { id: null, name: '', typeId: null, gender: '公', age: '', color: '', healthStatus: '健康', stationId: null, status: 'available', image: '', description: '' }
      this.show = true; this.$nextTick(() => this.$refs.f && this.$refs.f.clearValidate())
    },
    save() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        const fn = this.form.id ? animalApi.update : animalApi.add
        fn(this.form).then(() => { this.$message.success('已保存'); this.show = false; this.load() })
      })
    },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => animalApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })).catch(()=>{})
    },
    viewHealth(row) {
      const prefix = this.$route.path.startsWith('/station') ? '/station' : '/admin'
      this.$router.push({ path: prefix + '/health', query: { animalId: row.id } })
    },
    claimStatusText(s) { return { pending: '待审核', approved: '已通过', rejected: '已拒绝' }[s] || s },
    viewClaim(row) {
      animalClaimApi.page({ current: 1, size: 50, animalId: row.id }).then(r => {
        this.claimList = r.data.records || []
        this.showClaim = true
      })
    },
    auditClaim(claim, status) {
      this.claimAuditForm = { id: claim.id, status, remark: '' }
      this.showClaimAudit = true
    },
    doClaimAudit() {
      this.claimAuditing = true
      animalClaimApi.audit(this.claimAuditForm).then(() => {
        this.$message.success('审核完成')
        this.showClaimAudit = false
        this.showClaim = false
        this.load()
      }).finally(() => { this.claimAuditing = false })
    }
  }
}
</script>
