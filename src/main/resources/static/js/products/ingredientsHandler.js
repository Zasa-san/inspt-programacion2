/**
 * UI de receta por ingredientes: checkbox habilita cantidad.
 * La cantidad 0 implica "no usar".
 */
$(() => {
  const $rows = $('input.ingredient-enabled').closest('tr');
  if (!$rows.length) return;

  const syncRow = ($row) => {
    const $check = $row.find('input.ingredient-enabled');
    const $qty = $row.find('input.ingredient-qty');
    const enabled = $check.prop('checked');

    if (!enabled) {
      $qty.val(0);
      $row.addClass('has-background-light');
    } else {
      $row.removeClass('has-background-light');
      if (parseInt($qty.val() || '0', 10) === 0) {
        $qty.val(1);
      }
    }
  };

  // init
  $rows.each((_, tr) => syncRow($(tr)));

  // toggle
  $(document).on('change', 'input.ingredient-enabled', function () {
    syncRow($(this).closest('tr'));
  });

  // si el usuario escribe cantidad, tildar automáticamente
  $(document).on('input', 'input.ingredient-qty', function () {
    const $row = $(this).closest('tr');
    const $check = $row.find('input.ingredient-enabled');
    const qty = parseInt($(this).val() || '0', 10);
    $check.prop('checked', qty > 0);
    syncRow($row);
  });
});

