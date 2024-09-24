package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Match;
import com.fasterxml.jackson.databind.JsonNode;

public interface MatchService {
    Match getMatchInfo(JsonNode node);
}
