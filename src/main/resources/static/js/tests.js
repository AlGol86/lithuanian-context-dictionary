let totalWords = 0;
const solved = new Set();

document.addEventListener("DOMContentLoaded", () => {

    fetch(API_ENDPOINT)
        .then(r => r.json())
        .then(words => {
            totalWords = words.length;
            renderWords(words);
        });

    document.getElementById("sendBtn")
        .addEventListener("click", sendProgress);
});

function renderWords(words) {
    const container = document.getElementById("words-container");
    container.innerHTML = "";

    words.forEach(word => {

        const div = document.createElement("div");
        div.className = "word-block";

        const label = document.createElement("p");

        if (word.hint != null) label.textContent = `${word.translation} (${word.hint})`;
        else label.textContent = `${word.translation}`;

        const input = document.createElement("input");
        input.dataset.correct = word.word;
        input.dataset.id = word.id;

        input.addEventListener("input", checkWord);

        div.appendChild(label);
        div.appendChild(input);
        container.appendChild(div);
    });
}

function checkWord(e) {
    const input = e.target;
    const correct = input.dataset.correct.toLowerCase(); ;
    const value = input.value.toLowerCase(); ;
    const id = Number(input.dataset.id);

    if (correct.startsWith(value)) {

        input.classList.remove("wrong");

        if (value === correct) {
            input.classList.add("correct");
            solved.add(id);
            updateScore();
        }

    } else {
        input.classList.remove("correct");
        input.classList.add("wrong");
    }
}

function updateScore() {
    const percent = Math.round((solved.size / totalWords) * 100);
    document.getElementById("score").textContent = percent;
}

function sendProgress() {
    const name = document.getElementById("playerName").value.trim();
    if (!name) {
        alert("Enter your name");
        return;
    }

    fetch("/api/grammar-tests/results", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            name: name,
            page: PAGE_NAME,
            solvedIds: Array.from(solved)
        })
    })
        .then(() => {
            const sendStatus = document.getElementById("sendStatus");

            // Clear previous content
            sendStatus.innerHTML = "";

            // Add text
            const textNode = document.createTextNode("Saved! ");
            sendStatus.appendChild(textNode);

            // Add link to results page
            const link = document.createElement("a");
            link.href = "/grammar-tests/results.html";        // change to your actual results URL
            link.innerText = "Open results page";
            link.target = "_blank";        // optional: open in new tab
            sendStatus.appendChild(link);
        })
        .catch(err => {
            console.error("Error saving progress:", err);
            document.getElementById("sendStatus").innerText = "Failed to save progress.";
        });
}