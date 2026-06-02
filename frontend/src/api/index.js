import request from '@/utils/request'

export const auth = {
  login: d => request.post('/auth/login', d),
  register: d => request.post('/auth/register', d),
  logout: () => request.post('/auth/logout'),
  info: () => request.get('/auth/info')
}

const crud = (base) => ({
  page: p => request.get(base + '/page', { params: p }),
  get: id => request.get(base + '/' + id),
  add: d => request.post(base, d),
  update: d => request.put(base, d),
  del: id => request.delete(base + '/' + id)
})

export const userApi = {
  ...crud('/user'),
  updateProfile: d => request.put('/user/profile', d),
  changePassword: d => request.put('/user/password', d)
}
export const stationApi = { ...crud('/station'), list: () => request.get('/station/list'), publicList: () => request.get('/station/public/list'), publicGet: id => request.get('/station/public/' + id), activities: id => request.get('/station/public/' + id + '/activities') }
export const animalTypeApi = { ...crud('/animal-type'), list: () => request.get('/animal-type/list'), publicList: () => request.get('/animal-type/public/list') }
export const animalApi = {
  ...crud('/animal'),
  publicPage: p => request.get('/animal/public/page', { params: p }),
  publicGet: id => request.get('/animal/public/' + id),
  publicByStation: p => request.get('/animal/public/station/' + p.stationId, { params: { current: p.current, size: p.size } }),
  stats: () => request.get('/animal/stats')
}
export const helpApi = {
  ...crud('/help'),
  publicPage: p => request.get('/help/public/page', { params: p }),
  publicGet: id => request.get('/help/public/' + id),
  assign: d => request.put('/help/assign', d),
  stats: () => request.get('/help/stats')
}
export const helpRecordApi = crud('/help-record')
export const adoptionApi = { ...crud('/adoption'), audit: d => request.put('/adoption/audit', d) }
export const visitApi = { ...crud('/visit'), countByAdoption: adoptionId => request.get('/visit/count', { params: { adoptionId } }) }
export const healthApi = {
  ...crud('/health'),
  publicList: p => request.get('/health/public/list', { params: p })
}
export const noticeApi = {
  ...crud('/notice'),
  publicPage: p => request.get('/notice/public/page', { params: p }),
  publicGet: id => request.get('/notice/public/' + id),
  latest: () => request.get('/notice/public/latest')
}
export const statsApi = { dashboard: () => request.get('/stats/dashboard'), station: id => request.get('/stats/station/' + id) }
export const systemApi = { info: () => request.get('/system/info') }
export const reviewApi = {
  publicPage: p => request.get('/review/public/station/' + p.stationId, { params: { current: p.current, size: p.size } }),
  add: d => request.post('/review', d),
  check: stationId => request.get('/review/check', { params: { stationId } })
}
export const uploadUrl = '/api/upload'
