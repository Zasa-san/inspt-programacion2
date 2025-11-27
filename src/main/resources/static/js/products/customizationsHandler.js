/**
 * Serializa las customizaciones en formato JSON antes de enviar el formulario
 */
$(() => {
  const $form = $('form[action*="/products/"]');

  if (!$form.length) { return };

  $form.on('submit', (e) => {
    const customizations = [];
    let newCounter = 0;

    // Buscar todos los boxes de customización
    const $customizationBoxes = $form.find('.box[style*="background-color"]');

    $customizationBoxes.each((index, box) => {
      const $box = $(box);
      const $checkbox = $box.find('input[type="checkbox"][name="customizationEnabled"]');
      const $idInput = $box.find('input[name="customizationIds"]');
      const $nameInput = $box.find('input[name="customizationNames"]');
      const $priceInput = $box.find('input[name="customizationPrices"]');

      if (!$nameInput.length || !$priceInput.length) { return };

      const enabled = $checkbox.length ? $checkbox.prop('checked') : false;
      const nombre = $nameInput.val().trim();
      const priceModifier = parseInt($priceInput.val()) || 0;

      // Determinar el ID: si tiene valor en el hidden, es existente; sino es nueva
      let id;
      if ($idInput.length && $idInput.val() && $idInput.val().trim() !== '') {
        // Customización existente
        id = $idInput.val();
      } else {
        // Nueva customización
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

    // Crear un campo hidden con el JSON
    $form.find('input[name="customizationsJson"]').remove();

    const $jsonInput = $('<input>', {
      type: 'hidden',
      name: 'customizationsJson',
      value: JSON.stringify(customizations)
    });
    $form.append($jsonInput);

    // Deshabilitar los campos viejos para evitar conflictos
    $form.find('input[name="customizationEnabled"]').prop('disabled', true);
    $form.find('input[name="customizationIds"]').prop('disabled', true);
    $form.find('input[name="customizationNames"]').prop('disabled', true);
    $form.find('input[name="customizationPrices"]').prop('disabled', true);
  });
});
