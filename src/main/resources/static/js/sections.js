document.addEventListener('DOMContentLoaded', () => {
    initEditSectionModal();
});

function initEditSectionModal() {
    const modalContainer = document.getElementById('editSectionModalContainer');
    const sectionList = document.getElementById('sectionList');

    if (!sectionList || !modalContainer) return;

    sectionList.addEventListener('click', (event) => {
        const editButton = event.target.closest('.edit-section-btn');
        if (!editButton) return;

        event.preventDefault();

        const sectionId = editButton.dataset.sectionId;
        fetch(`/sections/edit/${sectionId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(html => {
                modalContainer.innerHTML = html;
                const editModal = new bootstrap.Modal(modalContainer.querySelector('#editSectionModal'));
                editModal.show();
            })
            .catch(error => {
                console.error('Error fetching edit section modal:', error);
                alert('Could not load section details. Please try again.');
            });
    });

    modalContainer.addEventListener('submit', (event) => {
        const form = event.target;
        if (form.id !== 'editSectionForm') return;
        
        event.preventDefault();
        
        fetch(form.action, {
            method: 'POST',
            body: new FormData(form)
        }).then(response => {
            if (response.ok) {
                // Check for a redirect response
                if (response.redirected) {
                    window.location.href = response.url;
                } else {
                    location.reload();
                }
            } else {
                alert('Error updating section.');
            }
        }).catch(error => {
            console.error('Error submitting form:', error);
            alert('Error updating section.');
        });
    });

    modalContainer.addEventListener('click', (event) => {
        if (event.target.id !== 'deleteSectionBtn') return;

        const deleteForm = modalContainer.querySelector('#deleteSectionForm');
        if (deleteForm && confirm('Are you sure you want to delete this section? This will also delete all students in it.')) {
            fetch(deleteForm.action, {
                method: 'POST'
            }).then(response => {
                if (response.ok) {
                    if (response.redirected) {
                        window.location.href = response.url;
                    } else {
                        location.reload();
                    }
                } else {
                    alert('Error deleting section.');
                }
            }).catch(error => {
                console.error('Error deleting section:', error);
                alert('Error deleting section.');
            });
        }
    });
}
