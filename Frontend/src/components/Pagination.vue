<template>
  <nav aria-label="Page navigation example">
    <ul class="pagination m-3 float-right">
      <li class="page-item" :class="{disabled: isFirstPage}">
        <a class="page-link" @click="onChangePage(activePage - 1)">
          <i class="nc-icon nc-stre-left"></i>
        </a>
      </li>
      <li v-for="page in availablePage" :key="page" class="page-item" :class="{active: activePage === page}">
        <a class="page-link" @click="onChangePage(page)">{{ page }}</a>
      </li>
      <li class="page-item" :class="{disabled: isLastPage}">
        <a class="page-link" @click="onChangePage(activePage + 1)">
          <i class="nc-icon nc-stre-right"></i>
        </a>
      </li>
    </ul>
  </nav>
</template>

<script>
export default {
  name: "pagination",

  props: {
    page: Number,
    lastPage: Number,
  },

  data() {
    return {
      activePage: this.page,
      isFirstPage: true,
      isLastPage: true,
      availablePage: [],
    }
  },

  methods: {
    isFirstOrLastPage() {
      this.isFirstPage = this.activePage === 1;
      this.isLastPage = this.activePage === this.lastPage;
    },

    onChangePage(page) {
      if (page < 1 || page > this.lastPage) {
        return
      }
      this.activePage = page;
    },

    calculateAvailablePage() {
      if (this.lastPage <= 5) {
        this.availablePage = Array.from({length: this.lastPage}, (_, index) => index + 1);
      } else if (this.activePage >= this.lastPage - 2) {
        this.availablePage = Array.from({length: 5}, (_, index) => this.lastPage - 4 + index)
      } else if (this.activePage <= 3) {
        this.availablePage = Array.from({length: 5}, (_, index) => index + 1);
      } else {
        this.availablePage = Array.from({length: 5}, (_, index) => this.activePage - 2 + index)
      }
    },

  },

  watch: {
    lastPage(newLastPage) {
      this.lastPage = newLastPage
      this.calculateAvailablePage();
      this.isFirstOrLastPage()
    },

    activePage(newActivePage) {
      this.isFirstOrLastPage();
      this.calculateAvailablePage();
      this.$emit('onChangePage', newActivePage)
    }
  },

  mounted() {
    this.calculateAvailablePage();
    this.isFirstOrLastPage()
  }
}
</script>

<style scoped>
.page-link {
  user-select: none;
}

.page-link:hover {
  cursor: pointer;
}

</style>
