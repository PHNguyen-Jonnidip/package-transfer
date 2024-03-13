<template>
  <div class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <card class="strpied-tabled-with-hover"
                body-classes="table-responsive"
          >
            <template slot="header">
              <h4 class="card-title">Response Log</h4>
              <p class="card-category">List of Response Log</p>
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

<script>
import Pagination from "@/components/Pagination";
import LTable from "@/components/Table";
import Card from "@/components/Cards/Card";
import {request} from "@/stores/request";
import SearchBar from "@/components/SearchBar";

export default {
  name: "HttpResponseLogTable",

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
        columns: ['id', 'outgoing_package_id', 'request_body', 'response_code', 'response_body', 'created_at'],
        search: ['outgoing_package_id.equal', 'incoming_package_id.equal', 'webhook_id.equal'],
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
      const path = '/api/http-response-logs'
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
          message: error.response.data.message || "Can't get response logs data please try again",
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
