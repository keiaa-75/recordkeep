document.addEventListener('DOMContentLoaded', () => {
    // Submit form on date change
    const dateFilter = document.getElementById('dateFilter');
    if (dateFilter) {
        dateFilter.addEventListener('change', function() {
            document.getElementById('filterForm').submit();
        });
    }

    // Real-time search functionality
    const searchInput = document.getElementById('searchInput');
    const sexFilter = document.getElementById('sexFilter');

    if(searchInput) {
        searchInput.addEventListener('input', filterStudents);
    }
    if(sexFilter) {
        sexFilter.addEventListener('change', filterStudents);
    }

    function filterStudents() {
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        const sexFilter = document.getElementById('sexFilter').value;
        const students = document.querySelectorAll('.student-item');

        students.forEach(student => {
            const name = student.dataset.name.toLowerCase();
            const lrn = student.dataset.lrn.toLowerCase();
            const sex = student.dataset.sex;

            const matchesSearch = name.includes(searchTerm) || lrn.includes(searchTerm);
            const matchesSex = !sexFilter || sex === sexFilter;

            student.style.display = matchesSearch && matchesSex ? 'block' : 'none';
        });
    }

    // File upload handling
    const csvFile = document.getElementById('csvFile');
    if(csvFile) {
        csvFile.addEventListener('change', function(e) {
            if (e.target.files.length > 0) {
                e.target.closest('form').submit();
            }
        });
    }

    // QR Management State
    const manageQrBtn = document.getElementById('manage-qr-btn');
    if(manageQrBtn) {
        const qrManagementCard = document.getElementById('qr-management-card');
        const downloadZipBtn = document.getElementById('download-zip-btn-students');
        const fabIcon = manageQrBtn.querySelector('i');

        manageQrBtn.addEventListener('click', () => {
            const isQrStateActive = qrManagementCard.style.display === 'block';

            if (isQrStateActive) {
                qrManagementCard.style.display = 'none';
                fabIcon.classList.remove('bi-x');
                fabIcon.classList.add('bi-plus');
                document.querySelectorAll('.qr-checkbox-container').forEach(c => c.style.display = 'none');
            } else {
                qrManagementCard.style.display = 'block';
                fabIcon.classList.remove('bi-plus');
                fabIcon.classList.add('bi-x');
                document.querySelectorAll('.qr-checkbox-container').forEach(c => c.style.display = 'block');
            }
        });

        downloadZipBtn.addEventListener('click', () => {
            const selectedLrns = Array.from(document.querySelectorAll('.student-qr-checkbox:checked'))
                                      .map(cb => cb.getAttribute('data-lrn'));

            if (selectedLrns.length === 0) {
                alert('Please select at least one student to download QR codes for.');
                return;
            }

            fetch('/download-qrs', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(selectedLrns),
            })
            .then(response => response.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = url;
                a.download = 'qr-codes.zip';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
            })
            .catch(err => console.error('Error downloading zip:', err));
        });
    }

    // Edit Student Modal
    const modalContainer = document.getElementById('editStudentModalContainer');

    document.querySelectorAll('.student-qr-checkbox').forEach(checkbox => {
        checkbox.addEventListener('click', (event) => {
            event.stopPropagation();
        });
    });

    document.querySelectorAll('.student-item').forEach(item => {
        item.addEventListener('click', () => {
            const lrn = item.dataset.lrn;
            
            fetch(`/web/students/edit/${lrn}`)
                .then(response => response.text())
                .then(html => {
                    modalContainer.innerHTML = html;
                    
                    const editModal = new bootstrap.Modal(modalContainer.querySelector('#editStudentModal'));
                    
                    const editForm = modalContainer.querySelector('#editStudentForm');
                    if (editForm) {
                        editForm.addEventListener('submit', (e) => {
                            e.preventDefault();
                            
                            const formData = new FormData(editForm);
                            fetch(editForm.action, {
                                method: 'POST',
                                body: formData
                            }).then(response => {
                                if (response.ok) {
                                    location.reload();
                                } else {
                                    alert('Error updating student.');
                                }
                            });
                        });
                    }

                    const deleteBtn = modalContainer.querySelector('#deleteStudentBtn');
                    const deleteForm = modalContainer.querySelector('#deleteStudentForm');
                    if (deleteBtn && deleteForm) {
                        deleteBtn.addEventListener('click', () => {
                            if (confirm('Are you sure you want to delete this student?')) {
                                fetch(deleteForm.action, {
                                    method: 'POST'
                                }).then(response => {
                                    if (response.ok) {
                                        location.reload();
                                    } else {
                                        alert('Error deleting student.');
                                    }
                                });
                            }
                        });
                    }
                    
                    editModal.show();
                });
        });
    });
});
