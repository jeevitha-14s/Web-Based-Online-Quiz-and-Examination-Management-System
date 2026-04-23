package com.devrezaur.main.service.scoring;

import com.devrezaur.main.model.QuestionForm;

public interface ScoringStrategy {

  int calculateScore(QuestionForm questionForm);
}
