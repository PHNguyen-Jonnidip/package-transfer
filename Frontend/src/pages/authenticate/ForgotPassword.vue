<template>
  <card class="container w-50 shadow margin-top">
    <form>
      <h3>Forgot Password</h3>
      <div class="form-group">
        <label>Username</label>
        <input type="text" class="form-control form-control-lg" required v-model="username"/>
      </div>
      <button type="submit" class="btn btn-dark btn-lg btn-block">Reset password</button>
    </form>
  </card>
</template>
<script>
export default {
  name: 'ForgotPassword',

  data() {
    return {
      username: ''
    }
  }
}
</script>
