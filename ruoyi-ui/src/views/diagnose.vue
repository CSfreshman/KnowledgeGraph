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
          <el-input v-model="formData.symptomsDesc" style="width: 500px" placeholder="请输入您的症状描述"></el-input>

          <el-button @click="submitSymptomsDesc" style="margin-left: 100px;">提交</el-button>
        </el-form-item>

        <el-form-item label="选择相似症状" label-width="100px">
          <el-select v-model="selectedNodeId" placeholder="选择相似症状" @change="selectNode" size="mini">
            <el-option
              v-for="item in allNodes"
              :key="item.id"
              :label="item.label == null ? item.name : item.label"
              :value="item.id"
            >
            </el-option>
          </el-select>

          <el-button @click="executeDiagnose" style="margin-left: 100px;">开始分析</el-button>
        </el-form-item>
      </el-form>

      <el-row>
        <el-tag
          v-for="item in selectedNodes"
          :key="item.id"
          closable
          @close="handleTagClose(item)"
          style="margin-left: 10px;"
        >
          {{item.label}}
        </el-tag>
      </el-row>

      <el-divider></el-divider>
    </div>

    <el-button @click="showForm = !showForm">{{ showForm ? '隐藏表单' : '显示表单' }}</el-button>
  </div>
</template>

<script>
import {executeDiagnose, submitSymptomsDesc} from "@/api/extra";
import {graphSelect} from "@/api/graph";

export default {
  name: "diagnose",
  data() {
    return {
      formData: {
        symptomsDesc: ''
      },
      matchNodes: [],
      allNodes: [],
      selectedNodeId: '',
      showForm: true,
      selectedNodes: [],
    }
  },
  created() {
    var queryAllNodeParam = {
      selectIndex: 3,
      nodeClassList: [
        {id: "1770365867855843328",
          name: "症状"}
      ]
    }

    graphSelect(queryAllNodeParam).then(resp=>{
      this.allNodes = resp.nodes
    })
  },
  methods: {
    submitSymptomsDesc(){
      submitSymptomsDesc({symptomsDesc:this.formData.symptomsDesc}).then(resp=>{
        this.matchNodes = resp;
        console.log(this.matchNodes)

        this.matchNodes.forEach(it=>{
          this.selectedNodes.push(it)
        })
      })
    },

    // 取消选择节点类型
    handleTagClose(item) {
      const index = this.selectedNodes.findIndex(element => element.id === item.id);
      if (index !== -1) {
        this.selectedNodes.splice(index, 1);
      }
    },

    selectNode() {
      // 选择的节点
      var node = this.allNodes.find(it=>it.id == this.selectedNodeId);
      console.log(node);
      // 去重
      if(!this.selectedNodes.find(it=>it.id == node.id)){
        this.selectedNodes.push(node)
      }

      // 清空选择状态
      this.selectedNodeId = ''
    },

    executeDiagnose() {
      // 执行分析
      executeDiagnose({selectedNodeList:this.selectedNodes}).then(resp=>{

      })
    }
  }
}
</script>

<style scoped>
  .card-header {
    font-size: 18px;
    font-weight: bold;
  }
</style>
