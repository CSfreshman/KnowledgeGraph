<template>
  <div>
    <el-row>
      <el-col :span="12">
        <el-card>
          <div slot="header" class="card-header">
            <span>系统介绍</span>
          </div>

          <div>
            <blockquote>
              <p>这是一个面向抑郁症知识图谱的可视化分析系统，包括了图谱构建、图谱检索、图谱计算、网络分析，辅助诊断等多种功能。
                另外包含了历史记录、数据统计等功能。</p>
            </blockquote>
          </div>

        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header" class="card-header">
            <span>词云</span>
          </div>
          <el-button @click="dialogVisible = true">点击查看词云</el-button>

        </el-card>

      </el-col>

    </el-row>

    <el-row>
      <el-col :span="6">
        <el-card>
          <div slot="header" class="card-header">
            <span>图谱检索</span>
            <el-link style="margin-left: 30px" @click="()=>{
              this.$router.push({
                path: '/graphSelect',
              })
            }" type="primary">立即前往</el-link>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div slot="header" class="card-header">
            <span>网络分析</span>
            <el-link style="margin-left: 30px" @click="()=>{
              this.$router.push({
                path: '/networkAnalysis',
              })
            }" type="primary">立即前往</el-link>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div slot="header" class="card-header">
            <span>图谱计算</span>
            <el-link style="margin-left: 30px" @click="()=>{
              this.$router.push({
                path: '/graphComputation',
              })
            }" type="primary">立即前往</el-link>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div slot="header" class="card-header">
            <span>辅助诊断</span>
            <el-link style="margin-left: 30px" @click="()=>{
              this.$router.push({
                path: '/diagnose',
              })
            }" type="primary">立即前往</el-link>
          </div>
        </el-card>
      </el-col>
    </el-row>


    <div>
      <el-dialog
        title="词云"
        :visible.sync="dialogVisible"
        width="500px"
      >
        <el-image
          :src="require('@/views/3.png')">

        </el-image>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      dialogVisible: false,
    };
  },
  methods: {

  }
};
</script>

<style scoped lang="scss">

.card-header {
  font-size: 18px;
  font-weight: bold;
}

</style>

