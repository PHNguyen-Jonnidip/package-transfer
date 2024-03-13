<template>
  <nav class="navbar navbar-expand-lg">
    <div class="container-fluid">
      <a class="navbar-brand" @click="goToUserProfile">
        {{user.first_name}} {{user.last_name}}</a>
      <button type="button"
              class="navbar-toggler navbar-toggler-right"
              :class="{toggled: $sidebar.showSidebar}"
              aria-controls="navigation-index"
              aria-expanded="false"
              aria-label="Toggle navigation"
              @click="toggleNotificationDropDown">
        <span class="navbar-toggler-bar burger-lines"></span>
        <span class="navbar-toggler-bar burger-lines"></span>
        <span class="navbar-toggler-bar burger-lines"></span>
      </button>
      <div class="navbar-collapse justify-content-end" :class="{ 'collapse': activeNotifications }">
<!--        <ul class="nav navbar-nav mr-auto">-->
          <!-- <li class="nav-item">
            <a class="nav-link" href="#" data-toggle="dropdown">
              <i class="nc-icon nc-palette"></i>
            </a>
          </li> -->
<!--          <base-dropdown tag="li">-->
<!--            <template slot="title">-->
<!--              <i class="nc-icon nc-planet"></i>-->
<!--              <b class="caret"></b>-->
<!--              <span class="notification">3</span>-->
<!--            </template>-->
<!--            <a class="dropdown-item" href="#">Notification 1</a>-->
<!--            <a class="dropdown-item" href="#">Notification 2</a>-->
<!--            <a class="dropdown-item" href="#">Notification 3</a>-->
<!--          </base-dropdown>-->
<!--          <li class="nav-item">-->
<!--            <a href="#" class="nav-link">-->
<!--              <i class="nc-icon nc-zoom-split"></i>-->
<!--              <span class="d-lg-block">&nbsp;Search</span>-->
<!--            </a>-->
<!--          </li>-->
<!--        </ul>-->
<!--        <ul class="navbar-nav ml-auto">-->
<!--          <base-dropdown title="Account">-->
<!--            <a class="dropdown-item" href="#">Action</a>-->
<!--            <a class="dropdown-item" href="#">Another action</a>-->
<!--            <a class="dropdown-item" href="#">Something</a>-->
<!--            <a class="dropdown-item" href="#">Another action</a>-->
<!--            <a class="dropdown-item" href="#">Something</a>-->
<!--            <div class="divider"></div>-->
<!--            <a class="dropdown-item logout" @click="logout()">Log out</a>-->
<!--          </base-dropdown>-->
<!--        </ul>-->
        <a class="navbar-nav ml-auto logout" @click="logout"><i class="fas fa-sign-out-alt text-danger"></i></a>
      </div>
    </div>
  </nav>
</template>
<script>
import { authentication } from '@/stores/authentication'
import {request} from "@/stores/request";
export default {
    computed: {
      routeName () {
        const {name} = this.$route
        return this.capitalizeFirstLetter(name)
      }
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
        },
        activeNotifications: false,
        authentication,
      }
    },
    methods: {
      capitalizeFirstLetter (string) {
        return string.charAt(0).toUpperCase() + string.slice(1)
      },
      toggleNotificationDropDown () {
        this.activeNotifications = !this.activeNotifications
      },
      closeDropDown () {
        this.activeNotifications = false
      },
      toggleSidebar () {
        this.$sidebar.displaySidebar(!this.$sidebar.showSidebar)
      },
      hideSidebar () {
        this.$sidebar.displaySidebar(false)
      },
      logout() {
        if (confirm('Are you sure?')) {
          authentication.logoutUser()
        }
      },
      goToUserProfile() {
        this.$router.push('/admin/user')
      },

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
    },

    mounted() {
      this.getProfile()
    }
  }

</script>
<style>
.navbar-brand:hover {
  cursor: pointer;
}
.logout:hover {
  cursor: pointer;
}
</style>
