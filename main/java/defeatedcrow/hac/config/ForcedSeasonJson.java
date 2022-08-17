package defeatedcrow.hac.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import defeatedcrow.hac.api.climate.EnumSeason;

public class ForcedSeasonJson {

	public static ForcedSeasonJson INSTANCE = new ForcedSeasonJson();

	public static SeasonData DATA = INSTANCE.new SeasonData(EnumSeason.SPRING, false);

	public static void lead() {
		File file = new File(ClimateConfig.configDir + "/save/forced_season.json");

		if (file != null) {
			try {
				if (!file.exists()) {
					return;
				}

				if (file.canRead()) {
					FileInputStream fis = new FileInputStream(file.getPath());
					InputStreamReader isr = new InputStreamReader(fis);
					JsonReader jsr = new JsonReader(isr);

					Gson gson = new Gson();
					SeasonData get = gson.fromJson(jsr, SeasonData.class);

					isr.close();
					fis.close();
					jsr.close();

					if (get != null) {
						INSTANCE.DATA = get;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 呼ばれる度に書き込みをする
	public static void write() {

		if (INSTANCE.DATA != null) {

			File file = new File(ClimateConfig.configDir + "/save/forced_season.json");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			try {
				if (!file.exists() && !file.createNewFile()) {
					return;
				}

				if (file.canWrite()) {
					FileOutputStream fos = new FileOutputStream(file.getPath());
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					JsonWriter jsw = new JsonWriter(osw);
					jsw.setIndent("    ");
					Gson gson = new Gson();
					gson.toJson(INSTANCE.DATA, SeasonData.class, jsw);

					osw.close();
					fos.close();
					jsw.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public class SeasonData {
		private EnumSeason season;
		private boolean forced;

		SeasonData(EnumSeason seasonIn, boolean forcedIn) {
			season = seasonIn;
			forced = forcedIn;
		}

		public EnumSeason getSeason() {
			return season;
		}

		public void setSeason(EnumSeason s) {
			season = s;
		}

		public boolean getForced() {
			return forced;
		}

		public void setForced(boolean f) {
			forced = f;
		}
	}

}
