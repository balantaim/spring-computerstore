//Remove loading effect on page load
window.addEventListener("load", function () {
    removePlaceholderAnimation();
});

function removePlaceholderAnimation() {
    const skeletonLoadItemsPrimary = document.querySelectorAll(".is-skeleton");
    const skeletonLoadItemsSecondary = document.querySelectorAll(".has-skeleton");
    //  const skeletonLoadItemsTertiary = document.querySelectorAll('.skeleton-block');
    //  const skeletonLoadItemsTertiary = document.querySelectorAll('.skeleton-lines');

    skeletonLoadItemsPrimary.forEach((item) => {
        item.classList.remove("is-skeleton");
    });
    skeletonLoadItemsSecondary.forEach((item) => {
        item.classList.remove("has-skeleton");
    });
    //  skeletonLoadItemsTertiary.forEach(item => {
    //    item.classList.remove('skeleton-block');
    //  });
    //  skeletonLoadItemsTertiary.forEach(item => {
    //    item.classList.remove('skeleton-lines');
    //  });
}

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
    if (dropdownContent !== null && dropdownContent.classList.length > 0) {
        if (window.innerWidth <= 1023) {
            if (dropdownContent.classList.contains("is-right")) {
                dropdownContent.classList.remove("is-right");
            }
            dropdownContent.classList.add("is-left");
        }
        //For laptop and large screen
        else {
            if (dropdownContent.classList.contains("is-left")) {
                dropdownContent.classList.remove("is-left");
            }
            dropdownContent.classList.add("is-right");
        }
    }
}
