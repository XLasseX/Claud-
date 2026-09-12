# DonutSMPCore 1.1.0

Paper 1.21.11 / Java 21.

### Features
- Shards system with persistent storage
- Team system with persistent storage
- Persistent total playtime
- PlaceholderAPI expansion
- Name/ping/kills/deaths can use PlaceholderAPI's built-in/stat placeholders

### DonutSMPCore placeholders
- `%donutsmp_shards%`
- `%donutsmp_team%`
- `%donutsmp_team_size%`
- `%donutsmp_playtime%`
- `%donutsmp_playtime_hours%`
- `%donutsmp_playtime_minutes%`
- `%donutsmp_playtime_seconds%`

### Scoreboard
Use these in DonutScoreboard:
- Money: `%vault_eco_balance_formatted%` (requires Vault + an economy provider)
- Shards: `%donutsmp_shards%`
- Kills: `%statistic_player_kills%`
- Deaths: `%statistic_deaths%`
- Keyall: `%donutscoreboard_keyall%`
- Playtime: `%donutsmp_playtime%`
- Team: `%donutsmp_team%`
- Name: `%player_name%`
- Ping: `%player_ping%`

If a placeholder is not available, install/update the corresponding PlaceholderAPI expansion or check `/papi parse me <placeholder>`.
