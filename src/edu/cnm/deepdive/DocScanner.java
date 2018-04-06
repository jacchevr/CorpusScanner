package edu.cnm.deepdive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class DocScanner {

  public static final String FILE_PATH = "/Users/jacqueschevrier/Desktop/ResumeCorpus";
  private static final String[] EXCLUSIONS = {"and", "nor", "not", "but", "else", "the", "then", "for",
      "when", "which", "where", "there", "from", "him", "her", "it's", "his", "their", "are", "our", "howev",
      "through", "that", "those", "does", "doesn't", "isn't", "aren't", "some", "any", "none", "all", "over",
      "new", "mexico", "albuquerque"};

  static Pattern splitter = Pattern.compile("['\"()\\[\\]\\.,;]*\\s+['\"()\\[\\]\\.,;]*");
  static SnowballStemmer stemmer = new englishStemmer();
  static List<String> exclusion = Arrays.asList(EXCLUSIONS);

  public static void main(String[] args) {
    File directory = new File(FILE_PATH);
    exclusion = Arrays.stream(EXCLUSIONS).map(word -> {
      stemmer.setCurrent(word);
      return stemmer.stem() ? stemmer.getCurrent().toLowerCase() : null;
    })
    .collect(Collectors.toList());
    for (int i = 0 ; i < directory.listFiles().length ; i++) {
      File individual = directory.listFiles()[i];
      System.out.println(scanFile(individual));
    }
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
