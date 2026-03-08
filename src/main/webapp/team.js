/**
 * Initializes the team dashboard by reading the URL parameter and fetching data.
 */
document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const teamName = params.get('name');

    if (!teamName) {
        document.getElementById('team-content').innerHTML = '<h2 style="color:red; text-align:center;">Error: No team selected.</h2>';
        return;
    }

    fetch('/api/standings')
        .then(response => response.json())
        .then(matches => {
            renderTeamDashboard(teamName, matches);
        })
        .catch(error => console.error("Error fetching match data:", error));
});

/**
 * Calculates statistics and renders the team dashboard HTML, including clickable match history.
 * * @param {string} teamName The name of the team to display.
 * @param {Array} matches The array of all matches.
 */
function renderTeamDashboard(teamName, matches) {
    const teamMatches = matches.filter(m => m.homeTeam === teamName || m.awayTeam === teamName);

    if (teamMatches.length === 0) {
        document.getElementById('team-content').innerHTML = '<h2 style="text-align:center;">Team not found in the database.</h2>';
        return;
    }

    const firstMatch = teamMatches[0];
    const crest = firstMatch.homeTeam === teamName ? firstMatch.homeTeamCrest : firstMatch.awayTeamCrest;

    let played = 0, wins = 0, draws = 0, losses = 0, gf = 0, ga = 0, points = 0;

    teamMatches.forEach(match => {
        if (!match.isPlayed) return;

        played++;
        const isHome = match.homeTeam === teamName;
        const teamScore = isHome ? match.homeScore : match.awayScore;
        const opponentScore = isHome ? match.awayScore : match.homeScore;

        gf += teamScore;
        ga += opponentScore;

        if (teamScore > opponentScore) {
            wins++;
            points += 3;
        } else if (teamScore === opponentScore) {
            draws++;
            points += 1;
        } else {
            losses++;
        }
    });

    const gd = gf - ga;

    let html = `
        <div class="team-header-large" style="text-align: center; border-bottom: 2px solid #eaeaea; padding-bottom: 20px; margin-bottom: 20px;">
            <img src="${crest}" alt="${teamName} logo" style="width: 100px; height: 100px; object-fit: contain;">
            <h2 style="font-size: 2.5em; margin: 10px 0 0 0; color: #1a1a1a;">${teamName}</h2>
        </div>

        <div class="stats-grid">
            <div class="stat-card"><h3>${points}</h3><p>Points</p></div>
            <div class="stat-card"><h3>${played}</h3><p>Played</p></div>
            <div class="stat-card" style="color: green;"><h3>${wins}</h3><p>Wins</p></div>
            <div class="stat-card" style="color: #d4af37;"><h3>${draws}</h3><p>Draws</p></div>
            <div class="stat-card" style="color: red;"><h3>${losses}</h3><p>Losses</p></div>
            <div class="stat-card"><h3>${gf}</h3><p>Goals For</p></div>
            <div class="stat-card"><h3>${ga}</h3><p>Goals Against</p></div>
            <div class="stat-card"><h3>${gd > 0 ? '+'+gd : gd}</h3><p>Goal Diff.</p></div>
        </div>

        <h3 style="margin-top: 30px; text-align: center; color: #1a1a1a;">All Matches</h3>
        <div style="max-height: 400px; overflow-y: auto; padding-right: 10px;">
    `;

    teamMatches.forEach(match => {
        const isPlayed = match.isPlayed;
        const scoreText = isPlayed ? `${match.homeScore} - ${match.awayScore}` : `vs`;
        const color = isPlayed ? '#1a1a1a' : '#888';

        const encodedHome = encodeURIComponent(match.homeTeam);
        const encodedAway = encodeURIComponent(match.awayTeam);

        html += `
            <div class="match-row" style="color: ${color};">
                <span style="width: 50px; font-weight: bold; color: #d4af37;">MD ${match.matchday}</span>

                <span class="team home">
                    <a href="team.html?name=${encodedHome}" class="team-link" style="color: inherit;">${match.homeTeam}</a>
                    <img src="${match.homeTeamCrest}" class="team-logo" alt="logo" style="margin-left: 12px; margin-right: 0;">
                </span>

                <span class="score-area" style="font-weight: bold; width: 60px; justify-content: center;">${scoreText}</span>

                <span class="team away">
                    <img src="${match.awayTeamCrest}" class="team-logo" alt="logo" style="margin-right: 12px; margin-left: 0;">
                    <a href="team.html?name=${encodedAway}" class="team-link" style="color: inherit;">${match.awayTeam}</a>
                </span>
            </div>
        `;
    });

    html += `</div>`;
    document.getElementById('team-content').innerHTML = html;
}