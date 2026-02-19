/**
 * Manejo del modal de customizaciones para agregar productos al carrito
 * Agrupa las opciones por "grupo" y usa radio/checkbox según "tipo"
 */
$(() => {
  const $modal = $('#customizationModal');
  const $modalProductName = $('#modalProductName');
  const $modalProductId = $('#modalProductId');
  const $modalQuantity = $('#modalQuantity');
  const $customizacionesContainer = $('#customizacionesContainer');
  const $customizacionesIdsInput = $('#customizacionesIdsInput');
  const $modalBasePrice = $('#modalBasePrice');
  const $modalExtrasPrice = $('#modalExtrasPrice');
  const $modalTotalPrice = $('#modalTotalPrice');

  let currentProductPrice = 0;
  let currentGrupos = [];
  let currentDefaultTotal = 0;

  // Función para formatear precio (centavos a pesos)
  function formatPrice(centavos) {
    return (centavos / 100).toLocaleString('es-AR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  // Actualizar precios mostrados en el modal
  function updatePrices() {
    let selectedTotal = 0;

    $customizacionesContainer.find('input[type="checkbox"]:checked, input[type="radio"]:checked').each(function () {
      if (!$(this).val()) return; // "Sin selección"
      const precio = parseInt($(this).data('price')) || 0;
      selectedTotal += precio;
    });

    let ajuste = selectedTotal - (currentDefaultTotal || 0);
    if (ajuste < 0) {
      // Evitar mostrar "extras" negativos; el modelo del producto se ajusta en otra etapa.
      ajuste = 0;
    }

    const totalUnitario = currentProductPrice + ajuste;

    $modalBasePrice.attr('data-price', currentProductPrice).text(formatPrice(currentProductPrice));
    $modalExtrasPrice.attr('data-price', ajuste).text(formatPrice(ajuste));
    $modalTotalPrice.attr('data-price', totalUnitario).text(formatPrice(totalUnitario));
  }

  // Actualizar input hidden con IDs seleccionados
  function updateSelectedIds() {
    const selectedIds = [];

    $customizacionesContainer.find('input[type="checkbox"]:checked, input[type="radio"]:checked').each(function () {
      const val = $(this).val();
      if (!val) return;
      selectedIds.push(parseInt(val));
    });

    $customizacionesIdsInput.val(JSON.stringify(selectedIds));
  }

  function computeDefaultTotal(grupos) {
    let total = 0;

    (grupos || []).forEach(grupo => {
      if (!grupo || !Array.isArray(grupo.ingredientes)) return;
      const tipo = (grupo.tipo || '').toUpperCase();
      const ingredientes = grupo.ingredientes;

      const defaults = ingredientes.filter(i => i && i.seleccionadoPorDefecto);
      const sumIngrediente = (i) => {
        const unit = parseInt(i.itemPrice) || 0;
        const cant = parseInt(i.cantidad) || 1;
        return unit * cant;
      };

      if (tipo === 'OPCIONAL_UNICO' || tipo === 'OBLIGATORIO') {
        const d = defaults.length ? defaults[0] : null;
        if (d) total += sumIngrediente(d);
      } else {
        defaults.forEach(d => { total += sumIngrediente(d); });
      }
    });

    return total;
  }



  // Abrir modal
  $('.open-customization-modal').on('click', function () {
    const $btn = $(this);
    const productId = $btn.data('product-id');
    const productName = $btn.data('product-name');
    const productPrice = parseInt($btn.data('product-price')) || 0;

    // Leer grupos/ingredientes del data-attribute (JSON válido del backend)
    try {
      const gruposJson = $btn.attr('data-grupos');
      currentGrupos = gruposJson ? JSON.parse(gruposJson) : [];
    } catch (e) {
      console.error('Error parseando grupos JSON:', e);
      currentGrupos = [];
    }

    currentProductPrice = productPrice;
    currentDefaultTotal = computeDefaultTotal(currentGrupos);

    // Setear valores en el modal
    $modalProductName.text(productName);
    $modalProductId.val(productId);
    $modalQuantity.val(1);

    // Generar inputs agrupados por grupo (OBLIGATORIO / OPCIONAL_*)
    $customizacionesContainer.empty();

    if (!currentGrupos || currentGrupos.length === 0) {
      $customizacionesContainer.html('<p class="has-text-grey">Este producto no tiene opciones disponibles.</p>');
    } else {
      currentGrupos.forEach((grupo, grupoIndex) => {
        if (!grupo || !Array.isArray(grupo.ingredientes)) return;

        const grupoNombre = grupo.nombre || 'Grupo';
        const tipo = (grupo.tipo || '').toUpperCase();
        const ingredientes = grupo.ingredientes.filter(i => i != null);

        // Determinar tipo de input para el grupo (basado solo en tipo)
        const esUnico = tipo === 'OPCIONAL_UNICO' || tipo === 'OBLIGATORIO';
        const icono = esUnico ? 'fa-dot-circle' : 'fa-check-square';

        const $grupoSection = $(`<div class="mb-4" data-grupo-index="${grupoIndex}"></div>`);
        $grupoSection.append(`
          <p class="has-text-weight-semibold mb-2">
            <span class="icon"><i class="fas ${icono}"></i></span>
            ${grupoNombre}
          </p>
        `);

        const radioName = `grupo_${productId}_${grupoIndex}`;

        if (esUnico) {
          const allowEmpty = minSeleccion === 0 && tipo !== 'OBLIGATORIO';

          if (allowEmpty) {
            const $none = $(`
              <label class="radio is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: pointer;">
                <input type="radio" name="${radioName}" value="" data-price="0" checked />
                <span class="ml-2">Sin selección</span>
                <span class="tag is-light ml-2">$0,00</span>
              </label>
            `);
            $grupoSection.append($none);
          }

          let checkedSet = false;
          ingredientes.forEach((ing) => {
            const unit = parseInt(ing.itemPrice) || 0;
            const cant = parseInt(ing.cantidad) || 1;
            const total = unit * cant;

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

          // Si el grupo es obligatorio y nada quedó chequeado (no había defaults),
          // seleccionar el primer ingrediente.
          if (tipo === 'OBLIGATORIO') {
            const $checked = $grupoSection.find('input[type="radio"]:checked');
            if (!$checked.length) {
              $grupoSection.find('input[type="radio"]').first().prop('checked', true);
            }
          }
        } else {
          ingredientes.forEach((ing) => {
            const unit = parseInt(ing.itemPrice) || 0;
            const cant = parseInt(ing.cantidad) || 1;
            const total = unit * cant;

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

          // Asegurar mínimos al abrir
          enforceMinMax($grupoSection);
        }

        $customizacionesContainer.append($grupoSection);
      });
    }

    // Resetear y actualizar precios
    updatePrices();
    updateSelectedIds();

    // Mostrar modal
    $modal.addClass('is-active');
  });

  // Cerrar modal
  function closeModal() {
    $modal.removeClass('is-active');
  }

  $('#closeModalBtn, #cancelModalBtn, .modal-background').on('click', closeModal);

  // Tecla ESC para cerrar
  $(document).on('keydown', function (e) {
    if (e.key === 'Escape' && $modal.hasClass('is-active')) {
      closeModal();
    }
  });

  // Actualizar precios cuando cambian checkboxes o radio buttons
  $customizacionesContainer.on('change', 'input[type="checkbox"], input[type="radio"]', function () {
    const $groupRoot = $(this).closest('[data-grupo-index]');
    if ($groupRoot.length) {
      enforceMinMax($groupRoot);
    }
    updatePrices();
    updateSelectedIds();
  });
});
