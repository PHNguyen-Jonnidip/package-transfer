<template>
  <div>
    <form @submit.prevent="onSubmit">

      <div v-for="(f, i) of searchFields" :key="i">
        <div class="input-group mb-2">
          <div class="input-group-addon m-1" :style="i === 0 ? '':'visibility:hidden'">
            <a class="btn btn-fill btn-success btn-sm" @click="addMoreSearchField"><i
              class="fas fa-plus-circle text-white"></i></a>
          </div>

          <select class="custom-select mr-1" name="search_field" v-model="searchFields[i]">
            <option value=''>Choose field...</option>
            <option v-for="field of availableFields" :key="field" :value="field" :selected="searchFields[i] === field">{{field}}</option>
          </select>

          <input type="search" class="form-control search-input" placeholder="Enter search value..." name="search_value"
                 v-model="searchValues[i]">

          <div class="input-group-addon ml-1" v-if="i > 0">
            <a class="btn btn-fill btn-danger btn-sm" @click="removeSearchField(i)"><i
              class="fas fa-minus-circle text-white"></i></a>
          </div>

          <button type="submit" class="btn btn-info btn-light btn-sm ml-1 btn-fill" v-if="i === 0"><i class="fas fa-search"></i></button>

        </div>
      </div>

    </form>

  </div>
</template>

<script lang="ts">
export default {
  name: "SearchBar",

  props: {
    fields: {
      type: Array,
      default: () => []
    },
    searchFields: {
      type: Array,
      default: () => ['']
    },
    searchValues: {
      type: Array,
      default: () => ['']
    }
  },

  data() {
    return {
      availableFields: []
    }
  },

  methods: {
    onSubmit(){
      const searchParams = {}
      for (let i = 0; i < this.searchFields.length; i++) {
        searchParams[this.searchFields[i]]=this.searchValues[i]
      }
      this.$emit('searchChange', searchParams);
    },

    addMoreSearchField(){
      this.searchFields.push('');
      this.searchValues.push('');
    },

    removeSearchField(i) {
      this.searchFields.splice(i, 1);
      this.searchValues.splice(i, 1);
    }
  },

  mounted() {
    this.availableFields = this.fields
  }
}
</script>

<style scoped>

</style>
