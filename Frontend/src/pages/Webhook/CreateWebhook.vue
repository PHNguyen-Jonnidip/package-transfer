<template>
  <div class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <card>
            <template slot="header">
              <h4 class="card-title">Create a Webhook</h4>
            </template>

            <form>
              <div class="row">
                <div class="col-md-8">
                  <base-input type="text"
                              label="Callback URL"
                              placeholder="https://webhook.site/"
                              v-model="form.callback_url"
                              required>
                  </base-input>
                </div>
                <div class="col-md-4">
                  <base-input type="text"
                              label="Topic"
                              placeholder="my-topic"
                              v-model="form.topic"
                              required>
                  </base-input>
                </div>
              </div>
              <div class="row">
                <div class="col-md-4">
                  <label class="control-label">
                    Security Type <i class="text-danger">*</i>
                  </label>
                  <select class="custom-select" id="securityTypeSelect" v-model="form.security_type">
                    <option value="NONE">NONE</option>
                    <option value="BASIC">BASIC</option>
                    <option value="API_KEY">API_KEY</option>
                    <option value="OAUTH2">OAUTH2</option>
                  </select>
                </div>
              </div>
              <div class="row" v-if="form.security_type === 'BASIC'">
                <div class="col-md-6">
                  <base-input type="text"
                              label="Auth Username"
                              placeholder="username"
                              required
                              v-model="form.basic_auth_username">
                  </base-input>
                </div>
                <div class="col-md-6">
                  <base-input type="password"
                              label="Auth Password"
                              placeholder="password"
                              required
                              v-model="form.basic_auth_password">
                  </base-input>
                </div>
              </div>
              <div class="row" v-if="form.security_type === 'API_KEY'">
                <div class="col-md-6">
                  <base-input type="password"
                              label="Api Key Secret"
                              placeholder="secret"
                              required
                              v-model="form.api_key_secret">
                  </base-input>
                </div>
                <div class="col-md-3">
                  <base-input type="text"
                              label="Api Key Custom Header"
                              placeholder="Custom Header"
                              v-model="form.api_key_custom_header"
                              tooltip="Default is Authorization">
                  </base-input>
                </div>
                <div class="col-md-3">
                  <base-input type="text"
                              label="Api Key Prefix"
                              placeholder="Bearer"
                              v-model="form.api_key_prefix"
                              tooltip="Default is Bearer">
                  </base-input>
                </div>
              </div>
              <div v-if="form.security_type === 'OAUTH2'">
                <div class="row">
                  <div class="col-md-8">
                    <base-input type="text"
                                label="Oauth2 Access Token URL"
                                placeholder="Token URL"
                                v-model="form.oauth2_access_token_url"
                                required>
                    </base-input>
                  </div>
                  <div class="col-md-5">
<!--                    <base-input type="text"-->
<!--                                label="Oauth2 Scope"-->
<!--                                placeholder="scope"-->
<!--                                v-model="form.oauth2_scope">-->
<!--                    </base-input>-->
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-6">
                    <base-input type="text"
                                label="Oauth2 Client ID"
                                placeholder="Client ID"
                                v-model="form.oauth2_client_id"
                                required>
                    </base-input>
                  </div>
                  <div class="col-md-6">
                    <base-input type="password"
                                label="Oauth2 Client Secret"
                                placeholder="Client Secret"
                                v-model="form.oauth2_client_secret"
                                required>
                    </base-input>
                  </div>
                </div>
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
import {request} from "@/stores/request";

export default {
  name: "CreateWebhook",

  data() {
    return {
      auth: request,
      form: {
        callback_url: '',
        topic: '',
        security_type: 'NONE',
        basic_auth_username: null,
        basic_auth_password: null,
        api_key_custom_header: null,
        api_key_prefix: null,
        api_key_secret: null,
        oauth2_access_token_url: null,
        oauth2_client_id: null,
        oauth2_client_secret: null,
        oauth2_scope: null,
      }
    }
  },

  methods: {
    onSubmit() {
      if (!this.form.callback_url || !this.form.topic) {
        this.$notifications.notify({
          message: 'Please enter all required fields.',
          horizontalAlign: 'right',
          verticalAlign: 'top',
          type: 'danger'
        });
        return;
      }
      const path = '/api/webhooks';
      this.auth.axiosInstance.post(path, {
        ...this.form
      }).then(() => {
        this.$notifications.notify( {
          message: `Successfully create new webhook with callback url: ${this.form.callback_url}`,
          horizontalAlign: 'right',
          verticalAlign: 'top',
          type: 'success'
        });
        this.$router.push('/admin/webhook');
      }).catch( (error) => {
        this.$notifications.notify({
          message: error.response.data.message || "Can't create webhook please try again",
          horizontalAlign: 'right',
          verticalAlign: 'top',
          type: 'danger'
        });
      })
    },

    onCancel() {
      this.$router.push('/admin/webhook');
    }
  },
}
</script>

<style scoped>

</style>
