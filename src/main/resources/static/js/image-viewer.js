// View an image.
const mainImage = new Viewer(document.getElementById('prime-image'), {
    //zoomable: false,
    movable: false,
    navbar: false,
    toolbar: {
        zoomIn: true,         // Show or hide the "zoom in" button
        zoomOut: true,        // Show or hide the "zoom out" button
        oneToOne: true,       // Show or hide the "one-to-one" (reset zoom) button
        reset: true,          // Show or hide the "reset" button
        prev: false,           // Show or hide the "previous" button
        play: true,           // Show or hide the "play" button (for slideshow)
        next: false,           // Show or hide the "next" button
        rotateLeft: true,     // Show or hide the "rotate left" button
        rotateRight: true,    // Show or hide the "rotate right" button
        flipHorizontal: true, // Show or hide the "flip horizontal" button
        flipVertical: true,   // Show or hide the "flip vertical" button
    },
    // viewed() {
    //     mainImage.zoomTo(0.8);
    // },
    title: [4, (image, imageData) => `${image.alt}`]
});
// Then, show the image by clicking it, or call `viewer.show()`.

//Check if the gallery container exist and the initiate the gallery
const galleryLocator = document.getElementById('images');
if (galleryLocator !== null) {
    // View a list of images.
    // Note: All images within the container will be found by calling `element.querySelectorAll('img')`.
    const gallery = new Viewer(galleryLocator, {
        title: function (image) {
            return image.alt + ' (' + (this.index + 1) + '/' + this.length + ')';
        },
        //title: [4, (image, imageData) => `${image.alt} (${imageData.naturalWidth} × ${imageData.naturalHeight})`]
        //zoomable: true,
        movable: false,
        toolbar: {
            zoomIn: true,         // Show or hide the "zoom in" button
            zoomOut: true,        // Show or hide the "zoom out" button
            oneToOne: true,       // Show or hide the "one-to-one" (reset zoom) button
            reset: false,          // Show or hide the "reset" button
            prev: true,           // Show or hide the "previous" button
            play: true,           // Show or hide the "play" button (for slideshow)
            next: true,           // Show or hide the "next" button
            rotateLeft: false,     // Show or hide the "rotate left" button
            rotateRight: false,    // Show or hide the "rotate right" button
            flipHorizontal: false, // Show or hide the "flip horizontal" button
            flipVertical: false,   // Show or hide the "flip vertical" button
        },
    });
    // Then, show one image by click it, or call `gallery.show()`.
}
