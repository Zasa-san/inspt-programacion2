/**
 * Manejo dinámico de customizaciones: agregar, eliminar, serializar
 */
$(() => {
  const $form = $('form[action*="/products/"]');
  const $container = $('#customizationsContainer');
  const $addBtn = $('#addCustomizationBtn');
  const $noMessage = $('#noCustomizationsMessage');

  if (!$form.length || !$container.length) return;

  // Obtener template del DOM
  const $template = $('#customizationTemplate');

  // Agregar nueva customización
  $addBtn.on('click', () => {
    $noMessage.hide();
    const $newItem = $template.children().first().clone();
    $container.append($newItem);
  });

  // Validación en tiempo real: mostrar/ocultar mensajes de error
  $container.on('input change', '.customization-name, .customization-price, .customization-grupo', function () {
    const $item = $(this).closest('.customization-item');
    validateCustomizationItem($item);
  });

  // Función de validación de un item
  function validateCustomizationItem($item) {
    const $nameInput = $item.find('.customization-name');
    const $priceInput = $item.find('.customization-price');
    const $grupoInput = $item.find('.customization-grupo');
    const $errorMsg = $item.find('.customization-error');

    const nombre = $nameInput.val().trim();
    const precio = $priceInput.val().trim();
    const precioNum = parseInt(precio) || 0;
    const grupo = $grupoInput.val().trim();

    let hasError = false;
    let errorText = '';

    // Si tiene nombre, validar todos los campos
    if (nombre) {
      if (!precio) {
        hasError = true;
        errorText = 'El precio es requerido';
        $priceInput.addClass('is-danger');
      } else {
        $priceInput.removeClass('is-danger');
      }

      if (precioNum < 0) {
        hasError = true;
        errorText = 'El precio no puede ser negativo';
        $priceInput.addClass('is-danger');
        $priceInput.val(0);
      }

      if (!grupo) {
        hasError = true;
        errorText = 'El grupo es requerido';
        $grupoInput.addClass('is-danger');
      } else {
        $grupoInput.removeClass('is-danger');
      }
    } else {
      // Sin nombre, limpiar errores
      $priceInput.removeClass('is-danger');
      $grupoInput.removeClass('is-danger');
    }

    if (hasError) {
      $nameInput.addClass('is-danger');
      $errorMsg.text(errorText).show();
    } else {
      $nameInput.removeClass('is-danger');
      $errorMsg.hide();
    }

    return !hasError;
  }

  // Serializar customizaciones al enviar el formulario
  $form.on('submit', (e) => {
    let isValid = true;

    $container.find('.customization-item').each((index, item) => {
      const $item = $(item);
      const $nameInput = $item.find('.customization-name');
      const $priceInput = $item.find('.customization-price');
      const $grupoInput = $item.find('.customization-grupo');
      const $errorMsg = $item.find('.customization-error');
      const $enabledCheckbox = $item.find('.customization-enabled');

      const nombre = $nameInput.val().trim();
      const precio = $priceInput.val().trim();
      const precioNum = parseInt(precio) || 0;
      const grupo = $grupoInput.val().trim();
      const enabled = $enabledCheckbox.prop('checked');

      // Solo validar si tiene nombre o está habilitada
      if (!nombre && !enabled) return;

      let hasError = false;
      let errorText = '';

      // Limpiar estilos previos
      $nameInput.removeClass('is-danger');
      $priceInput.removeClass('is-danger');
      $grupoInput.removeClass('is-danger');

      if (!nombre) {
        hasError = true;
        errorText = 'El nombre es requerido';
        $nameInput.addClass('is-danger');
      }

      if (!precio && nombre) {
        hasError = true;
        errorText = 'El precio es requerido';
        $priceInput.addClass('is-danger');
      }

      if (precioNum < 0) {
        hasError = true;
        errorText = 'El precio no puede ser negativo';
        $priceInput.addClass('is-danger');
      }

      if (!grupo && nombre) {
        hasError = true;
        errorText = 'El grupo es requerido';
        $grupoInput.addClass('is-danger');
      }

      if (hasError) {
        isValid = false;
        $errorMsg.text(errorText).show();
      } else {
        $errorMsg.hide();
      }
    });

    if (!isValid) {
      e.preventDefault();
      // Hacer scroll al primer error
      const $firstError = $container.find('.customization-error:visible').first();
      if ($firstError.length) {
        $firstError[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
      return false;
    }

    const customizations = [];
    let newCounter = 0;

    $container.find('.customization-item').each((index, item) => {
      const $item = $(item);
      const $idInput = $item.find('.customization-id');
      const $nameInput = $item.find('.customization-name');
      const $priceInput = $item.find('.customization-price');
      const $enabledCheckbox = $item.find('.customization-enabled');
      const $tipoSelect = $item.find('.customization-tipo');
      const $grupoInput = $item.find('.customization-grupo');

      const nombre = $nameInput.val().trim();
      const priceModifier = parseInt($priceInput.val()) || 0;
      const enabled = $enabledCheckbox.prop('checked');
      const tipo = $tipoSelect.val() || 'MULTIPLE';
      const grupo = $grupoInput.val().trim();

      // Si no tiene nombre y no está habilitada, ignorar (nueva vacía)
      if (!nombre && !enabled) return;

      // Determinar el ID: si tiene valor, es existente; sino es nueva
      let id;
      if ($idInput.val() && $idInput.val().trim() !== '') {
        id = $idInput.val();
      } else {
        id = `NEW_${newCounter}`;
        newCounter++;
      }

      customizations.push({
        id,
        nombre,
        priceModifier,
        enabled,
        tipo,
        grupo
      });
    });

    // Crear campo hidden con el JSON
    $form.find('input[name="customizationsJson"]').remove();

    const $jsonInput = $('<input>', {
      type: 'hidden',
      name: 'customizationsJson',
      value: JSON.stringify(customizations)
    });
    $form.append($jsonInput);
  });
});
