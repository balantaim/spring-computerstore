//Remove loading effect on page load
const skeletonLoadItems = document.querySelectorAll('.is-skeleton');

window.addEventListener('load', function(){
    skeletonLoadItems.forEach(item => {
        item.classList.remove('is-skeleton');
    });
});

