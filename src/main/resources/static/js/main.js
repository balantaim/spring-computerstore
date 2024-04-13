//Remove loading effect on page load
const skeletonLoadItems = document.querySelectorAll('.is-skeleton');

window.addEventListener('load', function () {
  skeletonLoadItems.forEach(item => {
    item.classList.remove('is-skeleton');
  });
});


//Toggle dropdown content for right and left expand
const dropdownContent = document.getElementById("dropdown-position");
//Set the position of dropdown after the content is load
dropdownPosition();
//Set the position of dropdown after screen resize
window.onresize = function () {
  //console.log("resized: " + window.innerWidth);
  dropdownPosition();
};
function dropdownPosition() {
  //For mobile and tablet
  if(dropdownContent != undefined){
    if (window.innerWidth <= 1023) {
        if(dropdownContent.classList.contains("is-right")){
          dropdownContent.classList.remove("is-right");
        }
        dropdownContent.classList.add("is-left");
      }
      //For laptop and large screen
      else {
        if(dropdownContent.classList.contains("is-left")){
          dropdownContent.classList.remove("is-left");
        }
        dropdownContent.classList.add("is-right");
      }
  }
};


