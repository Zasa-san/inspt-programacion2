const getStored = (key) => {
  try { return localStorage.getItem(key); } catch (e) { return null; }
}

const store = (key, v) => {
  try { localStorage.setItem(key, v); } catch (e) { }
}

const detectTheme = () => {
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) return 'dark';
  return 'light';
}

const applyTheme = ($doc, theme) => {
  $doc.attr('data-theme', theme);
  const isDark = theme === 'dark';
  $('#darkModeToggle').attr('aria-pressed', isDark ? 'true' : 'false');
}

const darkModeInit = ($) => {
  const KEY = 'kfc-theme';
  const $doc = $(document.documentElement);

  const stored = getStored(KEY);
  const initial = stored || detectTheme();
  applyTheme($doc, initial);

  $('#darkModeToggle').on('click', (e) => {
    e.preventDefault();
    const current = $doc.attr('data-theme') || getStored(KEY) || detectTheme();
    const next = current === 'dark' ? 'light' : 'dark';
    applyTheme($doc, next);
    store(KEY, next);
  });
}

darkModeInit(jQuery);
