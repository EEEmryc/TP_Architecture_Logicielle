document.addEventListener("DOMContentLoaded", () => {
    const tableTotal = document.getElementById("table-total");
    const tableIt = document.getElementById("table-it");
    const tablePue = document.getElementById("table-pue");

    if (!tableTotal || !tableIt || !tablePue) return;

    const rowsTotal = tableTotal.querySelectorAll("tbody tr");
    const rowsIt = tableIt.querySelectorAll("tbody tr");
    const rowsPue = tablePue.querySelectorAll("tbody tr");

    rowsPue.forEach((rowPue, index) => {
        const regionName = rowPue.cells[0].innerText.trim().toLowerCase();
        
        if (regionName === "world") {
            for (let i = 1; i < rowPue.cells.length; i++) {
                rowPue.cells[i].innerText = "—";
            }
            return;
        }

        const rowTotal = rowsTotal[index];
        const rowIt = rowsIt[index];

        for (let i = 1; i < rowPue.cells.length; i++) {
            const valTotal = parseFloat(rowTotal.cells[i].innerText.trim().replace(",", "."));
            const valIt = parseFloat(rowIt.cells[i].innerText.trim().replace(",", "."));

            if (!isNaN(valTotal) && !isNaN(valIt) && valIt > 0) {
                const pue = valTotal / valIt;
                rowPue.cells[i].innerText = pue.toFixed(2);
            } else {
                rowPue.cells[i].innerText = "N/A";
            }
        }
    });
});