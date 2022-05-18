/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.woonsan.unomi.dummyprediction;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.unomi.api.CustomItem;
import org.apache.unomi.api.Event;
import org.apache.unomi.api.actions.Action;
import org.apache.unomi.api.actions.ActionExecutor;
import org.apache.unomi.api.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyPredictiveLeadScoringAction implements ActionExecutor {

    private static Logger logger = LoggerFactory.getLogger(DummyPredictiveLeadScoringAction.class);

    private static final String DUMMY_LEAD_SCORING_PROPERTY = "dummyLeadScoringProbability";

    private String seed;

    @Override
    public int execute(final Action action, final Event event) {
        final int dummyPredictedScore = calculateDummyPredictedScore(event);

        if (dummyPredictedScore >= 0) {
            logger.info("Setting the dummy profile score on profile: {}", dummyPredictedScore);
            event.getProfile().setProperty(DUMMY_LEAD_SCORING_PROPERTY,
                    Integer.valueOf(dummyPredictedScore));
            return EventService.PROFILE_UPDATED;
        }

        return EventService.NO_CHANGE;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(final String seed) {
        this.seed = seed;
    }

    private int calculateDummyPredictedScore(final Event event) {
        if (!StringUtils.equals("view", event.getEventType())) {
            return -1;
        }

        final CustomItem pageItem = (CustomItem) event.getTarget();
        @SuppressWarnings("unchecked")
        final Map<String, Object> pageInfo = (Map<String, Object>) pageItem.getProperties()
                .get("pageInfo");
        final String destinationURL = (String) pageInfo.get("destinationURL");
        final String referringURL = (String) pageInfo.get("referringURL");

        logger.info("destinationURL: {}, referringURL: {}", destinationURL, referringURL);

        if (StringUtils.contains(destinationURL, "/buy_kia_morning")) {
            if (StringUtils.startsWith(referringURL, "https://www.google.com/")) {
                final String query = StringUtils.substringAfter(referringURL, "/search?q=");

                if (StringUtils.contains(query, "kia") && StringUtils.contains(query, "morning")) {
                    return 5;
                }
                else {
                    return 3;
                }
            }
            else {
                final String queryString = StringUtils.substringAfter(referringURL, "?");

                if (StringUtils.contains(queryString, "kia")
                        && StringUtils.contains(queryString, "morning")) {
                    return 4;
                }
                else {
                    return 2;
                }
            }
        }

        return 1;
    }
}
