<template>
  <card class="container w-50 shadow margin-top">
    <form @submit.prevent="onLogin">
      <h3>Sign In</h3>
      <div class="form-group">
        <label>Username</label>
        <input type="text" class="form-control form-control-lg" required v-model="username"/>
      </div>
      <div class="form-group">
        <label>Password</label>
        <input type="password" class="form-control form-control-lg" required v-model="password"/>
      </div>
      <p class="forgot-password text-right">
        Doesn't have account yet?
        <router-link to="/sign-up">Create an account</router-link>
      </p>
      <button type="submit" class="btn btn-dark btn-lg btn-block">Sign In</button>
      <!--      <p class="forgot-password text-right mt-2 mb-4">-->
      <!--        <router-link to="/forgot-password">Forgot password ?</router-link>-->
      <!--      </p>-->
    </form>
  </card>
</template>

<script lang="ts">
import {authentication} from "../../stores/authentication"
import {request} from "@/stores/request";

export default {
  name: 'Login',

  data() {
    return {
      authentication,
      request,
      username: '',
      password: '',
    }
  },

  methods: {

    onLogin(e) {
      this.request.axiosInstance.post('/api/auth/sign-in', {
        username: this.username,
        password: this.password
      }).then((response) => {
        this.authentication.loginUser(response.data.token)
      }).catch((error) => {
        this.$notifications.notify({
          message: error.response.data.message || "Can't login user please try again",
          horizontalAlign: 'right',
          verticalAlign: 'top',
          type: 'danger'
        })
      })
    }
  },

  mounted() {
  }
}
</script>
