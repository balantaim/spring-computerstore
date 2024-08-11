async function showResult(query) {
    if (query.length == 0) {
        document.getElementById("livesearch").innerHTML = "";
        document.getElementById("livesearch").style.border = "0px";
        return;
    } else if (query.length == 1) {
        return;
    }
    try {
        const response = await fetch(window.location.origin + "/Live-search?query=" + query,
        {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(data => document.getElementById("livesearch").innerHTML += data.text());
        //.then(data => console.log(data.text()));


//        if (!response.ok){
//            throw new Error("Could not fetch resource!");
//        }

        //const data = await response;
        //document.getElementById("livesearch").innerHTML = data;
        //document.getElementById("livesearch").style.border = "1px solid #A5ACB2";
        //console.log(response.json());
    } catch (error) {
        console.error(error);
    }


//    let xmlhttp = new XMLHttpRequest();
//    xmlhttp.onreadystatechange = function() {
//        if (this.readyState == 4 && this.status == 200) {
//            document.getElementById("livesearch").innerHTML = this.responseText;
//            document.getElementById("livesearch").style.border = "1px solid #A5ACB2";
//        }
//    }
//    xmlhttp.open("GET", window.location.origin + "/Live-search?query=" + query, true);
//    xmlhttp.send();
}