<template>
  <div id="main">
    <el-row>
      <el-col :span="24">
        <el-card id="up">
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
      <el-col :span="0">
<!--        <el-card>-->
<!--          <div slot="header" class="card-header">-->
<!--            <span>词云</span>-->
<!--          </div>-->
<!--          <div>-->
<!--            <blockquote>-->
<!--              <p>系统保留了用户的操作记录，对操作主体生成词云，帮助用户更直观的看到大家关心的内容。</p>-->
<!--            </blockquote>-->
<!--          </div>-->
<!--          <el-button @click="dialogVisible = true" size="small">点击查看词云</el-button>-->

<!--        </el-card>-->

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

          <div>
            <p>图谱检索用于对图谱可以进行单一节点、关系查询，以及对图谱进行本体联合查询</p>
            <br>
          </div>

          <div>
            <el-table
              :data="data.search"
              style="width: 100%">
              <el-table-column
                prop="name"
                label="名称"
              ></el-table-column>
              <el-table-column
                prop="count"
                label="次数"
              ></el-table-column>
            </el-table>
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

          <div>
            <p>网络分析用于对知识图谱构成的有向图进行路径分析、中心多度探寻等。</p>
            <br>
          </div>

          <div>
            <el-table
              :data="data.analysis"
              style="width: 100%">
              <el-table-column
                prop="name"
                label="名称"
              ></el-table-column>
              <el-table-column
                prop="count"
                label="次数"
              ></el-table-column>
            </el-table>
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
          <div>
            <p>图谱计算主要用于对图谱中的节点进行中心度计算，相似度计算等</p>
            <br>
          </div>
          <div>
            <el-table
              :data="data.computation"
              style="width: 100%">
              <el-table-column
                prop="name"
                label="名称"
              ></el-table-column>
              <el-table-column
                prop="count"
                label="次数"
              ></el-table-column>
            </el-table>
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
          <div>
            <p>辅助诊断通过输入患者的症状描述、年龄、性别等信息，来分析最匹配的抑郁症类型，并给出相应的治疗建议。</p>
          </div>
          <div>
            <el-table
              :data="data.diagnose"
              style="width: 100%">
              <el-table-column
                prop="name"
                label="名称"
              ></el-table-column>
              <el-table-column
                prop="count"
                label="次数"
              ></el-table-column>
            </el-table>
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
import {extraHotSearch} from "@/api/extra";

export default {
  name: "Index",
  data() {
    return {
      dialogVisible: false,
      data: {
        search: [],
        computation: [],
        analysis: [],
        diagnose: []
      }
    };
  },
  methods: {

  },
  mounted(){
    extraHotSearch().then(resp=>{

      // 获取 key 为 "1" 的数组
      const array1 = resp["1"];
      // 遍历数组中的前五个对象
      for (let i = 0; i < Math.min(5, array1.length); i++) {
        const obj = array1[i];
        for (const prop in obj) {
          if (obj.hasOwnProperty(prop)) {
            // 将键值对添加到结果数组中
            this.data.search.push({ name: prop, count: obj[prop] });
          }
        }
      }

      const array2 = resp["2"];
      // 遍历数组中的前五个对象
      for (let i = 0; i < Math.min(5, array2.length); i++) {
        const obj = array2[i];
        for (const prop in obj) {
          if (obj.hasOwnProperty(prop)) {
            // 将键值对添加到结果数组中
            this.data.analysis.push({ name: prop, count: obj[prop] });
          }
        }
      }

      const array3 = resp["3"];
      // 遍历数组中的前五个对象
      for (let i = 0; i < Math.min(5, array3.length); i++) {
        const obj = array3[i];
        for (const prop in obj) {
          if (obj.hasOwnProperty(prop)) {
            // 将键值对添加到结果数组中
            this.data.computation.push({ name: prop, count: obj[prop] });
          }
        }
      }

      const array4 = resp["4"];
      // 遍历数组中的前五个对象
      for (let i = 0; i < Math.min(5, array4.length); i++) {
        const obj = array4[i];
        for (const prop in obj) {
          if (obj.hasOwnProperty(prop)) {
            // 将键值对添加到结果数组中
            this.data.diagnose.push({ name: prop, count: obj[prop] });
          }
        }
      }

      console.log(this.data)

    })
  }
};
</script>

<style scoped lang="scss">

#main {
  background-color: #FFFFFF;
  width:100vw;
  height:100vh;
}

#up {
  height:26vh;
}


.card-header {
  font-size: 18px;
  font-weight: bold;
}

</style>

