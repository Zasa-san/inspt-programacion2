/**
 * Serializa la UI de grupos/ingredientes al formato esperado por FrontProductoService.parseGruposIngredientes:
 * [
 *   { nombre, tipo, ingredientes: [ { itemId, cantidad, seleccionadoPorDefecto } ] }
 * ]
 */
$(() => {
  const $form = $('form[action*="/products/"]');
  const $gruposContainer = $('#gruposContainer');
  const $noGruposMessage = $('#noGruposMessage');
  const $gruposJsonHidden = $('#gruposJsonHidden');

  if (!$form.length || !$gruposContainer.length || !$gruposJsonHidden.length) return;

  const $grupoTemplate = $('#grupoTemplate').children().first();
  const $ingredienteTemplate = $('#ingredienteTemplate').children().first();

  function notifyGroupsChanged() {
    document.dispatchEvent(new CustomEvent('product-groups-changed'));
  }

  function showGroupError($group, msg) {
    const $err = $group.find('.grupo-error').first();
    if (!$err.length) return;
    if (msg) {
      $err.text(msg);
      $err.prop('hidden', false);
    } else {
      $err.text('');
      $err.prop('hidden', true);
    }
  }

  function showIngredientError($ingRow, msg) {
    const $err = $ingRow.find('.ingrediente-error').first();
    if (!$err.length) return;
    if (msg) {
      $err.text(msg);
      $err.prop('hidden', false);
    } else {
      $err.text('');
      $err.prop('hidden', true);
    }
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

  function syncDefaultVisibilityByTipo($group) {
    const tipo = ($group.find('.grupo-tipo').val() ?? '').toString().trim().toUpperCase();
    const isObligatorio = tipo === 'OBLIGATORIO';

    $group.find('.ingrediente-item').each(function () {
      const $row = $(this);
      const $defaultColumn = $row.find('.ingrediente-default-column').first();
      const $defaultCheckbox = $row.find('.ingrediente-default').first();

      if (!$defaultColumn.length || !$defaultCheckbox.length) return;

      $defaultColumn.toggleClass('is-hidden', isObligatorio);

      if (isObligatorio) {
        $defaultCheckbox.prop('checked', false);
        $defaultCheckbox.prop('disabled', true);
      } else {
        $defaultCheckbox.prop('disabled', false);
      }
    });
  }

  function enforceSingleDefaultByTipo($group, $currentCheckbox) {
    const tipo = ($group.find('.grupo-tipo').val() ?? '').toString().trim().toUpperCase();
    if (tipo !== 'OPCIONAL_UNICO' || !$currentCheckbox || !$currentCheckbox.length) {
      return;
    }

    if (!$currentCheckbox.prop('checked')) {
      return;
    }

    $group.find('.ingrediente-default').not($currentCheckbox).each(function () {
      $(this).prop('checked', false);
    });
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

      const ingredientes = [];
      let defaultsSeleccionados = 0;
      $g.find('.ingrediente-item').each(function () {
        const $row = $(this);
        const itemId = parseIntOrNull($row.find('.ingrediente-itemSelect').val());
        const cantidad = parseIntOrZero($row.find('.ingrediente-cantidad').val());
        const seleccionadoPorDefecto = !!$row.find('.ingrediente-default').prop('checked');

        if (itemId == null) return;
        if (cantidad < 0) {
          ok = false;
          showIngredientError($row, 'Cantidad inválida (no puede ser negativa).');
          return;
        }

        if (seleccionadoPorDefecto) {
          defaultsSeleccionados += 1;
        }

        ingredientes.push({ itemId, cantidad, seleccionadoPorDefecto });
      });

      if (ingredientes.length === 0) {
        ok = false;
        showGroupError($g, 'El grupo debe tener al menos 1 ingrediente seleccionado.');
        return;
      }

      if (tipo === 'OPCIONAL_UNICO' && defaultsSeleccionados > 1) {
        ok = false;
        showGroupError($g, 'En grupos de selección única solo puede haber un ingrediente por defecto.');
        return;
      }

      grupos.push({ nombre, tipo, ingredientes });
    });

    return { ok, grupos };
  }

  $('#addGrupoBtn').on('click', () => {
    $noGruposMessage.hide();
    const $newGroup = $grupoTemplate.clone();
    $gruposContainer.append($newGroup);
    ensureAtLeastOneIngredient($newGroup);
    syncDefaultVisibilityByTipo($newGroup);
    notifyGroupsChanged();
  });

  $gruposContainer.on('click', '.remove-grupo-btn', function () {
    $(this).closest('.grupo-item').remove();
    if ($gruposContainer.find('.grupo-item').length === 0) {
      $noGruposMessage.show();
    }
    notifyGroupsChanged();
  });

  $gruposContainer.on('click', '.add-ingrediente-btn', function () {
    const $group = $(this).closest('.grupo-item');
    const $container = $group.find('.ingredientes-container').first();
    if (!$container.length) return;
    $container.append($ingredienteTemplate.clone());
    syncDefaultVisibilityByTipo($group);
    notifyGroupsChanged();
  });

  $gruposContainer.on('click', '.remove-ingrediente-btn', function () {
    const $group = $(this).closest('.grupo-item');
    $(this).closest('.ingrediente-item').remove();
    ensureAtLeastOneIngredient($group);
    notifyGroupsChanged();
  });

  $gruposContainer.on('change input', '.grupo-tipo, .ingrediente-itemSelect, .ingrediente-default', function () {
    const $group = $(this).closest('.grupo-item');
    if ($group.length) {
      syncDefaultVisibilityByTipo($group);

      if ($(this).hasClass('ingrediente-default')) {
        enforceSingleDefaultByTipo($group, $(this));
      }
    }

    const $row = $(this).closest('.ingrediente-item');
    if ($row.length) {
      showIngredientError($row, null);
    }
    notifyGroupsChanged();
  });

  $gruposContainer.on('change input', '.ingrediente-cantidad', function () {
    const $row = $(this).closest('.ingrediente-item');
    const value = parseIntOrZero($(this).val());
    if (value < 0) {
      $(this).val(0);
      showIngredientError($row, 'Cantidad inválida (no puede ser negativa).');
    } else {
      showIngredientError($row, null);
    }
    notifyGroupsChanged();
  });

  $gruposContainer.find('.grupo-item').each(function () {
    const $group = $(this);
    ensureAtLeastOneIngredient($group);
    syncDefaultVisibilityByTipo($group);
  });

  notifyGroupsChanged();

  $form.on('submit', (e) => {
    const { ok, grupos } = validateAndBuild();
    if (!ok) {
      e.preventDefault();
      const $firstGroupErr = $gruposContainer.find('.grupo-error').filter(function () { return !this.hidden; }).first();
      const $firstIngErr = $gruposContainer.find('.ingrediente-error').filter(function () { return !this.hidden; }).first();
      const $target = $firstGroupErr.length ? $firstGroupErr : $firstIngErr;
      if ($target.length) {
        $target[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
      return false;
    }
    $gruposJsonHidden.val(JSON.stringify(grupos));
  });
});
