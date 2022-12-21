<template>
  <!--查询框-->
  <r-query :model="query"   >
    <r-field  label="id" prop="id"  ><r-text v-model="query.id"  /></r-field>
    <template v-slot:s><el-button class="search-bar-button" type="primary" @click="doQuery" icon="el-icon-search" size="small">查询</el-button></template>
    <template v-slot:r><el-button icon=" el-icon-refresh-left" size="small" @click="reset" plain>重置</el-button></template>
  </r-query>
  <!--表格--->
  <r-table :query="query" ref="table" :hideOnePage="false" :field="field"    >

  </r-table>
</template>
<script>
import {ref} from "vue";
import { useStore} from "vuex";
export default {
  name: "${remark}",
  setup() {
    //查询框绑定字段
    const query = ref({})
    const store=useStore();
    const list=ref(store.getters.getDirect("sysConfig"));
    //字段属性
    const field=[
      {label: "主键", prop: "id",width:250},

    ]
    //表格实例
    const table=ref(null);
    //查询方法
    const doQuery=()=>{
      table.value.search()
    }
    //重置方法
    const reset=()=>{
      table.value.reset();
    }
    return {query,field,table,doQuery,reset,list}
  }

}
</script>
<style scoped>
.search-bar-button {
  display: inline-block;
  float: right;
}
</style>