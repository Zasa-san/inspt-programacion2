/**
 * UI de bundle: checkbox habilita cantidad.
 * La cantidad 0 implica "no incluir".
 */
$(() => {
  const $rows = $('input.bundle-enabled').closest('tr');
  if (!$rows.length) return;

  const syncRow = ($row) => {
    const $check = $row.find('input.bundle-enabled');
    const $qty = $row.find('input.bundle-qty');
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

  $rows.each((_, tr) => syncRow($(tr)));

  $(document).on('change', 'input.bundle-enabled', function () {
    syncRow($(this).closest('tr'));
  });

  $(document).on('input', 'input.bundle-qty', function () {
    const $row = $(this).closest('tr');
    const $check = $row.find('input.bundle-enabled');
    const qty = parseInt($(this).val() || '0', 10);
    $check.prop('checked', qty > 0);
    syncRow($row);
  });
});

