$(() => {
  $('.product-delete-form').on('submit', function (event) {
    const confirmed = window.confirm('¿Está seguro que desea eliminar este producto?');
    if (!confirmed) {
      event.preventDefault();
      return false;
    }
  });
});
