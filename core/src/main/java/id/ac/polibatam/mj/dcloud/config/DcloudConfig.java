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

		PARAM_STRING("param-string", "default-param-string@123", ".+"), 
		PARAM_INTEGER("param-integer", "0123456789", "[0-9]"),;

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
