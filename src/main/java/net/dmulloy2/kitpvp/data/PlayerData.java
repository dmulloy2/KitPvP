/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;
import net.dmulloy2.util.NumberUtil;

/**
 * @author dmulloy2
 */

@Data
public class PlayerData {
	private int kills;
	private int deaths;
	private int streak;
	private int rank;
	private int points;

	private String lastKnownBy;

	public PlayerData(ResultSet results) throws SQLException {
		this.kills = results.getInt("kills");
		this.deaths = results.getInt("deaths");
		this.streak = results.getInt("streak");
		this.rank = results.getInt("rank");
		this.points = results.getInt("points");
		this.lastKnownBy = results.getString("lastKnownBy");
	}

	protected PlayerData() {
	}

	public double getKDR() {
		if (deaths == 0) {
			return kills;
		}

		double kdr = (double) kills / (double) deaths;
		return NumberUtil.roundNumDecimals(kdr, 2);
	}

	public boolean shouldSave() {
		return kills > 0 || deaths > 0 || points > 0;
	}
}