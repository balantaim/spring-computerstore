const textarea = document.getElementById("textarea");
const textCounter = document.getElementById("text-counter");
const maxTextCharacter = 150;

textCounter.textContent = 0 + "/" + maxTextCharacter;

textarea.addEventListener("input", function(){
    let textLength = textarea.value.length;
    textCounter.textContent = textLength + "/" + maxTextCharacter;

    if(textLength > maxTextCharacter){
        textarea.style.borderColor = "#ff2851";
        textCounter.style.color = "#ff2851";
    }else{
        textarea.style.borderColor = "black";
        textCounter.style.color = "black";
    }
});