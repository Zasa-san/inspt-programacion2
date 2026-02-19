/**
 * Serializa la UI de grupos/ingredientes al formato esperado por FrontProductoService.parseGruposIngredientes:
 * [
 *   { nombre, tipo, minSeleccion, maxSeleccion, ingredientes: [ { itemId, cantidad, seleccionadoPorDefecto } ] }
 * ]
 */
$(() => {
  const $form = $('form[action*="/products/"]');
  const $gruposContainer = $('#gruposContainer');
  const $noGruposMessage = $('#noGruposMessage');
  const $gruposJsonHidden = $('#gruposJsonHidden');
  const $itemsDatalist = $('#itemsDatalist');

  if (!$form.length || !$gruposContainer.length || !$gruposJsonHidden.length) return;

  const $grupoTemplate = $('#grupoTemplate').children().first();
  const $ingredienteTemplate = $('#ingredienteTemplate').children().first();

  function showGroupError($group, msg) {
    const $err = $group.find('.grupo-error').first();
    if (!$err.length) return;
    if (msg) $err.text(msg).show();
    else $err.hide().text('');
  }

  function showIngredientError($ingRow, msg) {
    const $err = $ingRow.find('.ingrediente-error').first();
    if (!$err.length) return;
    if (msg) $err.text(msg).show();
    else $err.hide().text('');
  }

  function parseIntOrZero(val) {
    const n = parseInt((val ?? '').toString(), 10);
    return Number.isFinite(n) ? n : 0;
  }

  function parseIntOrNull(val) {
    const s = (val ?? '').toString().trim();
    if (!s) return null;
    const n = parseInt(s, 10);
    return Number.isFinite(n) ? n : null;
  }

  function isValidTipo(tipo) {
    return tipo === 'OBLIGATORIO' || tipo === 'OPCIONAL_MULTIPLE' || tipo === 'OPCIONAL_UNICO';
  }

  function ensureAtLeastOneIngredient($group) {
    const $container = $group.find('.ingredientes-container').first();
    if (!$container.length) return;
    if ($container.find('.ingrediente-item').length === 0) {
      $container.append($ingredienteTemplate.clone());
    }
  }

  function findItemIdByName(name) {
    if (!$itemsDatalist.length) return null;
    const val = (name ?? '').toString().trim();
    if (!val) return null;
    const opt = $itemsDatalist.find('option').filter(function () {
      return (this.value || '').toString() === val;
    }).first();
    if (!opt.length) return null;
    const id = parseInt(opt.attr('data-id'), 10);
    return Number.isFinite(id) ? id : null;
  }

  function findItemNameById(id) {
    if (!$itemsDatalist.length) return null;
    const n = parseInt((id ?? '').toString(), 10);
    if (!Number.isFinite(n)) return null;
    const opt = $itemsDatalist.find('option').filter(function () {
      return parseInt($(this).attr('data-id'), 10) === n;
    }).first();
    return opt.length ? (opt.val() || '').toString() : null;
  }

  function validateAndBuild() {
    let ok = true;
    const grupos = [];

    $gruposContainer.find('.grupo-item').each(function () {
      const $g = $(this);
      showGroupError($g, null);
      $g.find('.ingrediente-item').each(function () {
        showIngredientError($(this), null);
      });
    });

    const $groups = $gruposContainer.find('.grupo-item');
    if ($groups.length === 0) {
      ok = false;
      $noGruposMessage.show();
      return { ok, grupos: [] };
    }

    $groups.each(function () {
      const $g = $(this);
      const nombre = ($g.find('.grupo-nombre').val() ?? '').toString().trim();
      const tipo = ($g.find('.grupo-tipo').val() ?? '').toString().trim().toUpperCase();
      const minSeleccion = parseIntOrZero($g.find('.grupo-min').val());
      const maxSeleccion = parseIntOrZero($g.find('.grupo-max').val());

      if (!nombre) {
        ok = false;
        showGroupError($g, 'El nombre del grupo es requerido.');
        return;
      }
      if (!isValidTipo(tipo)) {
        ok = false;
        showGroupError($g, 'Tipo de grupo inválido.');
        return;
      }
      if (minSeleccion < 0 || maxSeleccion < 0 || minSeleccion > maxSeleccion) {
        ok = false;
        showGroupError($g, 'Rango inválido: min/max deben ser >= 0 y min no puede ser mayor que max.');
        return;
      }

      const ingredientes = [];
      $g.find('.ingrediente-item').each(function () {
        const $row = $(this);
        const itemId = parseIntOrNull($row.find('.ingrediente-itemId').val());
        const cantidad = parseIntOrZero($row.find('.ingrediente-cantidad').val());
        const seleccionadoPorDefecto = !!$row.find('.ingrediente-default').prop('checked');

        if (itemId == null) return; // fila vacía
        if (cantidad <= 0) {
          ok = false;
          showIngredientError($row, 'Cantidad inválida (mínimo 1).');
          return;
        }

        ingredientes.push({ itemId, cantidad, seleccionadoPorDefecto });
      });

      if (ingredientes.length === 0) {
        ok = false;
        showGroupError($g, 'El grupo debe tener al menos 1 ingrediente con Item ID.');
        return;
      }

      grupos.push({ nombre, tipo, minSeleccion, maxSeleccion, ingredientes });
    });

    return { ok, grupos };
  }

  $('#addGrupoBtn').on('click', () => {
    $noGruposMessage.hide();
    const $newGroup = $grupoTemplate.clone();
    $gruposContainer.append($newGroup);
    ensureAtLeastOneIngredient($newGroup);
  });

  $gruposContainer.on('click', '.remove-grupo-btn', function () {
    $(this).closest('.grupo-item').remove();
    if ($gruposContainer.find('.grupo-item').length === 0) {
      $noGruposMessage.show();
    }
  });

  $gruposContainer.on('click', '.add-ingrediente-btn', function () {
    const $group = $(this).closest('.grupo-item');
    const $container = $group.find('.ingredientes-container').first();
    if (!$container.length) return;
    $container.append($ingredienteTemplate.clone());
  });

  $gruposContainer.on('click', '.remove-ingrediente-btn', function () {
    const $group = $(this).closest('.grupo-item');
    $(this).closest('.ingrediente-item').remove();
    ensureAtLeastOneIngredient($group);
  });

  // Cuando el usuario elige un nombre (datalist), completar el ID
  $gruposContainer.on('change', '.ingrediente-itemSearch', function () {
    const $row = $(this).closest('.ingrediente-item');
    const name = ($(this).val() ?? '').toString();
    const id = findItemIdByName(name);
    if (id != null) {
      $row.find('.ingrediente-itemId').val(id);
      showIngredientError($row, null);
    } else if (name.trim() !== '') {
      // Si escribió algo no válido, limpiar el ID para forzar corrección
      $row.find('.ingrediente-itemId').val('');
      showIngredientError($row, 'Seleccioná un item válido del listado.');
    }
  });

  // Si se cambia el ID a mano, intentar reflejar el nombre
  $gruposContainer.on('change', '.ingrediente-itemId', function () {
    const $row = $(this).closest('.ingrediente-item');
    const id = ($(this).val() ?? '').toString();
    const name = findItemNameById(id);
    if (name) {
      $row.find('.ingrediente-itemSearch').val(name);
      showIngredientError($row, null);
    }
  });

  $gruposContainer.find('.grupo-item').each(function () {
    ensureAtLeastOneIngredient($(this));
  });

  $form.on('submit', (e) => {
    const { ok, grupos } = validateAndBuild();
    if (!ok) {
      e.preventDefault();
      const $firstGroupErr = $gruposContainer.find('.grupo-error:visible').first();
      const $firstIngErr = $gruposContainer.find('.ingrediente-error:visible').first();
      const $target = $firstGroupErr.length ? $firstGroupErr : $firstIngErr;
      if ($target.length) {
        $target[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
      return false;
    }
    $gruposJsonHidden.val(JSON.stringify(grupos));
  });
});
