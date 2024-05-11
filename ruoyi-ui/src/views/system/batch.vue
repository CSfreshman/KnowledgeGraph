<template>
  <div>
    <el-row>
      <el-col :span="8">
        <el-card>
          <el-select v-model="fromNodeClassId" placeholder="选择起点类型" @change="changeNodeClass(1)">
            <el-option
              v-for="item in nodeClassList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
          <div><el-row><el-input v-model="fromNodeClass.name" placeholder="类型名称" disabled></el-input></el-row></div>
          <div><el-row><el-input v-model="fromNodeClass.id" placeholder="类型ID" disabled></el-input></el-row></div>

        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <el-select v-model="toNodeClassId" placeholder="选择终点类型" @change="changeNodeClass(2)">
            <el-option
              v-for="item in nodeClassList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
          <div><el-row><el-input v-model="toNodeClass.name" placeholder="类型名称" disabled></el-input></el-row></div>
          <div><el-row><el-input v-model="toNodeClass.id" placeholder="类型ID" disabled></el-input></el-row></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <el-select v-model="edgeClassId" placeholder="选择关系类型" @change="changeEdgeClass">
            <el-option
              v-for="item in edgeClassList"
              :key="item.id"
              :label="item.label"
              :value="item.id">
            </el-option>
          </el-select>
          <div><el-row><el-input v-model="edgeClass.label" placeholder="类型名称" disabled></el-input></el-row></div>
          <div><el-row><el-input v-model="edgeClass.id" placeholder="类型ID" disabled></el-input></el-row></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row style="margin-top: 20px">
      <div>
        <el-input v-model="jsonData" type="textarea" :rows="15" placeholder="文本输入框" style="width: 80%; margin-left: 10%"></el-input>
      </div>
    </el-row>
    <el-row style="margin-top: 20px">
      <div>
        <el-button @click="importData" style="margin-left: 50%">导入</el-button>
      </div>
    </el-row>
  </div>
</template>

<script>
import {listClass as listNodeClass} from "@/api/mange/class/node";
import {listClass as listEdgeClass} from "@/api/mange/class/edge";
import {importData} from "@/api/importData";

export default {
  name: "batch",
  data() {
    return {
      jsonData: '',
      nodeClassList: '',
      edgeClassList: '',
      allEdgeClassList: '',

      fromNodeClassId: '',
      toNodeClassId: '',
      edgeClassId: '',

      fromNodeClass: '',
      toNodeClass: '',
      edgeClass: '',

      isValidJson: false
    }
  },
  methods: {
    importData() {
      this.checkJsonFormat();
      var fromData = {
        fromNodeClass: this.fromNodeClass,
        toNodeClass: this.toNodeClass,
        edgeClass: this.edgeClass,
        jsonData: this.jsonData
      }
      if(this.isValidJson){
        importData(fromData).then(resp=>{
          this.$modal.msgSuccess("完成");
        })
      }

    },
    checkJsonFormat() {
      try {
        JSON.parse(this.jsonData);
        this.isValidJson = true;
      } catch (error) {
        this.isValidJson = false;
        alert("JSON数据格式不正确," + error);
      }
    },
    changeNodeClass(index){
      var nodeClassId = index === 1 ? this.fromNodeClassId : this.toNodeClassId;
      var nodeClass = this.nodeClassList.find(it=>it.id === nodeClassId);
      if(index == 1){
        this.fromNodeClass = nodeClass;
      }else{
        this.toNodeClass = nodeClass;
        // this.edgeClassList.filter(it=>it.fromNodeId === this.fromNodeClassId && it.toNodeId === this.toNodeClassId);
      }
    },
    changeEdgeClass(){
      this.edgeClass = this.edgeClassList.find(it=>it.id === this.edgeClassId)
    },
    getNodeList() {
      var queryParams = {valid: 1}
      listNodeClass(queryParams).then(response => {
        this.nodeClassList = response.rows;
      });
    },
    getEdgeList() {
      var queryParams = {valid: 1}
      listEdgeClass(queryParams).then(response => {
        this.edgeClassList = response.rows;
      });
    },
  },

  created() {
    this.getNodeList();
    this.getEdgeList();
  }
}
</script>

<style scoped>

</style>
