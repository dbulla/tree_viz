package com.nurflugel.dependencyvisualizer.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Created by douglas_bullard on 1/16/16. */
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class RankingName
{
  private String name;

  public Ranking getRanking(String title) throws Exception
  {
    return Ranking.valueOf(title);
  }
}
