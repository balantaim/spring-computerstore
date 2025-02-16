
function updateCarrierText() {
  //Get checked radio button
  const carrierRadioButton = document.querySelector('input[name="carrier"]:checked');
  //Get name container for the carrier
  const carrierContainer = document.getElementById('carrier-name');
  if (carrierRadioButton !== null) {
    carrierRadioButton.id === "econt" ? carrierContainer.textContent = "Econt":carrierContainer.textContent = "Speedy"
  }
}
// Add an event listener to all radio buttons
document.getElementById('econt').addEventListener('change', updateCarrierText);
document.getElementById('speedy').addEventListener('change', updateCarrierText);
//Update the text after first load
updateCarrierText();