async function updateContent(cartId, locatorNumber, isIncrement) {
    // Update the Cart then get the new fragments from the back-end
    let data = "";
    const inputId = document.getElementById(`input-number-${locatorNumber}`);
    // Convert input value to integer (max 3 digets)
    const inputValue = parseInt(inputId.value, 3);
    // Check if input is valid
    if (inputValue != null && isValidInput(inputValue)) {
        const action = await isIncrement ? 'increment' : 'decrement';
        const url = window.location.origin + `/Cart/${action}/` + cartId;
        data = await fetchContent(url);
    }
    // Check if the content is valid then swap the old content with the new
    if (data !== null && data !== "") {
        await swapContent(data);
    }
}

function updateTaskCounters() {
    const taskCount = document.getElementById("task-count");
    const cartCount = document.getElementById("cart-count");
    const orderCount = document.getElementById("order-count");

    let taskCountValue = Number(taskCount.textContent);
    let cartCountValue = Number(cartCount.textContent);
    if (cartCount != null && cartCountValue === 1) {
        // Remove cart counts
        cartCount?.remove();
        // Update task counts
        if (taskCount != null) {
            if (orderCount != null) {
                const orderCountValue = Number(orderCount.textContent);
                taskCount.textContent = orderCountValue;
            } else {
                taskCount?.remove();
            }
        }
    } else if (cartCount != null && taskCount != null) {
        cartCountValue--;
        if (cartCountValue !== 0) {
            cartCount.textContent = cartCountValue;
            taskCountValue--;
            taskCount.textContent = taskCountValue;
        }
    }
}

async function deleteContent(cartId) {
    // Update the Cart then get the new fragments from the back-end
    let data = "";
    const url = window.location.origin + '/Cart/delete/' + cartId;
    
    data = await fetchContent(url);

    // Check if the content is valid then swap the old content with the new
    if (data !== null && data !== "") {
        await swapContent(data);
        // Update top bar task counts
        updateTaskCounters();
    }

}

async function fetchContent(url) {
    let newData = "";
    // csrf variable is get by model attribute
    await fetch(url, {
        method: "POST",
        redirect: "follow",
        headers: { [csrfHeader]: csrfToken }
    })
    .then((response) => response.text())
    .then((content) => (newData = content))
    .catch((error) => console.error("Error: " + error));
    // console.log("data: " + data);

    return newData;
}

async function swapContent(data) {
    const parser = new DOMParser();
    try {
        // Parse the string to document then get the fragments' data by ID
        const parsedDoc = parser.parseFromString(data, "text/html");
        const newProducts = parsedDoc.getElementById("cart-items");
        const newSummary = parsedDoc.getElementById("order-summary");

        // console.log(newSummary);
        // console.dir(parsedDoc);

        //Get the old content by ID
        const oldProducts = document.getElementById("cart-items");
        const oldSummary = document.getElementById("order-summary");

        //Check if the locators are valid then swap the content
        if (oldProducts && oldSummary) {
            if (newSummary instanceof HTMLElement && newProducts instanceof HTMLElement) {
                //Replace content with updated card items and order summary
                oldProducts.replaceWith(newProducts);
                oldSummary.replaceWith(newSummary);
                await removePlaceholderAnimation();
            } else {
                //Remove cart items and order summary
                oldProducts.replaceWith("");
                oldSummary.replaceWith("");
                //Show cart empty view
                const oldEmptyCart = document.getElementById("cart-empty");
                const newEmptyCart = parsedDoc.getElementById("cart-empty");
                oldEmptyCart.replaceWith(newEmptyCart);
            }
        }
    } catch (error) {
        console.error('Error: ' + error);
    }
}

async function removePlaceholderAnimation() {
    const skeletonLoadItemsPrimary = document.querySelectorAll('.is-skeleton');
    const skeletonLoadItemsSecondary = document.querySelectorAll('.has-skeleton');

    skeletonLoadItemsPrimary.forEach(item => {
        item.classList.remove('is-skeleton');
    });
    skeletonLoadItemsSecondary.forEach(item => {
        item.classList.remove('has-skeleton');
    });
}

async function isValidInput(inputValue, isIncrement) {
    const MIN_VALUE_INPUT = 1, MAX_VALUE_INPUT = 20;
    if (isIncrement) {
        inputValue++;
    } else {
        inputValue--;
    }
    return await inputValue > MIN_VALUE_INPUT && inputValue <= MAX_VALUE_INPUT;
}