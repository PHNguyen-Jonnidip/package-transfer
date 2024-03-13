<template>
  <div class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-3">
          <stats-card>
            <div slot="header" class="icon-warning">
              <i class="nc-icon nc-delivery-fast text-warning"></i>
            </div>
            <div slot="content">
              <p class="card-category">Incoming</p>
              <h4 class="card-title">{{generalInfo.incoming}}</h4>
            </div>
            <div slot="footer">
              Last 3 months
            </div>
          </stats-card>
        </div>

        <div class="col-3">
          <stats-card>
            <div slot="header" class="icon-success">
              <i class="nc-icon nc-favourite-28 text-success"></i>
            </div>
            <div slot="content">
              <p class="card-category">Done Outgoings</p>
              <h4 class="card-title">{{generalInfo.done_outgoing}}</h4>
            </div>
            <div slot="footer">
              Last 3 months
            </div>
          </stats-card>
        </div>

        <div class="col-3">
          <stats-card>
            <div slot="header" class="icon-danger">
              <i class="nc-icon nc-fav-remove text-danger"></i>
            </div>
            <div slot="content">
              <p class="card-category">Failed Outgoings</p>
              <h4 class="card-title">{{generalInfo.failed_outgoing}}</h4>
            </div>
            <div slot="footer">
              Last 3 months
            </div>
          </stats-card>
        </div>

        <div class="col-3">
          <stats-card>
            <div slot="header" class="icon-danger">
              <i class="nc-icon nc-cloud-upload-94 text-info"></i>
            </div>
            <div slot="content">
              <p class="card-category">Archived Outgoings</p>
              <h4 class="card-title">{{generalInfo.archived_outgoing}}</h4>
            </div>
            <div slot="footer">
              Last 3 months
            </div>
          </stats-card>
        </div>

      </div>
      <div class="row">
        <div class="col-md-8" v-if="!loadingIncomingStatistic">
          <chart-card :chart-data="lineChart.data"
                      :chart-options="lineChart.options"
                      :responsive-options="lineChart.responsiveOptions"
                      chart-type="Line">
            <template slot="header">
              <h4 class="card-title">Packages Statistics</h4>
              <p class="card-category">Last 10 days</p>
            </template>
            <template slot="footer">
              <div class="legend">
                <i class="fa fa-circle text-info"></i> Incoming
                <i class="fa fa-circle text-danger"></i> Outgoing
              </div>
            </template>
          </chart-card>
        </div>

        <div class="col-md-4" v-if="!loadingGeneralData">
          <chart-card :chart-data="pieChart.data" chart-type="Pie">
            <template slot="header">
              <h4 class="card-title">Incoming Statistics</h4>
              <p class="card-category">Last 3 months</p>
            </template>
            <template slot="footer">
              <div class="legend">
                <i class="fa fa-circle text-info"></i> POST
                <i class="fa fa-circle text-warning"></i> PUT
<!--                <i class="fa fa-circle text-info"></i> PATCH-->
                <i class="fa fa-circle text-danger"></i> DELETE
              </div>
            </template>
          </chart-card>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  import ChartCard from 'src/components/Cards/ChartCard.vue'
  import StatsCard from 'src/components/Cards/StatsCard.vue'
  import LTable from 'src/components/Table.vue'
  import {request} from "@/stores/request";

  export default {
    components: {
      LTable,
      ChartCard,
      StatsCard
    },
    data () {
      return {
        request,
        loadingGeneralData: true,
        loadingIncomingStatistic: true,
        generalInfo: {
          incoming: 0,
          post_incoming: 0,
          put_incoming: 0,
          patch_incoming: 0,
          delete_incoming: 0,
          outgoing: 0,
          done_outgoing: 0,
          failed_outgoing: 0,
          archived_outgoing: 0,
          webhook: 0,
        },
        editTooltip: 'Edit Task',
        deleteTooltip: 'Remove',
        pieChart: {
          data: {
            labels: ['POST', 'DELETE', 'PUT'],
            series: [10,10,10]
          }
        },
        lineChart: {
          data: {
            labels: [],
            series: []
          },
          options: {
            showArea: false,
            height: '245px',
            axisX: {
              showGrid: false
            },
            lineSmooth: true,
            showLine: true,
            showPoint: true,
            fullWidth: false,
            chartPadding: {
              right: 50
            }
          },
          responsiveOptions: [
            ['screen and (max-width: 640px)', {
              axisX: {
                labelInterpolationFnc (value) {
                  return value[0]
                }
              }
            }]
          ]
        },
      }
    },

    methods: {
      getGeneralInfo() {
        const path = '/api/general-information/packages'
        const from = new Date();
        from.setMonth(from.getMonth() - 3);
        this.request.axiosInstance.get(path, {params: {from}})
          .then((response) => {
            this.generalInfo = response.data;
            this.pieChart.data.series = [
              response.data.post_incoming,
              response.data.delete_incoming,
              response.data.put_incoming
            ]
          }).catch((error) => {
            this.$notifications.notify({
              message: error.response.data.message || "Can't get general information please try again",
              horizontalAlign: 'right',
              verticalAlign: 'top',
              type: 'danger'
            })
          }).finally(() => {
            this.loadingGeneralData = false;
          })
      },

      getIncomingStatistics() {
        const path = '/api/general-information/statistics-incoming'
        const from = new Date();
        from.setDate(from.getDate() - 10);
        this.request.axiosInstance.get(path, {params: {from}})
          .then((response) => {
            const labels = [];
            for (const date of response.data.labels) {
              labels.push(new Date(date).toDateString().slice(4,10));
            }
            this.lineChart.data.series = response.data.series;
            this.lineChart.data.labels = labels;
          }).catch((error) => {
            this.$notifications.notify({
              message: error.response.data.message || "Can't get incoming statistic please try again",
              horizontalAlign: 'right',
              verticalAlign: 'top',
              type: 'danger'
            })
          }).finally(() => {
            this.loadingIncomingStatistic = false;
          })
      },
    },

    mounted() {
      this.getGeneralInfo();
      this.getIncomingStatistics();
    }
  }
</script>
<style>

</style>
