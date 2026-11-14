/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugin.doap;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.doap.options.ASFExtOptions;

/**
 * Encapsulates all user messages.
 *
 * @since 1.1
 */
class UserMessages {

    private final ASFExtOptions asfExtOptions;

    UserMessages(ASFExtOptions asfExtOptions) {
        this.asfExtOptions = asfExtOptions;
    }

    public static final int REQUIRED = 10;

    public static final int REQUIRED_BY_ASF_OR_RECOMMENDED = 11;

    public static final int REQUIRED_BY_ASF = 12;

    public static final int RECOMMENDED = 20;

    public static final int INVALID_URL = 30;

    public static final int INVALID_DATE = 31;

    public static final int INVALID_ISO_DATE = 32;

    public static final int INVALID_EMAIL = 33;

    public static final int SHORT_DESC_TOO_LONG = 34;

    private List<String> errorMessages = new ArrayList<>();

    private List<String> warnMessages = new ArrayList<>();

    /**
     * @return the error messages
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * @return the warn messages
     */
    public List<String> getWarnMessages() {
        return warnMessages;
    }

    /**
     * @param tags    not null
     * @param value   could be null
     * @param errorId positive id
     */
    protected void addMessage(String[] tags, String value, int errorId) {
        if (tags == null) {
            throw new IllegalArgumentException("tags is required");
        }

        boolean isPom = false;
        if (tags[0].equalsIgnoreCase("project")) {
            isPom = true;
        }
        switch (errorId) {
            case REQUIRED:
                errorMessages.add("A " + toConfiguration(tags, null) + "  parameter is required.");
                break;
            case REQUIRED_BY_ASF_OR_RECOMMENDED:
                if (isPom) {
                    if (asfExtOptions.isIncluded()) {
                        errorMessages.add("A POM " + toConfiguration(tags, null) + " value is required by ASF.");
                    } else {
                        warnMessages.add("No POM " + toConfiguration(tags, null)
                                + " value is defined, it is highly recommended to have one.");
                    }
                } else {
                    if (asfExtOptions.isIncluded()) {
                        errorMessages.add("A " + toConfiguration(tags, null) + " parameter is required by ASF.");
                    } else {
                        warnMessages.add("No " + toConfiguration(tags, null)
                                + " parameter defined, it is highly recommended to have one.");
                    }
                }
                break;
            case REQUIRED_BY_ASF:
                if (isPom) {
                    errorMessages.add("A POM " + toConfiguration(tags, null) + " value is required by ASF.");
                } else {
                    errorMessages.add("A " + toConfiguration(tags, null) + " parameter is required by ASF.");
                }
                break;
            case RECOMMENDED:
                warnMessages.add("No " + toConfiguration(tags, null)
                        + " parameter defined, it is highly recommended to have one.");
                break;
            case INVALID_URL:
                if (isPom) {
                    errorMessages.add("The POM " + toConfiguration(tags, value) + " value is not a valid URL.");
                } else {
                    errorMessages.add("The " + toConfiguration(tags, value) + " parameter is not a valid URL.");
                }
                break;
            case INVALID_DATE:
                errorMessages.add("The " + toConfiguration(tags, value) + " parameter should be in YYYY-MM-DD.");
                break;
            case INVALID_EMAIL:
                errorMessages.add("The POM " + toConfiguration(tags, value) + " value is not a valid email.");
                break;
            case INVALID_ISO_DATE:
                errorMessages.add("The " + toConfiguration(tags, value) + " parameter is not a valid ISO language.");
                break;
            case SHORT_DESC_TOO_LONG:
                errorMessages.add("The " + toConfiguration(tags, value)
                        + " first sentence is too long maximum words number is 10.");
                break;
            default:
                throw new IllegalArgumentException("Unknown errorId=" + errorId);
        }
    }

    /**
     * @param tags  not null
     * @param value of the last tag, could be null
     * @return the XML configuration defined in tags
     */
    protected String toConfiguration(String[] tags, String value) {
        if (tags == null) {
            throw new IllegalArgumentException("tags is required");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.length; i++) {
            if (i == tags.length - 1 && (value == null || value.isEmpty())) {
                sb.append("<").append(tags[i]).append("/>");
            } else {
                sb.append("<").append(tags[i]).append(">");
            }
        }
        if (value != null && !value.isEmpty()) {
            sb.append(value);
        }
        for (int i = tags.length - 1; i >= 0; i--) {
            if (!(i == tags.length - 1 && (value == null || value.isEmpty()))) {
                sb.append("</").append(tags[i]).append(">");
            }
        }

        return sb.toString();
    }
}
