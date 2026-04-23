package com.devrezaur.main.service;

import com.devrezaur.main.model.Question;
import com.devrezaur.main.model.QuestionForm;
import com.devrezaur.main.model.Result;
import com.devrezaur.main.repository.QuestionRepo;
import com.devrezaur.main.repository.ResultRepo;
import com.devrezaur.main.service.factory.ResultFactory;
import com.devrezaur.main.service.scoring.ScoringStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {

  private final int NUM_OF_QUES = 5;

  private final QuestionRepo questionRepo;
  private final ResultRepo resultRepo;
  private final ScoringStrategy scoringStrategy;
  private final ResultFactory resultFactory;

  public QuestionForm getQuestions() {
    List<Question> allQuestions = questionRepo.findAll();
    List<Question> selectedQuestions = new ArrayList<>();

    Random random = new Random();
    for (int i = 0; i < NUM_OF_QUES; i++) {
      int randomIndex = random.nextInt(allQuestions.size());
      selectedQuestions.add(allQuestions.get(randomIndex));
      allQuestions.remove(randomIndex);
    }

    return new QuestionForm(selectedQuestions);
  }

  public Result evaluateAndSaveResult(String username, QuestionForm questionForm) {
    int totalCorrect = scoringStrategy.calculateScore(questionForm);
    Result result = resultFactory.createResult(username, totalCorrect);
    resultRepo.save(result);

    return result;
  }

  public List<Result> getResults() {
    return resultRepo.findAll(Sort.by(Sort.Direction.DESC, "totalCorrect"));
  }
}
