/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.polibatam.mj.dcloud.util;

import id.ac.polibatam.mj.dcloud.exception.runtime.DcloudInvalidDataRuntimeException;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mia
 */
public class CommandLineParser {

    private Map<String, String> options = new HashMap<String, String>();
    private List<String> params = new ArrayList<String>();

    public CommandLineParser(final String[] args) {

        for (String arg : args) {

            if (arg.startsWith("--")) {
                if (arg.length() == 2) {
                    throw new DcloudInvalidDataRuntimeException("INVALID option [" + arg + "]");
                }
                final String[] optString = arg.substring(2).split("=");
                final String key = optString[0];
                String value = null;
                if (optString.length > 1) {
                    for (int i = 1; i < optString.length; i++) {
                        if (null == value) {
                            value = optString[i];
                        } else {
                            value += "=";
                            value += optString[i];
                        }
                    }
                }
                options.put(key, value);
            } else if (arg.startsWith("-")) {
                if (arg.length() == 1) {
                    throw new DcloudInvalidDataRuntimeException("INVALID option [" + arg + "]");
                }
                final String optString = arg.substring(1);
                if (!StringUtils.isAlpha(optString)) {
                    throw new DcloudInvalidDataRuntimeException("INVALID option [" + arg + "]");
                }
                final char[] optsChar = optString.toCharArray();
                for (char optChar : optsChar) {
                    options.put(Character.toString(optChar), null);
                }
            } else {
                params.add(arg);
            }

        }

    }

    public Map<String, String> getOptions() {
        return this.options;
    }

    public List<String> getParameters() {
        return this.params;
    }

}
