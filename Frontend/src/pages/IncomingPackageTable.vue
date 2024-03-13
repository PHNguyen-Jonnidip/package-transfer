<template>
  <div class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <card class="strpied-tabled-with-hover"
                body-classes="table-responsive"
          >
            <template slot="header">
              <div class="float-left">
                <h4 class="card-title">Incoming Package</h4>
                <p class="card-category">List of Incoming Package</p>
              </div>
              <router-link to="/admin/incoming-package/new" class="btn btn-fill btn-primary float-right"><i class="fas fa-plus"></i> Create a test Incoming</router-link>
            </template>

            <SearchBar :fields="table.search" @searchChange="onSearchChange"/>

            <l-table class="table-hover table-striped"
                     :columns="table.columns"
                     :data="table.data">
            </l-table>

            <pagination
              :lastPage="table.lastPage"
              :page="table.page"
              @onChangePage="changePage"
            ></pagination>
          </card>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import LTable from 'src/components/Table.vue'
import Card from 'src/components/Cards/Card.vue'
import Pagination from "../components/Pagination.vue";
import {request} from "@/stores/request";
import SearchBar from "../components/SearchBar.vue";

export default {
  name: 'IncomingPackageTable',

  components: {
    Pagination,
    LTable,
    Card,
    SearchBar,
  },

  data() {
    return {
      request,
      table: {
        columns: ['id', 'status', 'target_topics', 'payload', 'created_at', 'updated_at'],
        search: ['status.equal', 'payload.contain'],
        searchParams: {},
        data: [],
        page: 1,
        size: 10,
        total: 0,
        sort: 'createdAt,desc',
        lastPage: 1,
      },
    }
  },

  methods: {
    changePage(page) {
      this.table.page = page;
      this.getData();
    },

    onSearchChange(searchParams){
      this.table.searchParams = searchParams;
      this.getData();
    },

    async getData() {
      const path = '/api/incoming-packages'
      const params = {
        page: this.table.page - 1,
        size: this.table.size,
        sort: this.table.sort,
        ...this.table.searchParams,
      }
      await this.request.axiosInstance.get(path, {params}).then((response) => {
        this.table.data = response.data.results
        this.table.total = response.data.total
        this.table.lastPage = Math.ceil(this.table.total / this.table.size);
      }).catch((error) => {
        this.$notifications.notify({
          message: error.response.data.message || "Can't get incoming packages data please try again",
          horizontalAlign: 'right',
          verticalAlign: 'top',
          type: 'danger'
        })
      });
    }
  },

  mounted() {
    this.getData();
  }
}
</script>

<style scoped>

</style>
