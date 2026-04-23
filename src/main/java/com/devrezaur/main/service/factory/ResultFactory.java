package com.devrezaur.main.service.factory;

import com.devrezaur.main.model.Result;
import org.springframework.stereotype.Component;

@Component
public class ResultFactory {

  public Result createResult(String username, int totalCorrect) {
    return Result.builder()
        .username(username)
        .totalCorrect(totalCorrect)
        .build();
  }
}
