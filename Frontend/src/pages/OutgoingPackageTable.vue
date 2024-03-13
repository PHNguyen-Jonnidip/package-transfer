<template>
  <div class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <card class="strpied-tabled-with-hover"
                body-classes="table-responsive"
          >
            <template slot="header">
              <h4 class="card-title">Outgoing Package</h4>
              <p class="card-category">List of Outgoing Package</p>
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
  name: "OutgoingPackageTable",

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
        columns: ['id', 'status', 'webhook_id', 'incoming_package_id', 'number_of_retries', 'first_failed_delivery_time_stamp', 'last_failed_delivery_time_stamp'],
        search: ['status.equal', 'incoming_package_id.equal', 'webhook_id.equal'],
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
      const path = '/api/outgoing-packages'
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
          message: error.response.data.message || "Can't get outgoing packages data please try again",
          horizontalAlign: 'right',
          verticalAlign: 'top',
          type: 'danger'
        })
      });
    },
  },

  mounted() {
    this.getData();
  }
}
</script>

<style scoped>
</style>
