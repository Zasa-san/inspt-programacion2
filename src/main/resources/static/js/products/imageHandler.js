document.addEventListener('DOMContentLoaded', function () {
  const fileInput = document.getElementById('imageFile');
  const fileNameSpan = document.getElementById('fileName');

  if (fileInput && fileNameSpan) {
    fileInput.addEventListener('change', function (e) {
      const file = e.target.files[0];
      const fileName = file ? file.name : 'Ningún archivo seleccionado';
      fileNameSpan.textContent = fileName;
    });
  }
});