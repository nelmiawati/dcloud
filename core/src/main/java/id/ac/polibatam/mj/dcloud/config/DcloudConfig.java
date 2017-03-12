/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.ac.polibatam.mj.dcloud.config;

/**
 *
 * @author mia
 */
public class DcloudConfig extends APlainConfig {

	public enum Param implements IParam {

		DCLOUD_LOCAL_WORKSPACE("dcloud-local-workspace", "workspace", ".+"),
		DCLOUD_COUNT("dcloud-count", "4", "[0-9]"),
		DCLOUD_THRESHOLD("dcloud-threshold", "2", "[0-9]"),
		CLIENT("client", "id.ac.polibatam.mj.dcloud.io.UtopiaClient", ".+"), 
		CREDENTIAL("credential", "utopia", ".+"),
		;

		private final String name;
		private final String defaultValue;
		private final String pattern;

		private Param(final String name, final String defaultValue, final String pattern) {
			this.name = name;
			this.defaultValue = defaultValue;
			this.pattern = pattern;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getDefaultValue() {
			return this.defaultValue;
		}

		@Override
		public String getPattern() {
			return this.pattern;
		}

	}

	private static final String CONFIG_FILE_NAME = "dcloud-config.properties";
	
	private static final DcloudConfig CONFIG = new DcloudConfig();

	private DcloudConfig() {
		super.loadConfigProperties();
	}

	public static DcloudConfig getInstance() {
		return CONFIG;
	}

	@Override
	protected String getConfigFileName() {
		return CONFIG_FILE_NAME;
	}

}
