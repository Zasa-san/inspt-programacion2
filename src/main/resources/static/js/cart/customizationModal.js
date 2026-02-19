/**
 * Manejo del modal de ingredientes para agregar productos al carrito.
 *
 * - OBLIGATORIO: muestra checkboxes seleccionados y deshabilitados (base del producto).
 * - OPCIONAL_UNICO: radios con "Sin seleccion" por defecto si no hay default.
 * - OPCIONAL_MULTIPLE: checkboxes.
 *
 * El backend trabaja con `ingredientesIds` (ids de Ingrediente).
 */
$(() => {
  const $modal = $('#customizationModal');
  const $modalProductName = $('#modalProductName');
  const $modalProductId = $('#modalProductId');
  const $modalQuantity = $('#modalQuantity');
  const $container = $('#customizacionesContainer');
  const $idsInput = $('#customizacionesIdsInput');
  const $modalBasePrice = $('#modalBasePrice');
  const $modalExtrasPrice = $('#modalExtrasPrice');
  const $modalTotalPrice = $('#modalTotalPrice');

  let currentProductPrice = 0;
  let currentGrupos = [];
  let currentDefaultTotal = 0;

  function formatPrice(centavos) {
    return (centavos / 100).toLocaleString('es-AR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  function precioIngrediente(ing) {
    if (!ing) return 0;
    const unit = parseInt(ing.itemPrice) || 0;
    const cant = parseInt(ing.cantidad) || 1;
    return unit * cant;
  }

  function computeDefaultTotal(grupos) {
    let total = 0;

    (grupos || []).forEach(grupo => {
      if (!grupo || !Array.isArray(grupo.ingredientes)) return;

      const tipo = (grupo.tipo || '').toUpperCase();
      const ingredientes = grupo.ingredientes.filter(i => i != null);
      const defaults = ingredientes.filter(i => i && i.seleccionadoPorDefecto);

      if (tipo === 'OBLIGATORIO') {
        const selected = defaults.length ? defaults : ingredientes;
        selected.forEach(i => { total += precioIngrediente(i); });
        return;
      }

      if (tipo === 'OPCIONAL_UNICO') {
        const d = defaults.length ? defaults[0] : null;
        total += precioIngrediente(d);
        return;
      }

      // OPCIONAL_MULTIPLE
      defaults.forEach(i => { total += precioIngrediente(i); });
    });

    return total;
  }

  function updateSelectedIds() {
    const selectedIds = [];

    $container.find('input[type="checkbox"]:checked, input[type="radio"]:checked').each(function () {
      const val = $(this).val();
      if (!val) return;
      selectedIds.push(parseInt(val));
    });

    $idsInput.val(JSON.stringify(selectedIds));
  }

  function updatePrices() {
    let selectedTotal = 0;

    $container.find('input[type="checkbox"]:checked, input[type="radio"]:checked').each(function () {
      if (!$(this).val()) return; // "Sin seleccion"
      selectedTotal += parseInt($(this).data('price')) || 0;
    });

    let extras = selectedTotal - (currentDefaultTotal || 0);
    if (extras < 0) extras = 0;

    const totalUnitario = currentProductPrice + extras;

    $modalBasePrice.attr('data-price', currentProductPrice).text(formatPrice(currentProductPrice));
    $modalExtrasPrice.attr('data-price', extras).text(formatPrice(extras));
    $modalTotalPrice.attr('data-price', totalUnitario).text(formatPrice(totalUnitario));
  }

  function closeModal() {
    $modal.removeClass('is-active');
  }

  function renderGrupoObligatorio($grupoSection, ingredientes) {
    const defaults = ingredientes.filter(i => i && i.seleccionadoPorDefecto);
    const selected = defaults.length ? defaults : ingredientes;

    selected.forEach((ing) => {
      const total = precioIngrediente(ing);
      const label = ing.itemName || `Item ${ing.itemId || ''}`;

      const $item = $(`
        <label class="checkbox is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: default;">
          <input type="checkbox" value="${ing.id}" data-price="${total}" checked disabled />
          <span class="ml-2">${label}</span>
          <span class="tag is-success is-light ml-2">Incluido</span>
        </label>
      `);
      $grupoSection.append($item);
    });
  }

  function renderGrupoOpcionalUnico($grupoSection, radioName, ingredientes) {
    let checkedSet = false;

    const $none = $(`
      <label class="radio is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: pointer;">
        <input type="radio" name="${radioName}" value="" data-price="0" />
        <span class="ml-2">Sin selecciÃ³n</span>
        <span class="tag is-light ml-2">$0,00</span>
      </label>
    `);
    $grupoSection.append($none);

    ingredientes.forEach((ing) => {
      const total = precioIngrediente(ing);
      const precioDisplay = total > 0 ? `+$${formatPrice(total)}` : '$0,00';
      const tagClass = total > 0 ? 'is-warning' : 'is-light';

      let checked = '';
      if (!checkedSet && ing.seleccionadoPorDefecto) {
        checked = 'checked';
        checkedSet = true;
      }

      const label = ing.itemName || `Item ${ing.itemId || ''}`;
      const $item = $(`
        <label class="radio is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: pointer;">
          <input type="radio" name="${radioName}" value="${ing.id}" data-price="${total}" ${checked} />
          <span class="ml-2">${label}</span>
          <span class="tag ${tagClass} is-light ml-2">${precioDisplay}</span>
        </label>
      `);
      $grupoSection.append($item);
    });

    if (!checkedSet) {
      $grupoSection.find('input[type="radio"][value=""]').first().prop('checked', true);
    }
  }

  function renderGrupoOpcionalMultiple($grupoSection, ingredientes) {
    ingredientes.forEach((ing) => {
      const total = precioIngrediente(ing);
      const precioDisplay = total > 0 ? `+$${formatPrice(total)}` : '$0,00';
      const tagClass = total > 0 ? 'is-info' : 'is-light';
      const checked = ing.seleccionadoPorDefecto ? 'checked' : '';
      const label = ing.itemName || `Item ${ing.itemId || ''}`;

      const $item = $(`
        <label class="checkbox is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: pointer;">
          <input type="checkbox" value="${ing.id}" data-price="${total}" ${checked} />
          <span class="ml-2">${label}</span>
          <span class="tag ${tagClass} is-light ml-2">${precioDisplay}</span>
        </label>
      `);
      $grupoSection.append($item);
    });
  }

  $('.open-customization-modal').on('click', function () {
    const $btn = $(this);
    const productId = $btn.data('product-id');
    const productName = $btn.data('product-name');
    const productPrice = parseInt($btn.data('product-price')) || 0;

    try {
      const gruposJson = $btn.attr('data-grupos');
      currentGrupos = gruposJson ? JSON.parse(gruposJson) : [];
    } catch (e) {
      console.error('Error parseando grupos JSON:', e);
      currentGrupos = [];
    }

    currentProductPrice = productPrice;
    currentDefaultTotal = computeDefaultTotal(currentGrupos);

    $modalProductName.text(productName);
    $modalProductId.val(productId);
    $modalQuantity.val(1);

    $container.empty();

    if (!currentGrupos || currentGrupos.length === 0) {
      $container.html('<p class="has-text-grey">Este producto no tiene opciones disponibles.</p>');
    } else {
      currentGrupos.forEach((grupo, grupoIndex) => {
        if (!grupo || !Array.isArray(grupo.ingredientes)) return;

        const grupoNombre = grupo.nombre || 'Grupo';
        const tipo = (grupo.tipo || '').toUpperCase();
        const ingredientes = grupo.ingredientes.filter(i => i != null);

        const icono = (tipo === 'OPCIONAL_UNICO') ? 'fa-dot-circle' : 'fa-check-square';

        const $grupoSection = $(`<div class="mb-4" data-grupo-index="${grupoIndex}"></div>`);
        $grupoSection.append(`
          <p class="has-text-weight-semibold mb-2">
            <span class="icon"><i class="fas ${icono}"></i></span>
            ${grupoNombre}
          </p>
        `);

        const radioName = `grupo_${productId}_${grupoIndex}`;

        if (tipo === 'OBLIGATORIO') {
          renderGrupoObligatorio($grupoSection, ingredientes);
        } else if (tipo === 'OPCIONAL_UNICO') {
          renderGrupoOpcionalUnico($grupoSection, radioName, ingredientes);
        } else {
          renderGrupoOpcionalMultiple($grupoSection, ingredientes);
        }

        $container.append($grupoSection);
      });
    }

    updatePrices();
    updateSelectedIds();
    $modal.addClass('is-active');
  });

  $('#closeModalBtn, #cancelModalBtn, .modal-background').on('click', closeModal);

  $(document).on('keydown', function (e) {
    if (e.key === 'Escape' && $modal.hasClass('is-active')) {
      closeModal();
    }
  });

  $container.on('change', 'input[type="checkbox"], input[type="radio"]', function () {
    updatePrices();
    updateSelectedIds();
  });
});

