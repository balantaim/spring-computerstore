function showResult(query) {
    if (query.length == 0) {
        document.getElementById("livesearch").innerHTML = "";
        document.getElementById("livesearch").style.border = "0px";
        return;
    } else if (query.length == 1) {
        return;
    }
    let xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            document.getElementById("livesearch").innerHTML = this.responseText;
            document.getElementById("livesearch").style.border = "1px solid #A5ACB2";
        }
    }
    xmlhttp.open("GET", window.location.origin + "/Live-search?query=" + query, true);
    xmlhttp.send();
}