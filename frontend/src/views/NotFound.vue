<template>
  <div style="text-align:center;padding:80px 20px;">
    <h1 style="font-size:80px;margin:0;color:#409EFF;">404</h1>
    <p>抱歉，您访问的页面不存在</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>
<script>export default { name: 'NotFound' }</script>
