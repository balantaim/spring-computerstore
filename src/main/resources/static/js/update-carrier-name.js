
function updateCarrierText() {
    //Get checked radio button
    const carrierRadioButton = document.querySelector('input[name="carrier"]:checked');
    //Get name container for the carrier
    const carrierContainer = document.getElementById('carrier-name');
    if (carrierRadioButton !== null) {
        carrierRadioButton.id === "ECONT" ? carrierContainer.textContent = "Econt" : carrierContainer.textContent = "Speedy";
        //Update input value for the POST method
        document.getElementById('carrier-input').value = carrierRadioButton.id;
    }
}

// Add an event listener to all radio buttons
document.getElementById('ECONT').addEventListener('change', updateCarrierText);
document.getElementById('SPEEDY').addEventListener('change', updateCarrierText);
