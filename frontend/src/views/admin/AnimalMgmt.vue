<template>
  <div class="card">
    <div class="page-header">流浪动物管理</div>
    <div class="toolbar">
      <el-input v-model="q.name" placeholder="名字" style="width:160px;" clearable @keyup.enter.native="load(1)"/>
      <el-select v-model="q.typeId" clearable placeholder="种类" style="width:120px;" @change="load(1)">
        <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id"/>
      </el-select>
      <el-select v-model="q.status" clearable placeholder="状态" style="width:120px;" @change="load(1)">
        <el-option label="待领养" value="available"/><el-option label="已领养" value="adopted"/><el-option label="治疗中" value="treatment"/>
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
      <el-table-column label="健康记录数" width="110" align="center">
        <template slot-scope="s">
          <el-button type="text" @click="viewHealth(s.row.id, s.row.name)">
            {{ s.row.healthRecordCount || 0 }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="s"><el-tag size="mini" :type="s.row.status==='available'?'success':s.row.status==='adopted'?'info':'warning'">{{ statusText(s.row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="s">
          <el-button size="mini" @click="open(s.row)">编辑</el-button>
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
          <el-radio-group v-model="form.status"><el-radio label="available">待领养</el-radio><el-radio label="adopted">已领养</el-radio><el-radio label="treatment">治疗中</el-radio></el-radio-group>
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
  </div>
</template>

<script>
import { animalApi, animalTypeApi, stationApi, uploadUrl } from '@/api'
import { getUser } from '@/utils/auth'
export default {
  data() {
    return {
      uploadUrl, headers: { Authorization: 'Bearer ' + (localStorage.getItem('token') || '') },
      q: { current: 1, size: 10, name: '', typeId: undefined, status: '' },
      list: [], total: 0, types: [], stations: [], show: false, role: getUser().role,
      form: { id: null, name: '', typeId: null, gender: '公', age: '', color: '', healthStatus: '健康', stationId: null, status: 'available', image: '', description: '' },
      rules: { name: [{ required: true, message: '请输入名字' }], typeId: [{ required: true, message: '请选择种类' }] }
    }
  },
  mounted() {
    animalTypeApi.list().then(r => { this.types = r.data || [] })
    stationApi.list().then(r => { this.stations = r.data || [] })
    this.load()
  },
  methods: {
    statusText(s) { return { available: '待领养', adopted: '已领养', treatment: '治疗中' }[s] || s },
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
    viewHealth(animalId, name) {
      const prefix = this.$route.path.startsWith('/station') ? '/station' : '/admin'
      this.$router.push({ path: `${prefix}/health`, query: { animalId, name } })
    },
    del(row) {
      this.$confirm('确定删除？', '提示', { type: 'warning' }).then(() => animalApi.del(row.id).then(() => { this.$message.success('已删除'); this.load() })).catch(()=>{})
    }
  }
}
</script>
