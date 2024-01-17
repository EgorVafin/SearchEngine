package searchengine;

import java.io.IOException;
import java.util.Map;

public class TestMorph {

    public static void main(String[] args) throws IOException {

//        LuceneMorphology luceneMorph =
//                new RussianLuceneMorphology();
//        List<String> wordBaseForms =
//                luceneMorph.getNormalForms("восприятия");
//        wordBaseForms.forEach(System.out::println);

        LemmaParser lemmaParser = new LemmaParser();

        String text = "Повторное появление-   в   леопарда в Осетии позволяет предположить," +
                "что леопард 1235 цоhоро test постоянно обитает в некоторых районах Северного" +
                "Кавказа.";

        Map<String, Integer> lemmas = lemmaParser.parseLemmas(text);
        lemmas.forEach((k, v) -> System.out.println(k + " - " + v));






    }
}
