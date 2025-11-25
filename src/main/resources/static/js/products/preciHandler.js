/**
 * Convierte centavos a formato decimal con coma
 * @param {number} centavos - Precio en centavos (ej: 5500)
 * @returns {string} Precio formateado (ej: "55,00")
 */
const formatPrice = (centavos) => {
  const decimal = centavos / 100;
  return decimal.toFixed(2).replace('.', ',');
}

/**
 * Convierte un string con coma decimal a centavos
 * Maneja números con o sin coma, asegura 2 decimales
 * @param {string} precio - Precio (ej: "55", "55,5", "55,50", "55,567")
 * @returns {number} Precio en centavos (ej: 5500, 5550, 5550, 5556)
 */
const parsePrice = (precio) => {
  if (!precio || precio.trim() === '') return 0;

  // Reemplazar coma por punto para parseFloat
  let normalized = precio.trim().replace(',', '.');

  // Convertir a número
  let decimal = parseFloat(normalized);

  if (isNaN(decimal)) return 0;

  // Convertir a centavos y redondear
  return Math.round(decimal * 100);
}

/**
 * Formatea todos los elementos con clase 'price-format' en la página
 * Usa el atributo data-price para obtener el valor en centavos
 */
const formatPricesOnPage = () => {
  $('.price-format').each(function () {
    const $el = $(this);
    const dataPrice = $el.attr('data-price');
    const centavos = parseInt(dataPrice);

    if (!isNaN(centavos)) {
      const formatted = formatPrice(centavos);
      $el.text(formatted);
    }
  });
}

/**
 * Inicializa el manejo de precios en formularios de productos
 * Convierte el valor del input visible a centavos antes de enviar
 */
const initProductForm = () => {
  const $priceInput = $('#priceInput');
  const $priceHidden = $('#priceHidden');

  if ($priceInput.length === 0 || $priceHidden.length === 0) return;

  // Buscar el formulario que contiene el input
  const $form = $priceInput.closest('form');

  if ($form.length === 0) return;

  // Interceptar el submit
  $form.on('submit', function (e) {
    const priceValue = $priceInput.val().trim();

    if (priceValue) {
      const centavos = parsePrice(priceValue);
      $priceHidden.val(centavos);
    }
  });
}

/**
 * Carga el precio inicial en el formulario de edición
 * @param {number} initialPriceCentavos - Precio inicial en centavos
 */
const loadInitialPrice = (initialPriceCentavos) => {
  const $priceInput = $('#priceInput');

  if ($priceInput.length > 0 && !isNaN(initialPriceCentavos)) {
    $priceInput.val(formatPrice(initialPriceCentavos));
  }
}

// Inicialización automática cuando el DOM está listo
$(document).ready(function () {
  formatPricesOnPage();
  initProductForm();
  // Si existe un input hidden con el precio (en centavos) lo cargamos en el input visible
  const $priceHidden = $('#priceHidden');
  if ($priceHidden.length > 0) {
    const initial = parseInt($priceHidden.val());
    if (!isNaN(initial)) {
      loadInitialPrice(initial);
    }
  }
});
