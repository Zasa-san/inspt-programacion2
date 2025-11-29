/**
 * Manejo del modal de customizaciones para agregar productos al carrito
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
  let currentCustomizaciones = [];

  // Función para formatear precio (centavos a pesos)
  function formatPrice(centavos) {
    return (centavos / 100).toLocaleString('es-AR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  // Actualizar precios mostrados en el modal
  function updatePrices() {
    let extrasTotal = 0;

    // Sumar checkboxes seleccionados
    $customizacionesContainer.find('input[type="checkbox"]:checked').each(function () {
      const precio = parseInt($(this).data('price')) || 0;
      extrasTotal += precio;
    });

    // Sumar radio buttons seleccionados
    $customizacionesContainer.find('input[type="radio"]:checked').each(function () {
      const precio = parseInt($(this).data('price')) || 0;
      extrasTotal += precio;
    });

    const totalUnitario = currentProductPrice + extrasTotal;

    $modalBasePrice.attr('data-price', currentProductPrice).text(formatPrice(currentProductPrice));
    $modalExtrasPrice.attr('data-price', extrasTotal).text(formatPrice(extrasTotal));
    $modalTotalPrice.attr('data-price', totalUnitario).text(formatPrice(totalUnitario));
  }

  // Actualizar input hidden con IDs seleccionados
  function updateSelectedIds() {
    const selectedIds = [];
    
    // Recoger checkboxes seleccionados
    $customizacionesContainer.find('input[type="checkbox"]:checked').each(function () {
      selectedIds.push(parseInt($(this).val()));
    });
    
    // Recoger radio buttons seleccionados
    $customizacionesContainer.find('input[type="radio"]:checked').each(function () {
      selectedIds.push(parseInt($(this).val()));
    });
    
    $customizacionesIdsInput.val(JSON.stringify(selectedIds));
  }

  // Abrir modal
  $('.open-customization-modal').on('click', function () {
    const $btn = $(this);
    const productId = $btn.data('product-id');
    const productName = $btn.data('product-name');
    const productPrice = parseInt($btn.data('product-price')) || 0;

    // Leer customizaciones del data-attribute (JSON válido del backend)
    try {
      const customizacionesJson = $btn.attr('data-customizaciones');
      currentCustomizaciones = customizacionesJson ? JSON.parse(customizacionesJson) : [];
    } catch (e) {
      console.error('Error parseando customizaciones JSON:', e);
      currentCustomizaciones = [];
    }

    currentProductPrice = productPrice;

    // Setear valores en el modal
    $modalProductName.text(productName);
    $modalProductId.val(productId);
    $modalQuantity.val(1);

    // Generar inputs de customizaciones
    $customizacionesContainer.empty();

    if (currentCustomizaciones.length === 0) {
      $customizacionesContainer.html('<p class="has-text-grey">Este producto no tiene extras disponibles.</p>');
    } else {
      // Separar por tipo
      const unicasItems = currentCustomizaciones.filter(c => c.tipo === 'UNICA');
      const multiplesItems = currentCustomizaciones.filter(c => c.tipo === 'MULTIPLE');

      // Renderizar UNICA como radio buttons (solo uno seleccionable)
      if (unicasItems.length > 0) {
        const $unicaSection = $('<div class="mb-4"></div>');
        $unicaSection.append('<p class="has-text-weight-semibold mb-2"><span class="icon"><i class="fas fa-check-circle"></i></span> Elige una opción:</p>');
        
        // Opción "Sin selección" para tipo UNICA
        const $noneOption = $(`
          <label class="radio is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: pointer;">
            <input type="radio" name="unica_${productId}" value="" data-price="0" checked />
            <span class="ml-2">Sin selección</span>
            <span class="tag is-light ml-2">+$0,00</span>
          </label>
        `);
        $unicaSection.append($noneOption);

        unicasItems.forEach(custom => {
          const precioDisplay = `+$${formatPrice(custom.priceModifier)}`;
          const $item = $(`
            <label class="radio is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: pointer;">
              <input type="radio" name="unica_${productId}" value="${custom.id}" data-price="${custom.priceModifier}" />
              <span class="ml-2">${custom.nombre}</span>
              <span class="tag is-warning is-light ml-2">${precioDisplay}</span>
            </label>
          `);
          $unicaSection.append($item);
        });

        $customizacionesContainer.append($unicaSection);
      }

      // Renderizar MULTIPLE como checkboxes (múltiples seleccionables)
      if (multiplesItems.length > 0) {
        const $multipleSection = $('<div></div>');
        $multipleSection.append('<p class="has-text-weight-semibold mb-2"><span class="icon"><i class="fas fa-plus-square"></i></span> Extras adicionales:</p>');

        multiplesItems.forEach(custom => {
          const precioDisplay = `+$${formatPrice(custom.priceModifier)}`;
          const $item = $(`
            <label class="checkbox is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: pointer;">
              <input type="checkbox" value="${custom.id}" data-price="${custom.priceModifier}" />
              <span class="ml-2">${custom.nombre}</span>
              <span class="tag is-info is-light ml-2">${precioDisplay}</span>
            </label>
          `);
          $multipleSection.append($item);
        });

        $customizacionesContainer.append($multipleSection);
      }
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
    updatePrices();
    updateSelectedIds();
  });
});
