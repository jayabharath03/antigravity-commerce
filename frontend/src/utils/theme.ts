const KEY = 'theme';

/** Apply the saved theme on app start (call before/at render to avoid a flash). */
export function initTheme() {
  if (localStorage.getItem(KEY) === 'dark') {
    document.documentElement.classList.add('dark');
  }
}

export function isDark(): boolean {
  return document.documentElement.classList.contains('dark');
}

/** Toggle light/dark, persist the choice, and return the new state. */
export function toggleTheme(): boolean {
  const dark = document.documentElement.classList.toggle('dark');
  localStorage.setItem(KEY, dark ? 'dark' : 'light');
  return dark;
}
