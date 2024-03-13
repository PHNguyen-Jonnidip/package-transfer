<template>
  <table class="table">
    <thead class="thead-light">
      <slot name="columns">
        <tr>
          <th v-for="column in columns" :key="column">{{column}}</th>
        </tr>
      </slot>
    </thead>
    <tbody>
      <tr v-for="(item, index) in data" :key="index">
        <slot :row="item">
          <td v-for="column in columns" :key="column" v-if="column !== 'action'" style="vertical-align: top">{{itemValue(item, column)}}</td>
          <td v-else>
            <a class="edit-link" @click="redirectToEditPage(item)">Edit</a>
            <a class="delete-link" @click="triggerDelete(item)">Delete</a>
          </td>
        </slot>
      </tr>
    </tbody>
  </table>
</template>
<script>
  export default {
    name: 'l-table',

    props: {
      columns: Array,
      data: Array
    },

    methods: {
      hasValue (item, column) {
        return item[column.toLowerCase()] !== 'undefined'
      },

      itemValue (item, column) {
        return item[column.toLowerCase()]
      },

      redirectToEditPage(item) {
        this.$router.push(`${this.$route.path}/${item.id}`)
      },

      triggerDelete(item) {
        this.$emit('onDelete', item.id)
      }
    },

    mounted() {
    }
  }
</script>
<style>
.edit-link {
  margin: 0 0.5rem;
  color: #ED8D00;
  text-decoration: underline;
}

.edit-link:hover {
  cursor: pointer;
  text-decoration: underline;
}

.delete-link {
  margin: 0 0.5rem;
  color: #FF4A55;
  text-decoration: underline;
}

.delete-link:hover {
  cursor: pointer;
  text-decoration: underline;
}
</style>
