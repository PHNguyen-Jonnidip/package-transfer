<template>
  <card class="container w-50 shadow margin-top">
    <form @submit.prevent="onSignup">
      <h3>Create An Account</h3>
      <div class="form-group">
        <label>First Name <i class="text-danger">*</i></label>
        <input type="text" class="form-control form-control-lg" required v-model="firstName"/>
      </div>
      <div class="form-group">
        <label>Last Name <i class="text-danger">*</i></label>
        <input type="text" class="form-control form-control-lg" required v-model="lastName"/>
      </div>
      <div class="form-group">
        <label>Email address <i class="text-danger">*</i></label>
        <input type="email" class="form-control form-control-lg" required v-model="email"/>
      </div>
      <div class="form-group">
        <label>Username <i class="text-danger">*</i></label>
        <input type="text" class="form-control form-control-lg" required v-model="username"/>
      </div>
      <div class="form-group">
        <label>Password <i class="text-danger">*</i></label>
        <input type="password" class="form-control form-control-lg" required v-model="password"/>
      </div>
      <button type="submit" class="btn btn-dark btn-lg btn-block">Sign Up</button>
      <p class="forgot-password text-right">
        Already registered?
        <router-link to="/login">Sign in</router-link>
      </p>
    </form>
  </card>
</template>

<script lang="ts">
import {request} from "@/stores/request";

export default {
  name: 'SignUp',

  data() {
    return {
      request,
      firstName: '',
      lastName: '',
      email: '',
      username: '',
      password: '',
    }
  },

  methods: {
    async onSignup(e) {
      console.log(this.firstName, this.lastName, this.email, this.username, this.password)
      const path = '/api/auth/sign-up'
      await this.request.axiosInstance.post(path, {
          email: this.email,
          first_name: this.firstName,
          last_name: this.lastName,
          username: this.username,
          password: this.password,
        }).then(() => {
          this.$notifications.notify({
            message: `User with username ${this.username} has been created`,
            horizontalAlign: 'right',
            verticalAlign: 'top',
            type: 'success'
          })
          this.$router.push('/login')
        }).catch((error) => {
          this.$notifications.notify({
            message: error.response.data.message || "Can't sign up user please try again",
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
