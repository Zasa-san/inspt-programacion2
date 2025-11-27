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
  $container.on('input change', '.customization-name, .customization-price', function () {
    const $item = $(this).closest('.customization-item');
    const $nameInput = $item.find('.customization-name');
    const $priceInput = $item.find('.customization-price');
    const $errorMsg = $item.find('.customization-error');

    const nombre = $nameInput.val().trim();
    const precio = $priceInput.val().trim();

    // Validar: si tiene uno, debe tener el otro
    if ((nombre && !precio) || (!nombre && precio)) {
      $nameInput.addClass('is-danger');
      $priceInput.addClass('is-danger');
      $errorMsg.show();
    } else {
      $nameInput.removeClass('is-danger');
      $priceInput.removeClass('is-danger');
      $errorMsg.hide();
    }
  });

  // Serializar customizaciones al enviar el formulario
  $form.on('submit', (e) => {
    // Validar que customizaciones con contenido tengan nombre Y precio
    let isValid = true;
    $container.find('.customization-item').each((index, item) => {
      const $item = $(item);
      const $nameInput = $item.find('.customization-name');
      const $priceInput = $item.find('.customization-price');
      const $errorMsg = $item.find('.customization-error');

      const nombre = $nameInput.val().trim();
      const precio = $priceInput.val().trim();

      // Si tiene uno pero no el otro, es inválido
      if ((nombre && !precio) || (!nombre && precio)) {
        isValid = false;
        $nameInput.addClass('is-danger');
        $priceInput.addClass('is-danger');
        $errorMsg.show();
      } else {
        $nameInput.removeClass('is-danger');
        $priceInput.removeClass('is-danger');
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

      const nombre = $nameInput.val().trim();
      const priceModifier = parseInt($priceInput.val()) || 0;
      const enabled = $enabledCheckbox.prop('checked');

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
        enabled
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
