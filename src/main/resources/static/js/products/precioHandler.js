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
  const $gruposContainer = $('#gruposContainer');

  if ($priceInput.length === 0 || $priceHidden.length === 0) return;

  const $form = $priceInput.closest('form');
  if ($form.length === 0) return;

  let lastAutoPrice = null;

  const parseNonNegativeInt = (value) => {
    const parsed = parseInt((value ?? '').toString(), 10);
    if (!Number.isFinite(parsed)) return 0;
    return Math.max(parsed, 0);
  }

  const calcularPrecioSugerido = () => {
    if ($gruposContainer.length === 0) {
      return parseNonNegativeInt($priceHidden.val());
    }

    let total = 0;

    $gruposContainer.find('.grupo-item').each(function () {
      const $group = $(this);
      const tipo = (($group.find('.grupo-tipo').val() ?? '').toString().trim().toUpperCase());

      $group.find('.ingrediente-item').each(function () {
        const $row = $(this);
        const itemId = parseInt(($row.find('.ingrediente-itemSelect').val() ?? '').toString(), 10);
        if (!Number.isFinite(itemId)) return;

        const cantidad = parseNonNegativeInt($row.find('.ingrediente-cantidad').val());
        const $selected = $row.find('.ingrediente-itemSelect option:selected').first();
        const precioItem = parseNonNegativeInt($selected.attr('data-price'));
        const seleccionadoPorDefecto = !!$row.find('.ingrediente-default').prop('checked');

        const incluir = tipo === 'OBLIGATORIO' || seleccionadoPorDefecto;
        if (!incluir) return;

        total += precioItem * cantidad;
      });
    });

    return total;
  }

  const actualizarPrecioSugerido = (forzarActualizacion = false) => {
    const sugerido = calcularPrecioSugerido();
    const actualIngresado = parsePrice(($priceInput.val() ?? '').toString());
    const estaVacio = (($priceInput.val() ?? '').toString().trim() === '');
    const debeAutocompletar = forzarActualizacion || estaVacio || actualIngresado === lastAutoPrice;

    if (debeAutocompletar) {
      $priceInput.val(formatPrice(sugerido));
      $priceHidden.val(sugerido);
    }

    lastAutoPrice = sugerido;
  }

  document.addEventListener('product-groups-changed', () => {
    actualizarPrecioSugerido(false);
  });

  $priceInput.on('input change', function () {
    const centavos = parsePrice(($priceInput.val() ?? '').toString());
    $priceHidden.val(centavos);
  });

  $form.on('submit', function (e) {
    const priceValue = ($priceInput.val() ?? '').toString().trim();
    if (!priceValue && lastAutoPrice != null) {
      $priceInput.val(formatPrice(lastAutoPrice));
      $priceHidden.val(lastAutoPrice);
      return;
    }

    const centavos = parsePrice(priceValue);
    $priceHidden.val(centavos);
  });

  actualizarPrecioSugerido(true);
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

  const $priceHidden = $('#priceHidden');
  if ($priceHidden.length > 0) {
    const initial = parseInt($priceHidden.val());
    if (!isNaN(initial)) {
      loadInitialPrice(initial);
    }
  }
});
