<template>
  <div>

    <!--      头部-->
    <div slot="header" class="card-header">
      <span style="margin-right: 10px">抑郁症辅助诊断</span>
      <el-tooltip content="这里是一段关于 抑郁症辅助诊断 的描述" effect="dark" placement="bottom">
        <el-button icon="el-icon-thumb" circle size="small"></el-button>
      </el-tooltip>

      <el-button style="margin-left: 50px" @click="showForm = !showForm">{{ showForm ? '隐藏表单' : '显示表单' }}</el-button>
      <el-divider></el-divider>
    </div>

<!--    表单-->
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

          <el-select v-model="selectedSex" placeholder="选择性别" @change="selectSex" size="mini" style="margin-left: 100px">
            <el-option
              v-for="item in sex"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>

          <el-select v-model="selectedAge" placeholder="选择年龄" @change="selectAge" size="mini" style="margin-left: 100px">
            <el-option
              v-for="item in age"
              :key="item.id"
              :label="item.name"
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
          style="margin-left: 10px; margin-top: 5px"
        >
          {{item.label}}
        </el-tag>
      </el-row>

      <el-divider></el-divider>
    </div>

<!--    展示部分-->
    <div v-if="show">
      <el-row>
        <el-col :span="8" style="background-color: #FFFFFF; height: 100vh;">
          <div class="card-header">
            <span style="margin-right: 10px">相似疾病列表</span>
          </div>

          <el-table
            ref="singleTable"
            :data="similarNodes"
            highlight-current-row
            @current-change="handleCurrentChange"
            style="width: 100%"
            >
            <el-table-column
              type="index"
              width="50">
            </el-table-column>
            <el-table-column
              property="id"
              label="节点id"
              width="100">
            </el-table-column>
            <el-table-column
              property="label"
              label="疾病名称"
              width="120">
            </el-table-column>
            <el-table-column
              property="name"
              label="匹配症状"
              width="120">
            </el-table-column>

          </el-table>

        </el-col>
        <el-col :span="16">
<!--          疾病信息展示区域-->
          <el-row>
            <div class="card-header">
              <span style="margin-right: 10px">疾病信息</span>
            </div>
          </el-row>

          <el-row>
            <div class="card-header">
              <span style="margin-right: 10px">疾病治疗方式</span>
            </div>
          </el-row>

          <el-row>
            <div class="card-header">
              <span style="margin-right: 10px">疾病预防方式</span>
            </div>
          </el-row>

        </el-col>
      </el-row>
    </div>

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
      sex: [
        {id:1,name:'男'},
        {id:2,name:'女'}
      ],
      age: [

      ],
      selectedSex: '',
      selectedAge: '',

      similarNodes: [],
      show: false,
      currentRow: null


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

    var up = 60;
    for (let i = 1; i <= up; i++) {
      this.age.push({id: i, name: i + "岁"})
    }
    this.age.push({id:up + 1,name:"大于"+up+"岁"})
  },
  methods: {

    setCurrent(row) {
      this.$refs.singleTable.setCurrentRow(row);
    },
    handleCurrentChange(val) {
      this.currentRow = val;
    },

    submitSymptomsDesc(){
      submitSymptomsDesc({symptomsDesc:this.formData.symptomsDesc}).then(resp=>{
        this.matchNodes = resp.graph.nodes;
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
        console.log(resp.list)

        var that = this

        // 遍历数组并处理每个条目
        resp.list.forEach(function(entry) {
          // 提取键和值
          var nodeInfo = Object.keys(entry)[0]; // 获取键
          var value = entry[nodeInfo]; // 获取值

          // 解析键以获取 Neo4jNode 的信息
          var regexResult = /Neo4jNode\(id=(\d+), label=(.*?),/.exec(nodeInfo);
          var nodeId = regexResult[1];
          var label = regexResult[2];

          // 输出解析结果
          console.log("Node ID:", nodeId);
          console.log("Label:", label);
          console.log("Value:", value);
          console.log("--------------------");

          that.similarNodes.push({
            id: nodeId,
            label: label,
            similarity: value
          })
        });

        this.show = true;
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
