package ExtractStatsUsingParallelCorpora;

import utilities.Indexing;
import utilities.Preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by monadiab on 2/15/16.
 */
public class WordClusters {

    public static void main(String args[])
    {
        try {
            Object[] objs = getEGYClusters(args[0]);
        }catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public static Object[] getEGYClusters(String filePath) throws IOException
    {
        HashMap<Integer, Integer> word_cluster_map= new HashMap<Integer, Integer>();
        HashMap<Integer, HashSet<Integer>> cluster_words_map= new HashMap<Integer, HashSet<Integer>>();

        BufferedReader reader= new BufferedReader(new FileReader(filePath));
        String line2read="";
        while ((line2read= reader.readLine())!= null)
        {
            line2read= line2read.trim();
            String[] splitLine= line2read.split(" ");
            String word= splitLine[0];
            int cluster= Integer.parseInt(splitLine[1]);
            if (word.contains("@@pos@@"))
                word= word.split("@@pos@@")[0];

            word= Preprocessing.remove_last_vowel(word);


            if (!Indexing.DIA_indexDic.containsKey(word))
                Indexing.update_DIA_Index_Dic(word);
            final int word_index= Indexing.get_DIA_Index(word);

            if (!word_cluster_map.containsKey(word_index))
                word_cluster_map.put(word_index,cluster);

            if (!cluster_words_map.containsKey(cluster))
                cluster_words_map.put(cluster, new HashSet<Integer>(){{add(word_index);}});
            else
                cluster_words_map.get(cluster).add(word_index);

        }

        return new Object[]{word_cluster_map, cluster_words_map};
    }


    public static HashSet<Integer> expand_EGY_set_with_clusters(HashSet<Integer> EGY_words, HashMap<Integer, Integer> word_cluster_map,
                                                    HashMap<Integer, HashSet<Integer>> cluster_words_map)
    {
        HashSet<Integer> expanded_EGY_set= new HashSet<Integer>();

        for (int EGY:EGY_words)
        {
            if (word_cluster_map.containsKey(EGY))
            {
                int cluster= word_cluster_map.get(EGY);
                expanded_EGY_set.addAll(cluster_words_map.get(cluster));
            }
        }
       return expanded_EGY_set;
    }

    ///////////////////////////////////////////////////////////////////
    // functions for the time we expand EGY with cross-lingual synonyms

    public static HashMap<Integer, HashSet<Integer>> getEGYSynonyms(String synonymsFilePath, double similarityThreshold) throws IOException
    {
        HashMap<Integer, HashSet<Integer>> EGY_synonyms= new HashMap<Integer, HashSet<Integer>>();
        BufferedReader reader= new BufferedReader(new FileReader(synonymsFilePath));

        String line2read= "";
        while ((line2read= reader.readLine())!= null)
        {
            line2read= line2read.trim();
            String[] splitLine= line2read.split("\t");
            String Tharwa_EGY_word= splitLine[0];

            int Tharwa_EGY_index=0;

            //get Tharwa EGY word index
            if (!Indexing.DIA_indexDic.containsKey(Tharwa_EGY_word))
                Indexing.update_DIA_Index_Dic(Tharwa_EGY_word);
            Tharwa_EGY_index= Indexing.get_DIA_Index(Tharwa_EGY_word);

            for (String synonym: Arrays.copyOfRange(splitLine, 1, splitLine.length))
            {
                String word= Preprocessing.remove_last_vowel(synonym.split("_")[1]);
                float similarity= Float.parseFloat(synonym.split("_")[0]);

                if (!Indexing.DIA_indexDic.containsKey(word))
                    Indexing.update_DIA_Index_Dic(word);
                final int word_index= Indexing.get_DIA_Index(word);

                if (similarity >= similarityThreshold)
                {
                    if (!EGY_synonyms.containsKey(Tharwa_EGY_index))
                        EGY_synonyms.put(Tharwa_EGY_index, new HashSet<Integer>(){{add(word_index);}});
                    else
                        EGY_synonyms.get(Tharwa_EGY_index).add(word_index);
                }
            }
        }

        return EGY_synonyms;
    }

    public static HashSet<Integer> expand_EGY_set_with_synonyms(HashSet<Integer> EGY_words, HashMap<Integer, HashSet<Integer>> EGY_synonyms)
    {
        HashSet<Integer> expanded_EGY_set= new HashSet<Integer>();

        for (int EGY:EGY_words)
        {
            if (EGY_synonyms.containsKey(EGY))
            {
                expanded_EGY_set.addAll(EGY_synonyms.get(EGY));
            }
        }
        return expanded_EGY_set;
    }

}
