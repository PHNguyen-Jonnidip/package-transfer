<template>
  <component :is="tag"
             @click.native="hideSidebar"
             v-bind="$attrs"
             tag="li">
    <a class="nav-link" v-bind="$attrs">
      <slot>
        <span class="material-icons-outlined">
        home
        </span>
        <p>{{link.name}}</p>
      </slot>
    </a>
  </component>
</template>
<script>
  export default {
    inheritAttrs: false,
    inject: {
      autoClose: {
        default: true
      }
    },
    props: {
      link: {
        type: [String, Object],
        default: () => {
          return {
            name: '',
            path: '',
            icon: ''
          }
        }
      },
      tag: {
        type: String,
        default: 'router-link'
      }
    },
    methods: {
      hideSidebar () {
        if (this.autoClose && this.$sidebar && this.$sidebar.showSidebar === true) {
          this.$sidebar.displaySidebar(false)
        }
      }
    },
    mounted() {
    }
  }
</script>
<style>
</style>
