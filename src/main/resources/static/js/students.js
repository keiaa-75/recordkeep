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
    const editStudentModalElement = document.getElementById('editStudentModal');
    if (editStudentModalElement) {
        const editStudentModal = new bootstrap.Modal(editStudentModalElement);
        const editStudentForm = document.getElementById('editStudentForm');
        const deleteStudentForm = document.getElementById('deleteStudentForm');

        document.querySelectorAll('.student-qr-checkbox').forEach(checkbox => {
            checkbox.addEventListener('click', (event) => {
                event.stopPropagation();
            });
        });

        document.querySelectorAll('.student-item').forEach(item => {
            item.addEventListener('click', () => {
                const lrn = item.dataset.lrn;
                const firstName = item.dataset.firstname;
                const surname = item.dataset.surname;
                const sex = item.dataset.sex;
                const sectionId = item.dataset.sectionid;

                editStudentForm.action = `/sections/${sectionId}/students/${lrn}/update`;
                editStudentForm.querySelector('[name="lrn"]').value = lrn;
                editStudentForm.querySelector('[name="firstName"]').value = firstName;
                editStudentForm.querySelector('[name="surname"]').value = surname;
                editStudentForm.querySelector('[name="sex"]').value = sex;
                
                deleteStudentForm.action = `/sections/${sectionId}/students/${lrn}/delete`;

                editStudentModal.show();
            });
        });
        
        const deleteStudentBtn = document.getElementById('deleteStudentBtn');
        deleteStudentBtn.addEventListener('click', () => {
            if (confirm('Are you sure you want to delete this student?')) {
                deleteStudentForm.submit();
            }
        });
    }
});
