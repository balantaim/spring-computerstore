const firstName = document.getElementById("firstName");
const lastName = document.getElementById("lastName");
const phoneNumber = document.getElementById("phoneNumber");
const textarea = document.getElementById("textarea");
const textCounter = document.getElementById("text-counter");
const buttonSave = document.getElementById("save");

let validateFirstName = true, validateLastName = true, validatePhoneNumber = true, validateAddress = true;

textCounter.textContent = textarea.value.length + "/" + 150;

//Address field
textarea.addEventListener("input", function(){
    const textLength = textarea.value.length;
    textCounter.textContent = textLength + "/" + 150;

    if(textLength > 150){
        if(!textarea.classList.contains("is-danger"))
        {
            textarea.classList.add("is-danger");
            textCounter.classList.add("has-text-danger");
            buttonSave.disabled = true;
            validateAddress = false;
        }
    }else{
        if(textarea.classList.contains("is-danger"))
        {
            textarea.classList.remove("is-danger");
            textCounter.classList.remove("has-text-danger");
            validateAddress = true;
            validateAllFields();
        }
    }
});

//FirstName field
firstName.addEventListener("input", function(){
    const textLength = firstName.value.length;
    const errorIcon = document.getElementById("errorIconFirstName");
    const errorMessage = document.getElementById("errorMessageFirstName");

    if(textLength > 30){
        if(!firstName.classList.contains("is-danger"))
        {
            firstName.classList.add("is-danger");
            buttonSave.disabled = true;
            validateFirstName = false;
            //Add errors
            if(errorIcon.classList.contains("hidden")){
                errorIcon.classList.remove("hidden");
                errorMessage.classList.remove("hidden");
            }
        }
    }else{
        if(firstName.classList.contains("is-danger"))
        {
            firstName.classList.remove("is-danger");
            validateFirstName = true;
            //Remove errors
            errorIcon.classList.add("hidden");
            errorMessage.classList.add("hidden");
            //Try to enable the button 'save'
            validateAllFields();
        }
    }
});

//LastName field
lastName.addEventListener("input", function(){
    const textLength = lastName.value.length;
    const errorIcon = document.getElementById("errorIconLastName");
    const errorMessage = document.getElementById("errorMessageLastName");

    if(textLength > 30){
        if(!lastName.classList.contains("is-danger"))
        {
            lastName.classList.add("is-danger");
            buttonSave.disabled = true;
            validateLastName = false;
            //Add errors
            if(errorIcon.classList.contains("hidden")){
                errorIcon.classList.remove("hidden");
                errorMessage.classList.remove("hidden");
            }
        }
    }else{
        if(lastName.classList.contains("is-danger"))
        {
            lastName.classList.remove("is-danger");
            validateLastName = true;
            //Remove errors
            errorIcon.classList.add("hidden");
            errorMessage.classList.add("hidden");
            //Try to enable the button 'save'
            validateAllFields();
        }
    }
});

//PhoneNumber field
phoneNumber.addEventListener("input", function(){
    const textLength = phoneNumber.value.length;
    const errorIcon = document.getElementById("errorIconPhoneNumber");
    const errorMessage = document.getElementById("errorMessagePhoneNumber");

    if(textLength > 20){
        if(!phoneNumber.classList.contains("is-danger"))
        {
            phoneNumber.classList.add("is-danger");
            buttonSave.disabled = true;
            validatePhoneNumber = false;
            //Add errors
            if(errorIcon.classList.contains("hidden")){
                errorIcon.classList.remove("hidden");
                errorMessage.classList.remove("hidden");
            }
        }
    }else{
        if(phoneNumber.classList.contains("is-danger"))
        {
            phoneNumber.classList.remove("is-danger");
            validatePhoneNumber = true;
            //Remove errors
            errorIcon.classList.add("hidden");
            errorMessage.classList.add("hidden");
            //Try to enable the button 'save'
            validateAllFields();
        }
    }
});

//Enable button 'save' if all fields are validated
function validateAllFields(){
    if(validateFirstName && validateLastName && validatePhoneNumber && validateAddress ){
        buttonSave.disabled = false;
    }
}