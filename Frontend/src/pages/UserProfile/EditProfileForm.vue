<template>
  <card>
    <h4 slot="header" class="card-title">Edit Profile</h4>
    <form>
      <div class="row">
        <div class="col-md-6">
          <base-input type="text"
                      label="First Name"
                      placeholder="First Name"
                      v-model="user.first_name">
          </base-input>
        </div>
        <div class="col-md-6">
          <base-input type="text"
                      label="Last Name"
                      placeholder="Last Name"
                      v-model="user.last_name">
          </base-input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-7">
          <base-input type="email"
                      label="Email"
                      placeholder="Email"
                      disabled
                      v-model="user.email">
          </base-input>
        </div>
        <div class="col-md-5">
          <base-input type="text"
                      label="Username"
                      placeholder="Username"
                      disabled
                      v-model="user.username">
          </base-input>
        </div>
      </div>

      <div class="text-center">
        <button type="submit" class="btn btn-info btn-fill float-right" @click.prevent="updateProfile">
          Update Profile
        </button>
      </div>
      <div class="clearfix"></div>
    </form>
  </card>
</template>
<script>
  import Card from 'src/components/Cards/Card.vue'
  import { request} from "@/stores/request";

  export default {
    components: {
      Card
    },
    data () {
      return {
        auth: request,
        user: {
          id: '',
          username: '',
          email: '',
          first_name: '',
          last_name: '',
        }
      }
    },
    methods: {
      getProfile() {
        this.auth.axiosInstance.get('api/users/me')
          .then((response) =>{
            this.user = response.data;
          }).catch( (error) => {
          this.$notifications.notify({
            message: error.response.data.message || "Can't get user data please try again",
            horizontalAlign: 'right',
            verticalAlign: 'top',
            type: 'danger'
          });
        })
      },

      updateProfile () {
        const path = `api/users/${this.user.id}`;
        this.auth.axiosInstance.put(path, this.user).then(() => {
          this.$notifications.notify({
            message: 'Update user successfully',
            horizontalAlign: 'right',
            verticalAlign: 'top',
            type: 'success'
          });
          this.$router.push('/');
        }).catch((error) => {
          this.$notifications.notify({
            message: error.response.data.message || "Can't update user please try again",
            horizontalAlign: 'right',
            verticalAlign: 'top',
            type: 'danger'
          })
        })
      }
    },

    mounted() {
      this.getProfile();
    }
  }

</script>
<style>

</style>
