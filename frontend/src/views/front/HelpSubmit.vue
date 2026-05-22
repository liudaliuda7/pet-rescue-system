<template>
  <div class="card">
    <div class="page-header">提交求助信息</div>
    <el-form :model="form" :rules="rules" ref="f" label-width="100px" style="max-width:700px;">
      <el-form-item label="标题" prop="title"><el-input v-model="form.title"/></el-form-item>
      <el-form-item label="详细描述" prop="content"><el-input v-model="form.content" type="textarea" :rows="6"/></el-form-item>
      <el-form-item label="所在位置" prop="location"><el-input v-model="form.location"/></el-form-item>
      <el-form-item label="联系电话" prop="contact"><el-input v-model="form.contact"/></el-form-item>
      <el-form-item label="图片">
        <el-upload :action="uploadUrl" :headers="headers" :show-file-list="false" :on-success="onUp">
          <el-button icon="el-icon-upload">{{ form.image ? '重新上传' : '上传图片' }}</el-button>
        </el-upload>
        <img v-if="form.image" :src="form.image" style="margin-top:8px;max-width:200px;max-height:140px;border-radius:4px;"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">提交求助</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { helpApi, uploadUrl } from '@/api'
export default {
  data() {
    return {
      uploadUrl,
      headers: { Authorization: 'Bearer ' + (localStorage.getItem('token') || '') },
      loading: false,
      form: { title: '', content: '', location: '', contact: '', image: '' },
      rules: {
        title: [{ required: true, message: '请输入标题' }],
        content: [{ required: true, message: '请输入描述' }],
        location: [{ required: true, message: '请输入位置' }],
        contact: [{ required: true, message: '请输入联系电话' }]
      }
    }
  },
  methods: {
    onUp(r) { if (r.code === 200) { this.form.image = r.data.url; this.$message.success('上传成功') } },
    submit() {
      this.$refs.f.validate(ok => {
        if (!ok) return
        this.loading = true
        helpApi.add(this.form).then(() => {
          this.$message.success('提交成功')
          this.$router.push('/help')
        }).finally(() => { this.loading = false })
      })
    }
  }
}
</script>
