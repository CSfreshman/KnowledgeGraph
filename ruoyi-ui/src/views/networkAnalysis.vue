<template>
  <div id="main-container">
<!--    左侧表单内容-->
    <div id="left-container">

<!--      头部-->
      <div>
        <el-row>
          <el-col span="12">
            <el-button size="medium" @click="showPath = true">路径分析</el-button>
          </el-col>
          <el-col span="12">
            <el-button size="medium" @click="showPath = false">中心多度探寻</el-button>
          </el-col>
        </el-row>
        <el-divider></el-divider>
      </div>

<!--      路径分析-->
      <div v-if="showPath">
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
            <el-checkbox-group v-model="checkedEdge">
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

<!--      中心多度探寻-->
      <div v-if="!showPath">

<!--        设置分析对象实体-->
        <div>
          <el-row> 设置分析对象实体 </el-row>
          <el-row>
            <el-form>
              <el-form-item label="分析实体" label-width="100px">
<!--                为了方便编写，这里使用fromNode起点实体代替分析实体-->
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
            </el-form>
          </el-row>
          <el-divider></el-divider>
        </div>

<!--        设置关系度数-->
        <div>
          <el-radio-group v-model="selectedDegree" class="radio-group">
            <el-radio :label="1">1度</el-radio>
            <el-radio :label="2">2度</el-radio>
            <el-radio :label="3">3度</el-radio>
            <el-radio :label="4">4度</el-radio>
            <el-radio :label="5">5度</el-radio>
          </el-radio-group>
        </div>

<!--        设置分析实体-->
        <div>
          <el-row> 设置分析实体 </el-row>
          <div>
            <el-checkbox-group v-model="checkedNode">
              <el-checkbox v-for="item in nodeCheckBoxList" :key="item.id" :label="item" :disabled="!item.isEnable">{{ item.name }}</el-checkbox>
            </el-checkbox-group>
          </div>
          <el-divider></el-divider>
        </div>

<!--        设置分析关系-->
        <div>
          <el-row> 设置分析关系 </el-row>
          <div>
            <el-checkbox-group v-model="checkedEdge">
              <el-checkbox v-for="item in edgeCheckBoxList" :key="item.id" :label="item" :disabled="!item.isEnable">{{ item.label }}</el-checkbox>
            </el-checkbox-group>
          </div>
          <el-divider></el-divider>
        </div>

<!--        底部按钮-->
        <div>
          <el-button size="medium" @click="analyse">分析</el-button>
          <el-button size="medium" @click="reset">重置</el-button>
        </div>
      </div>
    </div>

<!--    图谱-->
    <div id="right-container">

    </div>
  </div>
</template>

<script>

import {getAll as getAllEdgeClass} from "@/api/mange/class/edge";
import {getAllNodeClass} from "@/api/mange/class/node";
import {getAll as getAllNodeInstance} from "@/api/mange/instance/node"
import {centerMultiDegree, pathAnalyse} from "@/api/graph";

export default {
  name: "networkAnalysis",
  data() {
    return {
      showPath: true,
      isShortest: true,
      allEdgeClassList: [],
      allNodeClassList: [],
      edgeCheckBoxList: [],
      nodeCheckBoxList: [],
      checkedEdge: [],
      checkedNode: [],
      maxDegree: '',
      fromNode: {name:''},
      toNode: {name: ''},
      queryNodeList: [],
      popoverVisibleFrom: false,
      popoverVisibleTo: false,
      selectedItem: '',
      analyseNode: {name: ''},  // 分析对象实体
      selectedDegree: 1,

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
    fillNodeCheckBoxList() {
      this.allNodeClassList.forEach(it=>{
        it.isEnable = true;
        it.isChecked = false;
        this.nodeCheckBoxList.push(it);
      })
      console.log("分析实体列表");
      console.log(this.nodeCheckBoxList);
    },

    // 执行分析动作
    analyse() {
      // 路径分析
      if(this.showPath){
        var formData = {
          edgeClassList: this.checkedEdge,
          fromNode: this.fromNode,
          toNode: this.toNode,
          isShortest: this.isShortest,
          maxDegree: this.maxDegree
        }
        console.log("路径分析表单数据如下")
        console.log(formData)

        pathAnalyse(formData).then(resp=>{

        })
      }else {
        // 中心多度探寻
        var formData = {
          edgeClassList: this.checkedEdge,
          nodeClassList: this.checkedNode,
          analyseNode: this.fromNode, // 分析实体在表单中使用的是fromNode
          selectedDegree: this.selectedDegree
        }

        console.log("中心多读探寻分析表单数据如下")
        console.log(formData)
        centerMultiDegree(formData).then(resp=>{

        })

      }


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

    getAllNodeClass({valid:1}).then(resp=>{
      this.allNodeClassList = resp;
      console.log("所有节点类型如下:")
      console.log(this.allNodeClassList)
      this.fillNodeCheckBoxList();
    })
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

  .radio-group {
    white-space: nowrap;
  }
</style>
