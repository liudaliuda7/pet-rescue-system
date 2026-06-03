import Vue from 'vue'
import VueRouter from 'vue-router'
import { isLoggedIn, getUser } from '@/utils/auth'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/FrontLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/front/Home.vue') },
      { path: 'help', name: 'help', component: () => import('@/views/front/HelpList.vue') },
      { path: 'help/submit', name: 'help-submit', component: () => import('@/views/front/HelpSubmit.vue'), meta: { auth: true } },
      { path: 'animal', name: 'animal', component: () => import('@/views/front/AnimalList.vue') },
      { path: 'animal/:id', name: 'animal-detail', component: () => import('@/views/front/AnimalDetail.vue') },
      { path: 'station/:id', name: 'station-detail', component: () => import('@/views/front/StationDetail.vue') },
      { path: 'notice', name: 'notice', component: () => import('@/views/front/NoticeList.vue') },
      { path: 'notice/:id', name: 'notice-detail', component: () => import('@/views/front/NoticeDetail.vue') },
      { path: 'profile', name: 'profile', component: () => import('@/views/front/Profile.vue'), meta: { auth: true } }
    ]
  },
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('@/views/Register.vue') },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { auth: true, role: 'admin' },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'admin-dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'user', name: 'admin-user', component: () => import('@/views/admin/UserMgmt.vue') },
      { path: 'station', name: 'admin-station', component: () => import('@/views/admin/StationMgmt.vue') },
      { path: 'help', name: 'admin-help', component: () => import('@/views/admin/HelpMgmt.vue') },
      { path: 'help-record', name: 'admin-help-record', component: () => import('@/views/admin/HelpRecordMgmt.vue') },
      { path: 'animal-type', name: 'admin-animal-type', component: () => import('@/views/admin/AnimalTypeMgmt.vue') },
      { path: 'animal', name: 'admin-animal', component: () => import('@/views/admin/AnimalMgmt.vue') },
      { path: 'claim', name: 'admin-claim', component: () => import('@/views/admin/ClaimMgmt.vue') },
      { path: 'adoption', name: 'admin-adoption', component: () => import('@/views/admin/AdoptionMgmt.vue') },
      { path: 'visit', name: 'admin-visit', component: () => import('@/views/admin/VisitMgmt.vue') },
      { path: 'health', name: 'admin-health', component: () => import('@/views/admin/HealthMgmt.vue') },
      { path: 'notice', name: 'admin-notice', component: () => import('@/views/admin/NoticeMgmt.vue') },
      { path: 'system', name: 'admin-system', component: () => import('@/views/admin/SystemInfo.vue') },
      { path: 'profile', name: 'admin-profile', component: () => import('@/views/common/ProfilePage.vue') }
    ]
  },
  {
    path: '/station',
    component: () => import('@/layouts/StationLayout.vue'),
    meta: { auth: true, role: 'station' },
    children: [
      { path: '', redirect: '/station/dashboard' },
      { path: 'dashboard', name: 'station-dashboard', component: () => import('@/views/station/Dashboard.vue') },
      { path: 'help', name: 'station-help', component: () => import('@/views/admin/HelpMgmt.vue') },
      { path: 'help-record', name: 'station-help-record', component: () => import('@/views/admin/HelpRecordMgmt.vue') },
      { path: 'animal', name: 'station-animal', component: () => import('@/views/admin/AnimalMgmt.vue') },
      { path: 'adoption', name: 'station-adoption', component: () => import('@/views/admin/AdoptionMgmt.vue') },
      { path: 'visit', name: 'station-visit', component: () => import('@/views/admin/VisitMgmt.vue') },
      { path: 'health', name: 'station-health', component: () => import('@/views/admin/HealthMgmt.vue') },
      { path: 'profile', name: 'station-profile', component: () => import('@/views/common/ProfilePage.vue') }
    ]
  },
  { path: '*', component: () => import('@/views/NotFound.vue') }
]

const router = new VueRouter({ mode: 'hash', routes })

router.beforeEach((to, from, next) => {
  if (to.matched.some(r => r.meta && r.meta.auth) && !isLoggedIn()) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }
  const need = to.matched.find(r => r.meta && r.meta.role)
  if (need) {
    const u = getUser()
    if (!u || u.role !== need.meta.role) {
      return next('/')
    }
  }
  next()
})

export default router
