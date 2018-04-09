package edu.cnm.deepdive;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class DocScanner {

  private static final String FILE_PATH = "/Users/jacqueschevrier/Desktop/ResumeCorpus";
  private static final String[] EXCLUSIONS = {"after",
      "all", "although", "and", "another", "any", "anybody", "anyone", "anything", "aren't", "astride",
      "atop", "bar", "because", "before", "behind", "below", "beneath", "beside", "besides", "between",
      "beyond", "both", "but", "can't", "come", "couldn't", "despite", "didn't", "doesn't", "don't",
      "down", "during", "each", "either", "even", "though", "everybody", "everyone", "everything",
      "except", "few", "for", "from", "hadn't", "hasn't", "haven't", "he'd", "he'll", "he's", "her",
      "hers", "herself", "him", "himself", "his", "i'd", "i'll", "i'm", "i've", "inside", "into", "isn't",
      "its", "itself", "less", "let's", "like", "many", "mine", "mightn't", "minus", "more", "most", "much",
      "mustn't", "myself", "near", "nearer", "nearest", "neither", "nobody", "none", "nor", "nothing",
      "off", "once", "one", "onto", "ontop", "opposite", "other", "others", "our", "ours", "ourselves",
      "out", "outside", "over", "pace", "past", "per", "plus", "post", "pre", "sans", "sauf", "save",
      "several", "shan't", "she", "she'd", "she'll", "she's", "short", "shouldn't", "since", "sithence",
      "some", "somebody", "someone", "something", "than", "that", "that's", "the", "their", "theirs", "them",
      "themselves", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those",
      "though", "through", "throughout", "thru", "thruout", "til", "till", "toward", "under", "underneath",
      "unless", "unlike", "until", "unto", "upon", "upside", "versus", "via", "vice versa", "we'd",
      "we're", "we've", "weren't", "what", "what'll", "what're", "what's", "what've", "whatever", "when",
      "whenever", "where's", "wherever", "whether", "which", "whichever", "while", "who", "who'll",
      "who're", "who's", "who've", "whoever", "whom", "whomever", "whose", "with", "within", "without",
      "won't", "wouldn't", "yet", "you", "you'd", "you'll", "you're", "you've", "your", "yours",
      "yourself", "yourselves", "mexico", "new", "include", "albuquerque", "equip", "use", "air",
      "staff", "patient", "task", "school", "high", "armi", "college", "intelligence", "various",
      "area", "medic", "work"};
  private static final String[] SUGGESTS = {"accomplished", "achieved", "adapted", "administered",
      "advanced", "agile", "analyzed", "attained", "balanced", "brainstormed", "budgeted", "built",
      "calculated", "centralized", "championed", "changed", "clarified", "coached", "collaborated",
      "communicated", "complied", "conceptualized", "configured", "constructed", "converted", "created",
      "decreased", "deadline", "driven", "delivered", "demonstrated", "designed", "determined",
      "determined", "developed", "devised", "directed", "distributed", "documented", "earned",
      "eliminated", "energized", "engineered", "enhanced", "ensured", "established", "evaluated",
      "exceeded", "excelled", "executed", "expedited", "extracted", "facilitated", "finalized",
      "follow", "forecast", "formed", "fulfilled", "gained", "generated", "handled", "headed"};

  static Pattern splitter = Pattern.compile("['\"()\\[\\]\\.,;]*\\s+['\"()\\[\\]\\.,;]*");
  static SnowballStemmer stemmer = new englishStemmer();
  static List<String> exclusion = Arrays.asList(EXCLUSIONS);
  static List<String> suggests = Arrays.asList(SUGGESTS);
  static File individual;

  public static void main(String[] args) throws IOException {
    File directory = new File(FILE_PATH);
    exclusion = Arrays.stream(EXCLUSIONS).map(word -> {
      stemmer.setCurrent(word);
      return stemmer.stem() ? stemmer.getCurrent().toLowerCase() : null;
    })
    .collect(Collectors.toList());
    suggests = Arrays.stream(SUGGESTS).map(word -> {
      stemmer.setCurrent(word);
      return stemmer.stem() ? stemmer.getCurrent().toLowerCase() : null;
    })
        .collect(Collectors.toList());
    List<String> allLines = new ArrayList<>();
    for (int i = 0 ; i < directory.listFiles().length ; i++) {
      File individual = directory.listFiles()[i];
      allLines.addAll(Files.readAllLines(individual.toPath()));
//      System.out.println(scanFile(individual));
    }
    Map<String, Long> collect =
        allLines.stream().map(line -> splitter.splitAsStream(line)).flatMap(Function.identity())
            .map(word -> {
              stemmer.setCurrent(word);
              return stemmer.stem() ? stemmer.getCurrent().toLowerCase() : null;
            })
            .filter(Objects::nonNull)
            .filter(word -> word.length() > 2)
            .filter(word -> !exclusion.contains(word))
            .filter(word -> org.apache.commons.lang3.StringUtils.isAlpha(word))
            .collect(groupingBy(Function.identity(), counting())).entrySet().stream()
            .filter((k) -> (k.getValue() >= 50))
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (v1, v2) -> {
                  throw new IllegalStateException();
                },
                LinkedHashMap::new
            ));
    System.out.println(collect.size());
    System.out.println(collect);


//

//    System.out.println(suggests);

  }

  private static List<String> scanFile (File individual) {
    try {
      Scanner scan = new Scanner(individual);
      return Files.lines(individual.toPath())
          .map(line -> splitter.splitAsStream(line))
          .flatMap(Function.identity())
          .map(word -> {
            stemmer.setCurrent(word);
            return stemmer.stem() ? stemmer.getCurrent().toLowerCase() : null;
          })
          .filter(Objects::nonNull)
          .filter(word -> word.length() > 2)
          .filter(word -> !exclusion.contains(word))
          .filter(word -> org.apache.commons.lang3.StringUtils.isAlpha(word))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
