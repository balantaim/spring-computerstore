const textarea = document.getElementById("textarea");
const textCounter = document.getElementById("text-counter");
const buttonSave = document.getElementById("save");
const maxTextCharacter = 150;

textCounter.textContent = 0 + "/" + maxTextCharacter;

textarea.addEventListener("input", function(){
    let textLength = textarea.value.length;
    textCounter.textContent = textLength + "/" + maxTextCharacter;

    if(textLength > maxTextCharacter){
        if(!textarea.classList.contains("is-danger"))
        {
            textarea.classList.add("is-danger");
            textCounter.classList.add("has-text-danger");
            buttonSave.disabled = true;
        }
    }else{
        if(textarea.classList.contains("is-danger"))
        {
            textarea.classList.remove("is-danger");
            textCounter.classList.remove("has-text-danger");
            buttonSave.disabled = false;
        }
    }
});