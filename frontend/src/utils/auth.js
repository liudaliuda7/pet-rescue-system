export function getUser() {
  const s = localStorage.getItem('user')
  try { return s ? JSON.parse(s) : null } catch { return null }
}
export function setAuth(token, user) {
  localStorage.setItem('token', token)
  localStorage.setItem('user', JSON.stringify(user))
}
export function clearAuth() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
}
export function isLoggedIn() { return !!localStorage.getItem('token') }
