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
    public int execute(Action action, Event event) {
        int dummyPredictedScore = -1;

        if ("view".equals(event.getEventType())) {
            final CustomItem pageItem = (CustomItem) event.getTarget();
            final Map<String, Object> pageInfo = (Map<String, Object>) pageItem.getProperties()
                    .get("pageInfo");
            final String landingPageId = (String) pageInfo.get("destinationURL");
            final String referrerId = (String) pageInfo.get("referringURL");

            // TODO
            dummyPredictedScore = 5;
        }

        if (dummyPredictedScore >= 0) {
            logger.info("Dummy profile score is now: " + dummyPredictedScore);
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
}
