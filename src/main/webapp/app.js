let allMatches = [];
let currentMatchday = 1;

/**
 * Fetches match data from the backend API and initializes the view.
 */
function fetchMatches() {
    fetch('/api/standings')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Errore Server: ${response.status} ${response.statusText}`);
            }
            return response.json();
        })
        .then(matches => {
            // Se l'array è vuoto (es. API limitata o token errato)
            if (!matches || matches.length === 0) {
                throw new Error("Nessuna partita trovata. Controlla il token API!");
            }

            allMatches = matches.map((m, index) => ({
                ...m, globalIndex: index, isPredicted: false
            }));

            const firstUnplayed = allMatches.find(m => !m.isPlayed);
            currentMatchday = firstUnplayed ? firstUnplayed.matchday : 1;

            initMatchdayControls();
            updateView();
        })
        .catch(error => {
            console.error("Error fetching matches:", error);
            // Mostriamo l'errore sullo schermo invece di lasciare "Caricamento..."
            document.getElementById('table-container').innerHTML =
                `<div style="color: red; padding: 20px; text-align: center;">
                    <h3>Si è verificato un errore:</h3>
                    <p>${error.message}</p>
                 </div>`;
            document.getElementById('matches-container').innerHTML = '';
        });
}

/**
 * Initializes the select dropdown and navigation buttons for matchdays.
 */
function initMatchdayControls() {
    const select = document.getElementById('matchday-select');
    const btnPrev = document.getElementById('prev-matchday');
    const btnNext = document.getElementById('next-matchday');

    select.innerHTML = '';
    for (let i = 1; i <= 38; i++) {
        const option = document.createElement('option');
        option.value = i;
        option.textContent = `Giornata ${i}`;
        select.appendChild(option);
    }

    select.value = currentMatchday;

    select.addEventListener('change', (e) => {
        currentMatchday = parseInt(e.target.value);
        updateView();
    });

    btnPrev.addEventListener('click', () => {
        if (currentMatchday > 1) {
            currentMatchday--;
            select.value = currentMatchday;
            updateView();
        }
    });

    btnNext.addEventListener('click', () => {
        if (currentMatchday < 38) {
            currentMatchday++;
            select.value = currentMatchday;
            updateView();
        }
    });
}

/**
 * Updates the UI state, rendering both the table and the match list.
 */
function updateView() {
    document.getElementById('prev-matchday').disabled = (currentMatchday === 1);
    document.getElementById('next-matchday').disabled = (currentMatchday === 38);

    const tableData = calculateTable(allMatches);
    renderTable(tableData);
    renderMatches();
}

/**
 * Renders the list of matches for the currently selected matchday, with clickable links.
 */
function renderMatches() {
    const container = document.getElementById('matches-container');
    container.innerHTML = '';

    const matchesToShow = allMatches.filter(m => m.matchday === currentMatchday);

    matchesToShow.forEach(match => {
        const row = document.createElement('div');
        row.className = 'match-row';

        let scoreAreaHtml = '';
        if (match.isPlayed) {
            scoreAreaHtml = `<div class="score-fixed">${match.homeScore}</div><span>-</span><div class="score-fixed">${match.awayScore}</div>`;
        } else {
            const hScore = match.isPredicted ? match.homeScore : '';
            const aScore = match.isPredicted ? match.awayScore : '';
            scoreAreaHtml = `<input type="number" min="0" class="score-input home-input" data-index="${match.globalIndex}" value="${hScore}">
                             <span>-</span>
                             <input type="number" min="0" class="score-input away-input" data-index="${match.globalIndex}" value="${aScore}">`;
        }

        // Encode team names for the URL
        const encodedHome = encodeURIComponent(match.homeTeam);
        const encodedAway = encodeURIComponent(match.awayTeam);

        row.innerHTML = `
            <div class="team home">
                <a href="team.html?name=${encodedHome}" class="team-link">${match.homeTeam}</a>
                <img src="${match.homeTeamCrest}" class="team-logo" alt="logo">
            </div>
            <div class="score-area">${scoreAreaHtml}</div>
            <div class="team away">
                <img src="${match.awayTeamCrest}" class="team-logo" alt="logo">
                <a href="team.html?name=${encodedAway}" class="team-link">${match.awayTeam}</a>
            </div>
        `;
        container.appendChild(row);
    });

    document.querySelectorAll('.score-input').forEach(input => {
        input.addEventListener('input', handlePrediction);
    });
}
/**
 * Handles user input for match predictions and triggers table recalculation.
 * * @param {Event} event The input event.
 */
function handlePrediction(event) {
    const input = event.target;
    const match = allMatches[input.getAttribute('data-index')];
    const parent = input.parentElement;
    const homeVal = parent.querySelector('.home-input').value;
    const awayVal = parent.querySelector('.away-input').value;

    if (homeVal !== '' && awayVal !== '') {
        match.isPredicted = true;
        match.homeScore = parseInt(homeVal);
        match.awayScore = parseInt(awayVal);
    } else {
        match.isPredicted = false;
        match.homeScore = 0;
        match.awayScore = 0;
    }
    renderTable(calculateTable(allMatches));
}

/**
 * Calculates the league table based on played and predicted matches,
 * strictly applying Serie A tie-breaker rules (Head-to-Head).
 * * @param {Array} matches The list of all matches.
 * @returns {Array} The sorted list of team objects.
 */
function calculateTable(matches) {
    const teams = {};
    matches.forEach(match => {
        if (!teams[match.homeTeam]) {
            teams[match.homeTeam] = { name: match.homeTeam, crest: match.homeTeamCrest, points: 0, played: 0, goalsFor: 0, goalsAgainst: 0 };
        }
        if (!teams[match.awayTeam]) {
            teams[match.awayTeam] = { name: match.awayTeam, crest: match.awayTeamCrest, points: 0, played: 0, goalsFor: 0, goalsAgainst: 0 };
        }
    });

    matches.forEach(match => {
        if (!match.isPlayed && !match.isPredicted) return;

        const home = teams[match.homeTeam];
        const away = teams[match.awayTeam];

        home.played++;
        away.played++;
        home.goalsFor += match.homeScore;
        home.goalsAgainst += match.awayScore;
        away.goalsFor += match.awayScore;
        away.goalsAgainst += match.homeScore;

        if (match.homeScore > match.awayScore) home.points += 3;
        else if (match.homeScore < match.awayScore) away.points += 3;
        else { home.points += 1; away.points += 1; }
    });

    const tableArray = Object.values(teams);
    tableArray.forEach(t => t.goalDifference = t.goalsFor - t.goalsAgainst);

    // Applying Serie A sorting rules
    tableArray.sort((a, b) => {
        // 1. Total Points
        if (b.points !== a.points) return b.points - a.points;

        // Find matches played between team A and team B
        let h2hPtsA = 0, h2hPtsB = 0;
        let h2hGdA = 0, h2hGdB = 0;

        const h2hMatches = matches.filter(m =>
            (m.isPlayed || m.isPredicted) &&
            ((m.homeTeam === a.name && m.awayTeam === b.name) ||
             (m.homeTeam === b.name && m.awayTeam === a.name))
        );

        // Calculate Head-to-Head stats
        h2hMatches.forEach(m => {
            const aIsHome = m.homeTeam === a.name;
            const scoreA = aIsHome ? m.homeScore : m.awayScore;
            const scoreB = aIsHome ? m.awayScore : m.homeScore;

            h2hGdA += (scoreA - scoreB);
            h2hGdB += (scoreB - scoreA);

            if (scoreA > scoreB) h2hPtsA += 3;
            else if (scoreB > scoreA) h2hPtsB += 3;
            else { h2hPtsA += 1; h2hPtsB += 1; }
        });

        // 2. Head-to-Head Points
        if (h2hPtsB !== h2hPtsA) return h2hPtsB - h2hPtsA;

        // 3. Head-to-Head Goal Difference
        if (h2hGdB !== h2hGdA) return h2hGdB - h2hGdA;

        // 4. Overall Goal Difference
        if (b.goalDifference !== a.goalDifference) return b.goalDifference - a.goalDifference;

        // 5. Overall Goals Scored
        if (b.goalsFor !== a.goalsFor) return b.goalsFor - a.goalsFor;

        // 6. Alphabetical fallback
        return a.name.localeCompare(b.name);
    });

    return tableArray;
}

/**
 * Renders the league table with color-coded UEFA and Relegation zones.
 * @param {Array} tableData The sorted array of teams.
 */
function renderTable(tableData) {
    let html = `<table class="standings-table">
        <thead><tr><th>Pos</th><th>Squadra</th><th>Pts</th><th>G</th><th>GF</th><th>GS</th><th>DR</th></tr></thead><tbody>`;

    tableData.forEach((team, index) => {
        const position = index + 1;
        let zoneClass = "";

        // Assigning zone classes based on position
        if (position === 1) zoneClass = "zone-champion";
        else if (position >= 2 && position <= 4) zoneClass = "zone-champions-league";
        else if (position >= 5 && position <= 6) zoneClass = "zone-europa-league";
        else if (position === 7) zoneClass = "zone-conference-league";
        else if (position >= 18) zoneClass = "zone-relegation";

        const encodedName = encodeURIComponent(team.name);

        html += `
            <tr class="${zoneClass}">
                <td>${position}</td>
                <td class="team-name-cell">
                    <img src="${team.crest}" class="team-logo" alt="logo">
                    <a href="team.html?name=${encodedName}" class="team-link">${team.name}</a>
                </td>
                <td><strong>${team.points}</strong></td><td>${team.played}</td>
                <td>${team.goalsFor}</td><td>${team.goalsAgainst}</td><td>${team.goalDifference}</td>
            </tr>
        `;
    });
    html += `</tbody></table>`;
    document.getElementById('table-container').innerHTML = html;
}
/**
 * Resets all user-made predictions while keeping the official results.
 */
function resetPredictions() {
    if (confirm("Sei sicuro di voler cancellare tutti i tuoi pronostici?")) {
        allMatches.forEach(match => {
            if (match.isPredicted) {
                match.isPredicted = false;
                match.homeScore = 0;
                match.awayScore = 0;
            }
        });


        const currentTable = calculateTable(allMatches);
        renderTable(currentTable);
        renderMatches();

        console.log("Predictions reset successfully.");
    }
}

document.getElementById('reset-btn').addEventListener('click', resetPredictions);
document.addEventListener("DOMContentLoaded", fetchMatches);