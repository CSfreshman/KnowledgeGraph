<template>
  <div>
    <el-row>
      <el-col span="7">
        <el-card>
          <el-form>
            <el-form-item :inline="true">
              <el-input
                placeholder="请输出实体类型名称"
                v-model="queryParams.name">
                <template #suffix>
                  <el-button style="border: none" icon="el-icon-search" size="small" @click="handleQuery"></el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-form>


          <el-button @click="handleAdd" >新建实体类型</el-button>

          <el-table v-loading="loading" :data="nodeClassList" :show-header="false" @row-click="changeMainNode">
            <el-table-column key="name" prop="name"></el-table-column>
          </el-table>
          <pagination
            v-show="total>0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
          />
        </el-card>
      </el-col>
      <el-col span="17">
        <el-card>
          <div v-show="mainNode == ''">
            <el-input
              placeholder="请选择一个实体类型"
              v-model="input"
              :disabled="true">
            </el-input>
          </div>


          <div v-show="mainNode != ''">

            <el-form :model="mainNode" label-width="80px">
              <el-row>
                <el-form-item label="id">
                  <el-input
                    :placeholder="mainNode.id"
                    v-model="input"
                    :disabled="true">
                  </el-input>
                </el-form-item>
              </el-row>

              <el-row>
                <el-form-item label="名称">
                  <el-input
                    :placeholder="mainNode.name"
                    v-model="input"
                    :disabled="true">
                  </el-input>
                </el-form-item>
              </el-row>

            </el-form>


            <el-row style="margin-bottom: 10px">

                <el-button @click="handleDeleteClass">删除该数据</el-button>

            </el-row>
            <el-row>
              <el-col :span="20">属性:</el-col>
              <el-col :span="4">
                <el-button @click="handleAddProperties">新增属性</el-button>
              </el-col>

            </el-row>
            <el-table v-loading="loading" :data="nodePropertiesList" @selection-change="handleSelectionChange">
              <el-table-column label="属性名" prop="name"></el-table-column>
<!--              <el-table-column label="类型" prop="type"></el-table-column>-->
              <el-table-column label="默认值" prop="defaultValue"></el-table-column>
              <el-table-column
                label="操作"
                align="center"
                width="160"
                class-name="small-padding fixed-width"
              >
                <template slot-scope="scope" v-if="scope.row.userId !== 1">
                  <el-button
                    size="mini"
                    type="text"
                    icon="el-icon-edit"
                    @click="handleUpdate(scope.row)"
                    v-hasPermi="['system:user:remove']"
                  >修改
                  </el-button>
                  <el-button
                    size="mini"
                    type="text"
                    icon="el-icon-delete"
                    @click="handleDeleteProperties(scope.row)"
                    v-hasPermi="['system:user:remove']"
                  >删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>


        </el-card>
      </el-col>
    </el-row>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="实体类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入实体类型名称" />
        </el-form-item>
<!--        <el-form-item label="创建人" prop="createUser">-->
<!--          <el-input v-model="form.createUser" placeholder="请输入创建人" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="是否有效，1有效，0无效" prop="valid">-->
<!--          <el-input v-model="form.valid" placeholder="请输入是否有效，1有效，0无效" />-->
<!--        </el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="titleProperties" :visible.sync="openProperties" width="500px" append-to-body>
      <el-form ref="form" :model="formProperties" label-width="80px">
        <el-form-item label="属性名" prop="name">
          <el-input v-model="formProperties.name" placeholder="请输入属性名(不能以数字开头)" />
        </el-form-item>
<!--        <el-form-item label="属性类型" prop="type">-->
<!--          <el-select v-model="formProperties.type" placeholder="请选择">-->
<!--            <el-option-->
<!--              v-for="item in optionalType"-->
<!--              :key="item.value"-->
<!--              :label="item.label"-->
<!--              :value="item.value">-->
<!--            </el-option>-->
<!--          </el-select>-->
<!--&lt;!&ndash;          <el-input v-model="" placeholder="请输入属性类型" />&ndash;&gt;-->
<!--        </el-form-item>-->
        <el-form-item label="默认值" prop="defaultValue">
          <div v-if="formProperties.name === 'color'">
            <el-color-picker v-model="formProperties.defaultValue" :color-format="'rgb'" :show-alpha="false"></el-color-picker>
          </div>
          <div v-if="formProperties.name != 'color'">
            <el-input v-model="formProperties.defaultValue" @input="changeMessage" placeholder="请输入属性默认值" />
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFormProperties">确 定</el-button>
        <el-button @click="cancelProperties">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import { listClass, getClass, delClass, addClass, updateClass } from "@/api/mange/class/node";
import {delNodeProperties, listNodeProperties, updateNodeProperties, addNodeProperties} from "@/api/mange/class/nodeProperties"

export default {
  name: "node",
  data() {
    return {
      editProp: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      nodeClassList: [],
      // 弹出层标题
      title: "",
      titleProperties: "",
      // 是否显示弹出层
      open: false,
      openProperties: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        createUser: null,
        valid: null
      },

      nodePropertiesQueryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        type: null,
        createUser: null,
        nodeId: null,
        valid: null,
        modifyTime: null,
        modifyUser: null,
        modifyType: null
      },

      // 表单参数
      form: {},
      formProperties: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "实体类型名称不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        valid: [
          { required: true, message: "是否有效，1有效，0无效不能为空", trigger: "blur" }
        ]
      },
      rulesProperties: {
        name: [
          { required: true, message: "属性名不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "属性类型不能为空", trigger: "change" }
        ],

      },
      mainNode: '',
      nodePropertiesList: [],

      optionalType: [{
        value: 'int',
        label: 'int'
      }, {
        value: 'float',
        label: 'float'
      }, {
        value: 'long',
        label: 'long'
      }, {
        value: 'double',
        label: 'double'
      }, {
        value: 'string',
        label: 'string'
      }],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    changeMessage(){
      this.$forceUpdate()
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      this.queryParams.valid = 1;
      listClass(this.queryParams).then(response => {
        this.nodeClassList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    cancelProperties() {
      this.openProperties = false;
      this.resetProperties();
    },

    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        createTime: null,
        createUser: null,
        valid: null
      };
      this.resetForm("form");
    },
    resetProperties() {
      this.formProperties = {
        id: null,
        name: null,
        type: null,
        createTime: null,
        createUser: null,
        nodeId: null,
        valid: null,
        modifyTime: null,
        modifyUser: null,
        modifyType: null
      };
      this.resetForm("formProperties");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.resetProperties();
      this.editProp = true;
      this.titleProperties = "修改属性";
      this.openProperties = true;
      this.formProperties.name = row.name;
      this.formProperties.type = 'String';
      this.formProperties.defaultValue = row.defaultValue;
      this.formProperties.id = row.id;
      this.formProperties.originValue = row.defaultValue;
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateClass(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addClass(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDeleteClass(row) {
      const ids = this.mainNode.id;
      this.$modal.confirm('是否确认删除编号为"' + ids + '"的数据项？').then(function() {
        return delClass(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/mange/class/node/export', {
        ...this.queryParams
      }, `class_${new Date().getTime()}.xlsx`)
    },

    changeMainNode(row) {
      this.mainNode = row;
      this.nodePropertiesQueryParams.nodeId = row.id;
      this.nodePropertiesQueryParams.valid = 1;
      listNodeProperties(this.nodePropertiesQueryParams).then(response => {
        this.nodePropertiesList = response.rows
      });
    },

    queryNodeProperties(id) {
      this.nodePropertiesQueryParams.valid = 1;
      listNodeProperties(this.nodePropertiesQueryParams).then(response => {
        this.nodePropertiesList = response.rows
      });
    },

    handleDeleteProperties(row) {
      const ids = row.id;
      this.$modal.confirm('是否确认删除id为"' + ids + '"的属性？').then(function() {
        return delNodeProperties(ids);
      }).then(() => {
        this.queryNodeProperties(row.nodeId);
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 新增按钮操作 */
    handleAddProperties() {
      this.resetProperties();
      this.openProperties = true;
      this.titleProperties = "添加属性";
    },
    /** 提交按钮 */
    submitFormProperties() {
      this.formProperties.type = 'String';
      this.$refs["form"].validate(valid => {
        if (valid) {
          if(this.editProp){
            this.formProperties.nodeId = this.mainNode.id;
            updateNodeProperties(this.formProperties).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.openProperties = false;
              this.editProp = false;
              this.queryNodeProperties(this.mainNode.id);
            });
          }else{
            this.formProperties.nodeId = this.mainNode.id;
            addNodeProperties(this.formProperties).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.openProperties = false;
              this.queryNodeProperties(this.mainNode.id);
            });
          }
          if (this.formProperties.id != null) {

          } else {

          }
        }
      });
    }
  }

}
</script>

<style scoped>

</style>
