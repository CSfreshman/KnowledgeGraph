<template>
  <div>
    <el-row>
      <el-col span="7">
        <el-card>
          <el-form>
            <el-form-item :inline="true">
              <el-input
                placeholder="请输出关系类型名称"
                v-model="queryParams.label">
                <template #suffix>
                  <el-button style="border: none" icon="el-icon-search" size="small" @click="handleQuery"></el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-form>


          <el-button @click="handleAdd" >新建关系类型</el-button>

          <el-table v-loading="loading" :data="edgeClassList" :show-header="false" @row-click="changeMainData">
            <el-table-column key="name" prop="label"></el-table-column>
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
          <div v-show="mainData == ''">
            <el-input
              placeholder="请选择一个关系类型"
              v-model="input"
              :disabled="true">
            </el-input>
          </div>


          <div v-show="mainData != ''">

            <el-form :model="mainData" label-width="80px">
              <el-row>
                <el-form-item label="id">
                  <el-input
                    :placeholder="mainData.id"
                    v-model="input"
                    :disabled="true">
                  </el-input>
                </el-form-item>
              </el-row>

              <el-row>
                <el-form-item label="名称">
                  <el-input
                    :placeholder="mainData.label"
                    v-model="input"
                    :disabled="true">
                  </el-input>
                </el-form-item>
              </el-row>

              <el-row>
                <el-form-item label="起点">
                  <el-input
                    :placeholder="mainData.fromNodeClassName"
                    v-model="input"
                    :disabled="true">
                  </el-input>
                </el-form-item>
              </el-row>

              <el-row>
                <el-form-item label="终点">
                  <el-input
                    :placeholder="mainData.toNodeClassName"
                    v-model="input"
                    :disabled="true">
                  </el-input>
                </el-form-item>
              </el-row>

            </el-form>


            <el-row>
              <el-col :span="20">属性:</el-col>
              <el-col :span="4">
                <el-button @click="handleAddProperties">新增属性</el-button>
              </el-col>
            </el-row>
            <el-table v-loading="loading" :data="edgePropertiesList" @selection-change="handleSelectionChange">
              <el-table-column label="属性名" prop="name"></el-table-column>
              <el-table-column label="类型" prop="type"></el-table-column>
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

<!--    新建关系类型-->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="关系类型名称" prop="name">
          <el-input v-model="form.label" placeholder="请输入实体类型名称" />
        </el-form-item>
        <el-form-item label="起点实体" prop="name">
          <el-select v-model="form.fromNodeId" placeholder="请选择">
            <el-option
              v-for="item in optionalNodeClassList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="终点实体" prop="name">
          <el-select v-model="form.toNodeId" placeholder="请选择">
            <el-option
              v-for="item in optionalNodeClassList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
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

    <!-- 新建属性 -->
    <el-dialog :title="titleProperties" :visible.sync="openProperties" width="500px" append-to-body>
      <el-form ref="form" :model="formProperties" :rules="rulesProperties" label-width="80px">
        <el-form-item label="属性名" prop="name">
          <el-input v-model="formProperties.name" placeholder="请输入属性名" />
        </el-form-item>
        <el-form-item label="属性类型" prop="type">
          <el-select v-model="formProperties.type" placeholder="请选择">
            <el-option
              v-for="item in optionalType"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
          <!--          <el-input v-model="" placeholder="请输入属性类型" />-->
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

import { listClass, getClass, delClass, addClass, updateClass } from "@/api/mange/class/edge";
import { getAllNodeClass  } from "@/api/mange/class/node";
import { listClassProperties, getClassProperties, delClassProperties, addClassProperties, updateClassProperties } from "@/api/mange/class/edgeProperties";


export default {
  name: "edge",
  data() {
    return {
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
      // 【请填写功能名称】表格数据
      edgeClassList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        label: null,
        fromNodeId: null,
        toNodeId: null,
        createUser: null,
        valid: 1
      },
      edgePropertiesQueryParams: {
        pageNum: 1,
        pageSize: 10,
        edgeId: null,
        name: null,
        type: null,
        createUser: null,
        valid: null,
        modityTime: null,
        modityUser: null,
        modityType: null
      },
      // 表单参数
      form: {},

      formProperties: {},
      // 表单校验
      rules: {
        label: [
          { required: true, message: "关系名不能为空", trigger: "blur" }
        ],
      },
      mainData: '',
      edgePropertiesList: [],
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
      titleProperties: '',
      openProperties: false,
      rulesProperties: {
        edgeId: [
          { required: true, message: "边id不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "属性名不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "属性类型不能为空", trigger: "change" }
        ],
        valid: [
          { required: true, message: "是否有效，1有效，0无效不能为空", trigger: "blur" }
        ],
      },
      optionalNodeClassList: ''
    };
  },
  created() {
    this.getList();
    this.getNodeClassList();
  },
  methods: {
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      listClass(this.queryParams).then(response => {
        this.edgeClassList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getNodeClassList() {
      var queryParams = {
        name: null,
        createUser: null,
        valid: 1
      };
      getAllNodeClass(queryParams).then(resp => {
        this.optionalNodeClassList = this.convertData(resp);
        console.log(this.optionalNodeClassList)
      })
    },


    convertData(originalData) {
      const convertedData = originalData.map(item => {
        return { value: item.id, label: item.name };
      });
      return convertedData;
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
        edgeId: null,
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
      this.title = "添加关系类型";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getClass(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改【请填写功能名称】";
      });
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
            this.form.valid = 1;
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
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除【请填写功能名称】编号为"' + ids + '"的数据项？').then(function() {
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

    changeMainData(row) {
      this.mainData = row;
      this.edgePropertiesQueryParams.edgeId = row.id;
      this.edgePropertiesQueryParams.valid = 1;
      listClassProperties(this.edgePropertiesQueryParams).then(response => {
        this.edgePropertiesList = response.rows
      });
    },

    queryEdgeProperties(id) {
      this.edgePropertiesQueryParams.valid = 1;
      listClassProperties(this.edgePropertiesQueryParams).then(response => {
        this.edgePropertiesList = response.rows
      });
    },

    handleDeleteProperties(row) {
      const ids = row.id;
      this.$modal.confirm('是否确认删除id为"' + ids + '"的属性？').then(function() {
        return delClassProperties(ids);
      }).then(() => {
        this.queryEdgeProperties(row.edgeId);
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
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.formProperties.id != null) {
            updateClassProperties(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.openProperties = false;
                this.queryEdgeProperties(this.mainData.id);
            });
          } else {
            this.formProperties.edgeId = this.mainData.id;
            this.formProperties.valid = 1
            addClassProperties(this.formProperties).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.openProperties = false;
              this.queryEdgeProperties(this.mainData.id);
            });
          }
        }
      });
    }
  }

}
</script>

<style scoped>

</style>
