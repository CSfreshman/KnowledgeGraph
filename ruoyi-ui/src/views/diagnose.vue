<template>
  <div>

    <!--      头部-->
    <div slot="header" class="card-header">
      <span style="margin-right: 10px">抑郁症辅助诊断</span>
      <el-tooltip content="这里是一段关于 抑郁症辅助诊断 的描述" effect="dark" placement="bottom">
        <el-button icon="el-icon-thumb" circle size="small"></el-button>
      </el-tooltip>
      <el-divider></el-divider>
    </div>

    <div v-show="showForm">
      <el-form :model="formData">

        <el-form-item label="输入症状描述" label-width="100px">
          <el-input v-model="formData.desc" style="width: 500px" placeholder="请输入您的症状描述"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button>提交</el-button>
        </el-form-item>

      </el-form>

      <el-divider></el-divider>
    </div>

    <el-button @click="showForm = !showForm">{{ showForm ? '隐藏表单' : '显示表单' }}</el-button>
  </div>
</template>

<script>
export default {
  name: "diagnose",
  data() {
    return {
      formData: {
        desc: ''
      },

      showForm: true
    }
  },
  created() {
  },
  methods: {

  }
}
</script>

<style scoped>
  .card-header {
    font-size: 18px;
    font-weight: bold;
  }
</style>
