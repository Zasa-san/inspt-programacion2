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

  const $icon = $('#darkModeToggle i');
  if (isDark) {
    $icon.removeClass('fa-lightbulb').addClass('fa-moon');
  } else {
    $icon.removeClass('fa-moon').addClass('fa-lightbulb');
  }
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

const navbarBurgerInit = ($) => {
  $('.navbar-burger').on('click', function () {
    const $burger = $(this);
    const target = $burger.data('target');
    const $target = $('#' + target);

    $burger.toggleClass('is-active');
    $target.toggleClass('is-active');
  });
}

const notificationDeleteInit = ($) => {
  $(document).on('click', '.notification .delete', function () {
    $(this).parent('.notification').remove();
  });
}

const scrollPreserveInit = ($) => {
  const KEY = 'kfc-scroll-pos';

  $(document).on('submit', 'form', function (e) {
    try {
      const action = $(this).attr('action') || '';
      // normalize action to path-only in case it's absolute
      const path = action.startsWith('http') ? new URL(action, window.location.origin).pathname : action;
      if (path && path.indexOf('/cart') === 0) {
        sessionStorage.setItem(KEY, String(window.scrollY || window.pageYOffset || 0));
      }
    } catch (err) {
      // ignore errors
    }
  });

  $(document).ready(() => {
    try {
      const v = sessionStorage.getItem(KEY);
      if (v !== null) {
        const pos = parseInt(v, 10) || 0;
        window.scrollTo(0, pos);
        sessionStorage.removeItem(KEY);
      }
    } catch (err) {
      // ignore
    }
  });
}

darkModeInit(jQuery);
navbarBurgerInit(jQuery);
notificationDeleteInit(jQuery);
scrollPreserveInit(jQuery);
