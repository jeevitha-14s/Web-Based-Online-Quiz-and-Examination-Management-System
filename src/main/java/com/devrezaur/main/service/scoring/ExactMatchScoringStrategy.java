package com.devrezaur.main.service.scoring;

import com.devrezaur.main.model.Question;
import com.devrezaur.main.model.QuestionForm;
import org.springframework.stereotype.Component;

@Component
public class ExactMatchScoringStrategy implements ScoringStrategy {

  @Override
  public int calculateScore(QuestionForm questionForm) {
    int totalCorrect = 0;

    for (Question question : questionForm.getQuestions()) {
      if (question.getCorrectAns() == question.getSelectedAns()) {
        totalCorrect++;
      }
    }

    return totalCorrect;
  }
}
