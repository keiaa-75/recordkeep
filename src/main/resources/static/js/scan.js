document.addEventListener('DOMContentLoaded', function() {
    let isProcessing = false;
    const lrnInput = document.getElementById('lrn');
    const studentModal = new bootstrap.Modal(document.getElementById('studentModal'));
    const alertContainer = document.getElementById('alertContainer');

    function onScanSuccess(decodedText, decodedResult) {
        if (isProcessing || !/^\d{12}$/.test(decodedText)) {
            return;
        }
        isProcessing = true;
        lrnInput.value = decodedText;
        
        fetch('/scan', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ lrn: decodedText })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.studentName) {
                document.getElementById('studentName').textContent = data.studentName;
                document.getElementById('scanTime').textContent = new Date().toLocaleTimeString();
                studentModal.show();
                setTimeout(() => {
                    studentModal.hide();
                }, 3000);
            } else {
                alertContainer.innerHTML = `<div class="alert alert-danger">${data.message}</div>`;
            }
            setTimeout(() => {
                isProcessing = false;
                html5QrcodeScanner.clear().then(() => {
                    html5QrcodeScanner.render(onScanSuccess, onScanError);
                });
            }, 2000);
        })
        .catch(err => {
            console.error('Error:', err);
            alertContainer.innerHTML = '<div class="alert alert-danger">Error recording attendance</div>';
            isProcessing = false;
        });
    }
    
    function onScanError(errorMessage) {
        if (!errorMessage.includes("QR code not found")) {
            console.error("QR Scanner Error:", errorMessage);
        }
    }

    const config = {
        fps: 10,
        qrbox: { width: 250, height: 250 },
        rememberLastUsedCamera: true,
        videoConstraints: { facingMode: "environment" }
    };
    const html5QrcodeScanner = new Html5QrcodeScanner("qr-reader", config, false);
    html5QrcodeScanner.render(onScanSuccess, onScanError);
});
