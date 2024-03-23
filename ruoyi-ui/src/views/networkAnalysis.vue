<template>
  <div id="main-container">
    <div id="left-container">

<!--      头部-->
      <div>
        路径分析
        <el-divider></el-divider>
      </div>

<!--      设置分析实体对象-->
      <div>
        <el-row> 设置分析实体对象 </el-row>
        <el-row>
          <el-form>
            <el-form-item label="起点" label-width="50px">
              <el-input placeholder="输入实体名称" v-model="fromNode.name">
                <template #suffix>
                  <el-button style="border: none" icon="el-icon-search" size="small" @click="queryNode(true)"></el-button>
                </template>
              </el-input>
              <el-popover
                v-model="popoverVisibleFrom"
                placement="bottom-start"
                trigger="click">
                <el-select v-model="selectedItem" placeholder="请选择结果" @change="item => handleSelectChange(item, true)">
                  <el-option v-for="item in queryNodeList" :key="item.id" :label="item.name + '--' + item.label" :value="item"></el-option>
                </el-select>
<!--                <div slot="reference">{{ fromNode.name }}</div>-->
              </el-popover>
            </el-form-item>

            <el-form-item label="终点" label-width="50px">
              <el-input placeholder="输入实体名称" v-model="toNode.name">
                <template #suffix>
                  <el-button style="border: none" icon="el-icon-search" size="small" @click="queryNode(false)"></el-button>
                </template>
              </el-input>

              <el-popover
                v-model="popoverVisibleTo"
                placement="bottom-start"
                trigger="click">
                <el-select v-model="selectedItem" placeholder="请选择结果" @change="item => handleSelectChange(item, false)">
                  <el-option v-for="item in queryNodeList" :key="item.id" :label="item.name + '--' + item.label" :value="item"></el-option>
                </el-select>
                <!--                <div slot="reference">{{ fromNode.name }}</div>-->
              </el-popover>

            </el-form-item>
          </el-form>
        </el-row>
        <el-divider></el-divider>
      </div>

<!--      设置路径参数-->
      <div>
        <el-row> 设置分析路径参数 </el-row>
        <el-row>
          <el-radio v-model="isShortest" :label="true">最短路径</el-radio>
          <el-radio v-model="isShortest" :label="false">全路径</el-radio>
        </el-row>

        <el-row v-if="!isShortest">
          <el-form>
            <el-form-item label="最大度数" label-width="70px">
              <el-input placeholder="输入最大度数" v-model="maxDegree"></el-input>
            </el-form-item>
          </el-form>
        </el-row>
        <el-divider></el-divider>
      </div>

<!--      设置参与分析的关系类型-->
      <div>
        <el-row> 设置分析关系 </el-row>
        <div>
          <el-checkbox-group v-model="checkedIds">
            <el-checkbox v-for="item in edgeCheckBoxList" :key="item.id" :label="item" :disabled="!item.isEnable">{{ item.label }}</el-checkbox>
          </el-checkbox-group>
        </div>
        <el-divider></el-divider>
      </div>

      <div>
        <el-button size="medium" @click="analyse">分析</el-button>
        <el-button size="medium" @click="reset">重置</el-button>
      </div>
    </div>
    <div id="right-container">

    </div>
  </div>
</template>

<script>

import {getAll as getAllEdgeClass} from "@/api/mange/class/edge";
import {getAll as getAllNodeInstance} from "@/api/mange/instance/node"
import {pathAnalyse} from "@/api/graph";

export default {
  name: "networkAnalysis",
  data() {
    return {
      isShortest: true,
      allEdgeClassList: [],
      edgeCheckBoxList: [],
      checkedIds: [],
      maxDegree: '',
      fromNode: {name:''},
      toNode: {name: ''},
      queryNodeList: [],
      popoverVisibleFrom: false,
      popoverVisibleTo: false,
      selectedItem: '',
    }
  },
  methods: {
    // 填充备选关系列表，渲染勾选框
    fillEdgeCheckBoxList() {
      this.allEdgeClassList.forEach(it=>{
        it.isEnable = true;
        it.isChecked = false;
        this.edgeCheckBoxList.push(it);
      })
      console.log("分析关系列表");
      console.log(this.edgeCheckBoxList);
    },

    // 执行分析动作
    analyse() {
      var formData = {
        edgeClassList: this.checkedIds,
        fromNode: this.fromNode,
        toNode: this.toNode,
        isShortest: this.isShortest,
        maxDegree: this.maxDegree
      }
      console.log("分析表单数据如下")
      console.log(formData)

      pathAnalyse(formData).then(resp=>{

      })

    },

    reset(){

    },

    // 根据名称查询节点
    queryNode(isFromNode) {
      var queryNodeName = '';
      if(isFromNode){
        queryNodeName = this.fromNode.name;
      }else{
        queryNodeName = this.toNode.name;
      }
      getAllNodeInstance({valid:1,name:queryNodeName}).then(resp=>{
        console.log(resp);
        this.queryNodeList = resp;
          if(isFromNode){
            this.popoverVisibleFrom = true;
          }else{
            this.popoverVisibleTo = true;
          }

      })
    },

    handleSelectChange(value,isFromNode) {
      if(isFromNode){
        this.fromNode = value
      }else{
        this.toNode = value
      }
      if(isFromNode){
        this.popoverVisibleFrom = false; // 关闭 popover
      }else {
        this.popoverVisibleTo = false; // 关闭 popover
      }

      // 选择完成后需要情况这些状态
      this.selectedItem = '';
      this.queryNodeList = '';
    }


  },
  mounted() {
    getAllEdgeClass({valid:1}).then(resp=>{
      this.allEdgeClassList = resp;
      console.log("所有关系类型如下:")
      console.log(this.allEdgeClassList)
      this.fillEdgeCheckBoxList();
    });
  }

}
</script>

<style scoped>
  #main-container {
    background-color: #FFFFFF;
    width:100vw;
    height:100vh;
  }

  #left-container {
    background-color: #FFFFFF;
    width:30vw;
    height:100vh;
  }
</style>
