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

    // Sumar radio buttons seleccionados (ignorar los de valor vacío = "Sin selección")
    $customizacionesContainer.find('input[type="radio"]:checked').each(function () {
      if ($(this).val()) {
        const precio = parseInt($(this).data('price')) || 0;
        extrasTotal += precio;
      }
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
    
    // Recoger radio buttons seleccionados (ignorar los vacíos)
    $customizacionesContainer.find('input[type="radio"]:checked').each(function () {
      const val = $(this).val();
      if (val) {
        selectedIds.push(parseInt(val));
      }
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

    // Generar inputs de customizaciones agrupados por "grupo"
    $customizacionesContainer.empty();

    if (currentCustomizaciones.length === 0) {
      $customizacionesContainer.html('<p class="has-text-grey">Este producto no tiene extras disponibles.</p>');
    } else {
      // Agrupar por "grupo"
      const grupos = {};
      currentCustomizaciones.forEach(custom => {
        const grupo = custom.grupo || 'Extra';
        if (!grupos[grupo]) {
          grupos[grupo] = {
            tipo: custom.tipo, // Asumimos que todas las del mismo grupo tienen el mismo tipo
            items: []
          };
        }
        grupos[grupo].items.push(custom);
      });

      // Renderizar cada grupo
      Object.keys(grupos).forEach(grupoNombre => {
        const grupoData = grupos[grupoNombre];
        const esUnica = grupoData.tipo === 'UNICA';
        
        const $grupoSection = $('<div class="mb-4"></div>');
        
        // Título del grupo con icono según tipo
        const icono = esUnica ? 'fa-dot-circle' : 'fa-check-square';
        $grupoSection.append(`
          <p class="has-text-weight-semibold mb-2">
            <span class="icon"><i class="fas ${icono}"></i></span> 
            ${grupoNombre}
          </p>
        `);

        if (esUnica) {
          // UNICA: Radio buttons - solo uno seleccionable por grupo
          // La primera opción se selecciona por defecto
          const radioName = `grupo_${productId}_${grupoNombre.replace(/\s+/g, '_')}`;

          grupoData.items.forEach((custom, index) => {
            const precioDisplay = custom.priceModifier > 0 
              ? `+$${formatPrice(custom.priceModifier)}` 
              : '$0,00';
            const tagClass = custom.priceModifier > 0 ? 'is-warning' : 'is-light';
            const isChecked = index === 0 ? 'checked' : '';
            
            const $item = $(`
              <label class="radio is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: pointer;">
                <input type="radio" name="${radioName}" value="${custom.id}" data-price="${custom.priceModifier}" ${isChecked} />
                <span class="ml-2">${custom.nombre}</span>
                <span class="tag ${tagClass} is-light ml-2">${precioDisplay}</span>
              </label>
            `);
            $grupoSection.append($item);
          });
        } else {
          // MULTIPLE: Checkboxes - múltiples seleccionables
          grupoData.items.forEach(custom => {
            const precioDisplay = custom.priceModifier > 0 
              ? `+$${formatPrice(custom.priceModifier)}` 
              : '$0,00';
            const tagClass = custom.priceModifier > 0 ? 'is-info' : 'is-light';
            
            const $item = $(`
              <label class="checkbox is-block mb-2 p-2 has-background-light" style="border-radius: 4px; cursor: pointer;">
                <input type="checkbox" value="${custom.id}" data-price="${custom.priceModifier}" />
                <span class="ml-2">${custom.nombre}</span>
                <span class="tag ${tagClass} is-light ml-2">${precioDisplay}</span>
              </label>
            `);
            $grupoSection.append($item);
          });
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
    updatePrices();
    updateSelectedIds();
  });
});
