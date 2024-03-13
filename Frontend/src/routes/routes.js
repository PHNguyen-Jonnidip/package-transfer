import {authentication} from "@/stores/authentication";
import DashboardLayout from '../layout/DashboardLayout.vue'
// GeneralViews
import NotFound from '../pages/NotFoundPage.vue'
import Login from 'src/pages/authenticate/Login.vue'
import ForgotPassword from "@/pages/authenticate/ForgotPassword";
import SignUp from "@/pages/authenticate/SignUp";

// Admin pages
import Overview from 'src/pages/Overview.vue'
import UserProfile from 'src/pages/UserProfile.vue'
import TableList from 'src/pages/TableList.vue'
import Typography from 'src/pages/Typography.vue'
import Icons from 'src/pages/Icons.vue'
import Notifications from 'src/pages/Notifications.vue'
import Upgrade from 'src/pages/Upgrade.vue'
import VueRouter from "vue-router";
import IncomingPackageTable from '../pages/IncomingPackageTable'
import OutgoingPackageTable from "@/pages/OutgoingPackageTable";
import WebhookTable from "@/pages/Webhook/WebhookTable";
import HttpResponseLogTable from "@/pages/HttpResponseLogTable";
import CreateWebhook from "@/pages/Webhook/CreateWebhook";
import UpdateWebhook from "@/pages/Webhook/UpdateWebhook";
import CreateIncoming from "@/pages/CreateIncoming";

const routes = [
  {
    path: '/login',
    component: Login,
    name: 'Login',
  },
  {
    path: '/forgot-password',
    component: ForgotPassword,
    name: 'ForgotPassword',
  },
  {
    path: '/sign-up',
    component: SignUp,
    name: 'SignUp',
  },
  {
    path: '/',
    component: DashboardLayout,
    redirect: '/admin/overview'
  },
  {
    path: '/admin',
    component: DashboardLayout,
    redirect: '/admin/overview',
    children: [
      {
        path: 'overview',
        name: 'Overview',
        component: Overview
      },
      {
        path: 'user',
        name: 'User',
        component: UserProfile
      },
      {
        path: 'table-list',
        name: 'Table List',
        component: TableList
      },
      {
        path: 'typography',
        name: 'Typography',
        component: Typography
      },
      {
        path: 'icons',
        name: 'Icons',
        component: Icons
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: Notifications
      },
      {
        path: 'upgrade',
        name: 'Upgrade to PRO',
        component: Upgrade
      },
      {
        path: 'incoming-package',
        name: 'IncomingPackageTable',
        component: IncomingPackageTable
      },
      {
        path: 'incoming-package/new',
        name: 'CreateIncoming',
        component: CreateIncoming
      },
      {
        path: 'outgoing-package',
        name: 'OutgoingPackageTable',
        component: OutgoingPackageTable
      },
      {
        path: 'webhook',
        name: 'WebhookTable',
        component: WebhookTable
      },
      {
        path: 'webhook/new',
        name: 'CreateWebhook',
        component: CreateWebhook
      },
      {
        path: 'webhook/:id',
        name: 'UpdateWebhook',
        component: UpdateWebhook
      },
      {
        path: 'http-response-log',
        name: 'HttpResponseLogTable',
        component: HttpResponseLogTable
      }
    ]
  },
  { path: '*', component: NotFound }
]

/**
 * Asynchronously load view (Webpack Lazy loading compatible)
 * The specified component must be inside the Views folder
 * @param  {string} name  the filename (basename) of the view to load.
function view(name) {
   var res= require('../components/Dashboard/Views/' + name + '.vue');
   return res;
};**/


// configure router
const router = new VueRouter({
  mode: 'history',
  routes, // short for routes: routes
  linkActiveClass: "nav-item active",
  scrollBehavior (to, from, savedPosition) {
    if (to.hash) {
      return to.hash
    } else {
      return { x: 0, y: 0 }
    }
  }
});

const authRoutes = [
  '/login', '/sign-up', '/forgot-password',
]

router.beforeEach((to, from, next) => {
  if (!authRoutes.includes(to.path)) {
    if (authentication.isLoggedIn()) {
      next();
    } else {
      sessionStorage.setItem('redirect', to.path);
      authentication.logoutUser();
      next('/login');
    }
  } else {
    if (authentication.isLoggedIn()) {
      next('/');
    } else {
      next();
    }
  }
})

export default router;
