        async function updateContent() {
            // Update the Cart then get the new fragments from the back-end
            let data = "";
            await fetch(`http://localhost:8080/cart/update/1`, {
                method: "POST",
                redirect: "follow",
            })
                .then((response) => response.text())
                .then((content) => (data = content))
                .catch((error) => console.error("Error: " + error));
            // console.log("data: " + data);

            // Check if the content is valid then swap the old content with the new
            if (data !== null && data !== "") {
                await swapContent(data);
            }
        }

        async function swapContent(data) {
            const parser = new DOMParser();
            try {
                // Parse the string to document then get the fragments' data by ID
                const parsedDoc = await parser.parseFromString(data, "text/html");
                const newSummary = await parsedDoc.getElementById("order-summary");
                const newProducts = await parsedDoc.getElementById("products-list");

                // console.log(newSummary);
                // console.dir(parsedDoc);

                //Get the old content by ID
                const oldProducts = await document.getElementById("products-list");
                const oldSummary = await document.getElementById("order-summary");

                //Check if the locators are valid then swap the content
                if (oldProducts && oldSummary && newSummary && newProducts) {
                    await oldProducts.replaceWith(newProducts);
                    await oldSummary.replaceWith(newSummary);
                }
            } catch (error) {
                console.error('Error: ' + error);
            }
        }