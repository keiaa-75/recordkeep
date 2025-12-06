document.addEventListener('DOMContentLoaded', () => {
    initDateFilter();
    initStudentFilters();
    initCsvUpload();
    initQrManagement();
    initEditStudentModal();
});

function initDateFilter() {
    const dateFilter = document.getElementById('dateFilter');
    if (dateFilter) {
        dateFilter.addEventListener('change', () => document.getElementById('filterForm').submit());
    }
}

function initStudentFilters() {
    const searchInput = document.getElementById('searchInput');
    const sexFilter = document.getElementById('sexFilter');

    const filterStudents = () => {
        const searchTerm = searchInput.value.toLowerCase();
        const sex = sexFilter.value;
        document.querySelectorAll('.student-item').forEach(student => {
            const name = student.dataset.name.toLowerCase();
            const lrn = student.dataset.lrn.toLowerCase();
            const studentSex = student.dataset.sex;
            const matchesSearch = name.includes(searchTerm) || lrn.includes(searchTerm);
            const matchesSex = !sex || studentSex === sex;
            student.style.display = matchesSearch && matchesSex ? 'block' : 'none';
        });
    };

    if (searchInput) searchInput.addEventListener('input', filterStudents);
    if (sexFilter) sexFilter.addEventListener('change', filterStudents);
}

function initCsvUpload() {
    const csvFile = document.getElementById('csvFile');
    if (csvFile) {
        csvFile.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                e.target.closest('form').submit();
            }
        });
    }
}

function initQrManagement() {
    const manageQrBtn = document.getElementById('manage-qr-btn');
    if (!manageQrBtn) return;

    const qrManagementCard = document.getElementById('qr-management-card');
    const downloadZipBtn = document.getElementById('download-zip-btn-students');
    const fabIcon = manageQrBtn.querySelector('i');

    manageQrBtn.addEventListener('click', () => {
        const isQrStateActive = qrManagementCard.style.display === 'block';
        qrManagementCard.style.display = isQrStateActive ? 'none' : 'block';
        fabIcon.classList.toggle('bi-x', !isQrStateActive);
        fabIcon.classList.toggle('bi-plus', isQrStateActive);
        document.querySelectorAll('.qr-checkbox-container').forEach(c => c.style.display = isQrStateActive ? 'none' : 'block');
    });

    downloadZipBtn.addEventListener('click', () => {
        const selectedLrns = Array.from(document.querySelectorAll('.student-qr-checkbox:checked')).map(cb => cb.dataset.lrn);
        if (selectedLrns.length === 0) {
            return alert('Please select at least one student.');
        }

        fetch('/download-qrs', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
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
            a.remove();
        })
        .catch(err => console.error('Error downloading zip:', err));
    });
}

function initEditStudentModal() {
    const modalContainer = document.getElementById('editStudentModalContainer');
    const studentList = document.getElementById('studentList');

    if (!studentList || !modalContainer) return;

    studentList.addEventListener('click', (event) => {
        const studentItem = event.target.closest('.student-item');
        if (!studentItem) return;

        // Prevent modal from opening when QR checkbox is clicked
        if (event.target.matches('.student-qr-checkbox')) {
            return;
        }

        const lrn = studentItem.dataset.lrn;
        fetch(`/web/students/edit/${lrn}`)
            .then(response => response.text())
            .then(html => {
                modalContainer.innerHTML = html;
                const editModal = new bootstrap.Modal(modalContainer.querySelector('#editStudentModal'));
                editModal.show();
            });
    });

    modalContainer.addEventListener('submit', (event) => {
        event.preventDefault();
        const form = event.target;
        if (form.id !== 'editStudentForm') return;
        
        fetch(form.action, {
            method: 'POST',
            body: new FormData(form)
        }).then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('Error updating student.');
            }
        });
    });

    modalContainer.addEventListener('click', (event) => {
        if (event.target.id !== 'deleteStudentBtn') return;

        const deleteForm = modalContainer.querySelector('#deleteStudentForm');
        if (deleteForm && confirm('Are you sure you want to delete this student?')) {
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
