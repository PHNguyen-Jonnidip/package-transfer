<template>
  <div class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <card>
            <template slot="header">
              <h4 class="card-title">Create an Incoming Package</h4>
            </template>

            <form>
              <div class="form-group w-50">
                <label for="topic">Add Topic <i class="text-danger">*</i></label>
                <div class="input-group">
                <input id="topic"
                       type="text"
                       class="form-control mr-2"
                       :class="{'is-invalid': !isTopicValid}"
                       placeholder="test"
                       aria-describedby="basic-addon2"
                       v-model="topic">
                  <div class="input-group-append">
                    <button
                      class="btn btn-success btn-round"
                      type="button"
                      title="Add Topic"
                      @click="onAddTopic">+
                    </button>
                  </div>
                  <div class="invalid-feedback">
                    Topic can't be empty
                  </div>
                </div>
              </div>

              <div v-if="form.target_topics.length > 0" class="control-label">
                Topics: {{form.target_topics}}
                <button @click.prevent="onResetTopic" title="Remove all topics" class="m-3 btn btn-round btn-danger"><i class="fas fa-ban text-danger"></i></button>
              </div>

              <label class="control-label">
                Http Method <i class="text-danger">*</i>
              </label>
              <select class="custom-select" id="methodSelect" v-model="form.method">
                <option value="POST">POST</option>
                <option value="PUT">PUT</option>
<!--                <option value="PATCH">PATCH</option>-->
                <option value="DELETE">DELETE</option>
              </select>

              <div class="form-group">
                <label for="payload">Payload <i class="text-danger">*</i></label>
                <textarea id="payload"
                          class="form-control"
                          :class="{'is-invalid': !isPayloadValid}"
                          aria-label="With textarea"
                          v-model="form.payload"
                          @input="createExamplePayload">
                </textarea>
                <div class="invalid-feedback" v-if="!isPayloadValid">
                  Please input a valid JSON string.
                </div>
              </div>

              <div class="form-group">
                <label for="payload">Example Post Incoming Package payload</label>
                <textarea id="payload" class="form-control" aria-label="With textarea" v-model="examplePayload" readonly></textarea>
              </div>

              <div class="float-right btn-toolbar" role="toolbar">
                <div class="btn-group mr-2" role="group">
                  <button class="btn btn-danger"
                          @click.prevent="onCancel">
                    Cancel
                  </button>
                </div>
                <div class="btn-group mr-2" role="group">
                  <button type="submit"
                          class="btn btn-success btn-fill"
                          @click.prevent="onSubmit">
                    Submit
                  </button>
                </div>
              </div>
            </form>
          </card>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Card from 'src/components/Cards/Card.vue'
import {request} from "@/stores/request";

export default {
  name: "CreateIncoming",

  components: {
    Card,
  },

  data() {
    return {
      request,
      form: {
        target_topics: [],
        method: 'POST',
        payload: '{"test": "Hello World!"}',
      },
      isPayloadValid: true,
      isTopicValid: true,
      topic: '',
      examplePayload: '',
    }
  },

  methods: {
    onSubmit() {
      if (!this.form.payload || this.form.target_topics.length === 0) {
        this.$notifications.notify({
          message: 'Please enter all required fields.',
          horizontalAlign: 'right',
          verticalAlign: 'top',
          type: 'danger'
        });
        return;
      }
      const path = '/api/incoming-packages';
      this.request.axiosInstance.post(path, this.form)
        .then((response) => {
          this.$notifications.notify({
            message: "Success create an incoming with id " + response.data.id,
            horizontalAlign: 'right',
            verticalAlign: 'top',
            type: 'success'
          });
          this.form.target_topics = [];
          this.form.payload = '{"test": "Hello World!"}';
          this.createExamplePayload();
        }).catch((error) => {
          this.$notifications.notify({
            message: error.response.data.message || 'Error when creating an incoming',
            horizontalAlign: 'right',
            verticalAlign: 'top',
            type: 'danger'
          });
        })
    },

    onCancel() {
      this.$router.push('/admin/incoming-package');
    },

    onAddTopic() {
      if (!this.topic) {
        this.isTopicValid = false;
        return;
      }
      this.isTopicValid = true;
      if (this.form.target_topics.includes(this.topic)) {
        this.topic = '';
        return;
      }
      this.form.target_topics.push(this.topic);
      this.topic = '';
      this.createExamplePayload();
    },

    onResetTopic() {
      this.form.target_topics = [];
      this.createExamplePayload();
    },

    createExamplePayload() {
      try {
        this.isPayloadValid = true;
        JSON.parse(this.form.payload);
      } catch (JSONException) {
        this.isPayloadValid = false;
        return;
      }
      this.examplePayload = JSON.stringify(this.form);
    }
  }
}
</script>

<style scoped>

</style>
