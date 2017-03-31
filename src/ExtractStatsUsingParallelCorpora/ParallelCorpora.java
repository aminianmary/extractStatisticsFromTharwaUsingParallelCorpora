package ExtractStatsUsingParallelCorpora;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import utilities.Indexing;
import utilities.Preprocessing;

/**
 * Created by Maryam Aminian
 */

//////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////  Building Alignment Dictionaries /////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////


public class ParallelCorpora {

    public static ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>> buildDA_EnAlignDic(String Ar_corpus, String En_corpus,
                                              String Allign_file, String outputDir, String prefix) throws IOException
    {

        System.out.println("Building DIA-En Alignment Dics Started...");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));

        BufferedWriter parallel_data_badrashiny_requested_format_writer=
                new BufferedWriter(new FileWriter(outputDir+"/parallel_data_EGY_Mohamed_requested_format"));



        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        final HashMap<Integer, HashMap<Integer, HashSet<Integer>>>  En_Ar_Align_Dic =
                new  HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
        final HashMap<Integer, HashMap<Integer, HashSet<Integer>>>  Ar_En_Align_Dic =
                new  HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();

        int sentenceID = -1;
        int totalNumberTokens=0;
        int totalNumberEn_ArTuples=0;
        int totalNumberAr_EnTuples=0;

        while (((Ar_line_to_read = Ar_data_reader.readLine()) != null))
        {
            En_line_to_read = En_data_reader.readLine();
            Allign_line_to_read = Allign_file_reader.readLine();


            sentenceID++;
            //System.out.print(sentenceID + "\n");
            // System.out.print(Ar_line_to_read);
            //  System.out.print(En_line_to_read);
            // System.out.print(Allign_line_to_read);
            // System.out.println(Allign_line_to_read);
            if(sentenceID %10000==0)
                System.out.println(sentenceID);
            if (!Allign_line_to_read.trim().equals("") && !Ar_line_to_read.equals("") && !En_line_to_read.equals(""))
            {

                String[] Ar_words = Ar_line_to_read.split(" ");
                String[] En_words = En_line_to_read.split(" ");
                String[] Allign_words = Allign_line_to_read.split(" ");
                totalNumberTokens += Ar_words.length;

                for (int i = 0; i < Allign_words.length; i++) {
                    // finding indexes from alignment data
                    Integer ArabicIndex = Integer.parseInt(Allign_words[i].split("-")[0]);
                    Integer EngIndex = Integer.parseInt(Allign_words[i].toString().split("-")[1]);

                    // finding words
                    String ArabicWord = Ar_words[ArabicIndex]; //Preprocessing.removeInsideWordPlus(Ar_words[ArabicIndex]);
                    String ArabicLemma = ArabicWord;
                    String ArabicPOS = "";

                    if (ArabicWord.contains("@@pos@@")) {
                        ArabicLemma = ArabicWord.split("@@pos@@")[0].trim();
                        ArabicPOS = ArabicWord.split("@@pos@@")[1].trim();
                    }


                    String EngLemma = En_words[EngIndex];
                    ArabicLemma= Preprocessing.remove_last_vowel(ArabicLemma);

                    //writing Badrashiny requested data
                    parallel_data_badrashiny_requested_format_writer.write(ArabicLemma+"\t"+ArabicPOS+"\t"+EngLemma+"\t"+EngIndex+"\t"+En_line_to_read+"\n");


                   // finging indexes in indexed dictionaries for DIA data

                    Integer DIA_index;
                    Integer En_index;
                    final Integer POS_index;


                    if (!Indexing.DIA_indexDic.containsKey(ArabicLemma))
                        Indexing.update_DIA_Index_Dic(ArabicLemma);
                    if (!Indexing.En_indexDic.containsKey(EngLemma))
                        Indexing.update_En_Index_Dic(EngLemma);
                    if (!Indexing.POS_indexDic.containsKey(ArabicPOS))
                        Indexing.update_POS_Index_Dic(ArabicPOS);

                    DIA_index = Indexing.get_DIA_Index(ArabicLemma);
                    En_index = Indexing.get_En_Index(EngLemma);
                    POS_index = Indexing.get_POS_Index(ArabicPOS);

                    //System.out.println("Arabic Word: "+ ArabicWord);
                    //System.out.println("Arabic POS: "+ ArabicPOS);
                    //System.out.println("Retrived POS index: " + POS_index);


                    //Update En_Ar_Align_Dic
                    if (!En_Ar_Align_Dic.containsKey(En_index)) {
                        HashMap<Integer, HashSet<Integer>> tempHashMap = new HashMap<Integer, HashSet<Integer>>();
                        tempHashMap.put(DIA_index, new HashSet<Integer>(){{add(POS_index);}});
                        En_Ar_Align_Dic.put(En_index, tempHashMap);
                        totalNumberEn_ArTuples += 1;
                    } else {
                        if (!En_Ar_Align_Dic.get(En_index).containsKey(DIA_index)) {
                            En_Ar_Align_Dic.get(En_index).put(DIA_index, new HashSet<Integer>(){{add(POS_index);}});
                            totalNumberEn_ArTuples += 1;
                        } else if (!En_Ar_Align_Dic.get(En_index).get(DIA_index).contains(POS_index)) {
                            En_Ar_Align_Dic.get(En_index).get(DIA_index).add(POS_index);
                            totalNumberEn_ArTuples += 1;
                        }
                    }

                    //Update Ar_En_Align_Dic
                    if (!Ar_En_Align_Dic.containsKey(DIA_index)) {
                        HashMap<Integer, HashSet<Integer>> tempHashMap = new HashMap<Integer, HashSet<Integer>>();
                        tempHashMap.put(En_index, new HashSet<Integer>(){{add(POS_index);}});
                        Ar_En_Align_Dic.put(DIA_index, tempHashMap);
                        totalNumberAr_EnTuples++;
                    } else {
                        if (!Ar_En_Align_Dic.get(DIA_index).containsKey(En_index)) {
                            Ar_En_Align_Dic.get(DIA_index).put(En_index, new HashSet<Integer>(){{add(POS_index);}});
                            totalNumberAr_EnTuples++;
                        } else if (!Ar_En_Align_Dic.get(DIA_index).get(En_index).contains(POS_index)) {
                            Ar_En_Align_Dic.get(DIA_index).get(En_index).add(POS_index);
                            totalNumberAr_EnTuples++;
                        }
                    }
                }
            }
            else
            {
                if (Allign_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": alignment is empty");

                if (En_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": English is empty");

                if (Ar_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": Arabic is empty");
            }
        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();

        parallel_data_badrashiny_requested_format_writer.flush();
        parallel_data_badrashiny_requested_format_writer.close();

        System.out.println("Building DA-En Alignment Dics Finished!");
        System.out.println("En_Index_dic size " + Indexing.En_indexDic.size());
        System.out.println("DA_Index_Dic_dic size " + Indexing.DIA_indexDic.size()+"\n----------------\n");

        System.out.println("Total Number of Tokens in the Arabic side  " + totalNumberTokens);
        System.out.println("Total Number of Tuples in the En-Ar Alignment Dic  "+totalNumberEn_ArTuples);
        System.out.println("Total Number of Tuples in the Ar-En Alignment Dic  "+totalNumberAr_EnTuples+"\n" +
                "----------------\n");

        System.out.println("Total Number of Entries in DA-En Alignment Dic " + En_Ar_Align_Dic.size());
        System.out.println("Total Number of Entries in En-DA Alignment Dic " + Ar_En_Align_Dic.size());

        System.out.println("***************************\n");

        write_paralle_dic_egy(Ar_En_Align_Dic, outputDir+"/parallel_dic_EGY");

        return  new ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>>(){{add(En_Ar_Align_Dic); add(Ar_En_Align_Dic);}};

    }


    public static ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>> buildMSA_EnAllignDataDic(String Ar_corpus, String En_corpus,
                                                    String Allign_file, String outputDir, String prefix) throws IOException
    {

        System.out.println("Building MSA-En Alignment Dic Started...");

        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(Ar_corpus));
        BufferedReader En_data_reader = new BufferedReader(new FileReader(En_corpus));
        BufferedReader Allign_file_reader = new BufferedReader(new FileReader(Allign_file));

        BufferedWriter parallel_data_badrashiny_requested_format_writer=
                new BufferedWriter(new FileWriter(outputDir+"/parallel_data_MSA_Mohamed_requested_format"));


        String Ar_line_to_read = "";
        String En_line_to_read = "";
        String Allign_line_to_read = "";

        final HashMap<Integer, HashMap<Integer, HashSet<Integer>>>  En_Ar_Align_Dic =
                new  HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
        final HashMap<Integer, HashMap<Integer, HashSet<Integer>>> Ar_En_Align_Dic =
                new  HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();

        int totalNumberEn_ArTuples=0;
        int totalNumberAr_EnTuples=0;

        int sentenceID = -1;
        int totalNumberTokens=0;

        while (((Ar_line_to_read = Ar_data_reader.readLine()) != null))
        {
            En_line_to_read = En_data_reader.readLine();
            Allign_line_to_read = Allign_file_reader.readLine();

            sentenceID++;
            if (sentenceID%100000==0)
                System.out.print(sentenceID + "\n");
            //System.out.print(Ar_line_to_read);
            // System.out.print(En_line_to_read);
            //System.out.print(Allign_line_to_read);
            //System.out.println(Allign_line_to_read);
            if (!Allign_line_to_read.trim().equals("") && !Ar_line_to_read.equals("") && !En_line_to_read.equals(""))
            {
                String[] Ar_words = Ar_line_to_read.split(" ");
                String[] En_words = En_line_to_read.split(" ");
                String[] Allign_words = Allign_line_to_read.split(" ");
                totalNumberTokens += Ar_words.length;

                for (int i = 0; i < Allign_words.length; i++) {
                    // finding indexes from alignment data
                    Integer ArabicIndex = Integer.parseInt(Allign_words[i].split("-")[0]);
                    Integer EngIndex = Integer.parseInt(Allign_words[i].toString().split("-")[1]);

                    // finding words
                    String ArabicWord = Ar_words[ArabicIndex]; //Preprocessing.removeInsideWordPlus(Ar_words[ArabicIndex]);
                    String ArabicLemma = ArabicWord;
                    String ArabicPOS = "";

                    //if word has POS tag
                    if (ArabicWord.contains("@@pos@@")) {
                        ArabicLemma = ArabicWord.split("@@pos@@")[0].trim();
                        ArabicPOS = ArabicWord.split("@@pos@@")[1].trim();
                    }

                    String EngLemma = En_words[EngIndex];
                    ArabicLemma= Preprocessing.remove_last_vowel(ArabicLemma);

                    //writing Badrashiny requested data
                    parallel_data_badrashiny_requested_format_writer.write(ArabicLemma+"\t"+ArabicPOS+"\t"+EngLemma+"\t"+EngIndex+"\t"+En_line_to_read+"\n");


                    // finging indexes in indexed dictionaries for DIA data
                    Integer MSA_index;
                    Integer En_index;
                    final Integer POS_index;

                    if (!Indexing.MSA_indexDic.containsKey(ArabicLemma))
                        Indexing.update_MSA_Index_Dic(ArabicLemma);
                    if (!Indexing.En_indexDic.containsKey(EngLemma))
                        Indexing.update_En_Index_Dic(EngLemma);
                    if (!Indexing.POS_indexDic.containsKey(ArabicPOS))
                        Indexing.update_POS_Index_Dic(ArabicPOS);


                    MSA_index = Indexing.get_MSA_Index(ArabicLemma);
                    En_index = Indexing.get_En_Index(EngLemma);
                    POS_index = Indexing.get_POS_Index(ArabicPOS);

                    //System.out.println("Arabic Word: "+ ArabicWord);
                    //System.out.println("Arabic POS: "+ ArabicPOS);
                    //System.out.println("Retrived POS index: " + POS_index);

                    // Constructing En_Ar_Alignment Dictionary
                    if (!En_Ar_Align_Dic.containsKey(En_index)) {
                        HashMap<Integer, HashSet<Integer>> tempHashMap = new HashMap<Integer, HashSet<Integer>>();

                        tempHashMap.put(MSA_index, new HashSet<Integer>(){{add(POS_index);}});

                        En_Ar_Align_Dic.put(En_index, tempHashMap);
                        totalNumberEn_ArTuples += 1;
                    } else {
                        if (!En_Ar_Align_Dic.get(En_index).containsKey(MSA_index)) {
                            En_Ar_Align_Dic.get(En_index).put(MSA_index, new HashSet<Integer>(){{add(POS_index);}});
                            totalNumberEn_ArTuples += 1;
                        } else if (!En_Ar_Align_Dic.get(En_index).get(MSA_index).contains(POS_index)) {
                            En_Ar_Align_Dic.get(En_index).get(MSA_index).add(POS_index);
                            totalNumberEn_ArTuples += 1;
                        }
                    }

                    //Update Ar_En_Align_Dic
                    if (!Ar_En_Align_Dic.containsKey(MSA_index)) {
                        HashMap<Integer, HashSet<Integer>> tempHashMap = new HashMap<Integer, HashSet<Integer>>();
                        tempHashMap.put(En_index, new HashSet<Integer>(){{add(POS_index);}});

                        Ar_En_Align_Dic.put(MSA_index, tempHashMap);

                        totalNumberAr_EnTuples++;
                    } else {
                        if (!Ar_En_Align_Dic.get(MSA_index).containsKey(En_index)) {
                            Ar_En_Align_Dic.get(MSA_index).put(En_index,  new HashSet<Integer>(){{add(POS_index);}});
                            totalNumberAr_EnTuples++;
                        } else if (!Ar_En_Align_Dic.get(MSA_index).get(En_index).contains(POS_index)) {
                            Ar_En_Align_Dic.get(MSA_index).get(En_index).add(POS_index);
                            totalNumberAr_EnTuples++;
                        }

                    }
                }
            }
            else
            {
                if (Allign_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": alignment is empty");

                if (En_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": English is empty");

                if (Ar_line_to_read.equals(""))
                    System.out.println("Sentence "+ sentenceID+": Arabic is empty");
            }

        }

        Ar_data_reader.close();
        En_data_reader.close();
        Allign_file_reader.close();
        parallel_data_badrashiny_requested_format_writer.flush();
        parallel_data_badrashiny_requested_format_writer.close();

        System.out.println("Building MSA-En Alignment Dics Finished!");
        System.out.println("En_Index_dic size " + Indexing.En_indexDic.size());
        System.out.println("MSA_Index_Dic_dic size " + Indexing.MSA_indexDic.size()+"\n----------------\n");

        System.out.println("Total Number of Tokens in the Arabic side  " + totalNumberTokens);
        System.out.println("Total Number of Tuples in the En-Ar Alignment Dic  "+totalNumberEn_ArTuples);
        System.out.println("Total Number of Tuples in the Ar-En Alignment Dic  "+totalNumberAr_EnTuples+"\n" +
                "----------------\n");

        System.out.println("Total Number of Entries in MSA-En Alignment Dic " + En_Ar_Align_Dic.size());
        System.out.println("Total Number of Entries in En-MSA Alignment Dic " + Ar_En_Align_Dic.size());

        System.out.println("***************************\n");

        write_paralle_dic_msa(Ar_En_Align_Dic, outputDir+"/parallel_dic_MSA" );

        return new ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>>(){{add(En_Ar_Align_Dic); add(Ar_En_Align_Dic);}};

    }


    public static void write_paralle_dic_egy(HashMap<Integer, HashMap<Integer,
            HashSet<Integer>>> EGY_En_Align_Dic, String output) throws IOException
    {
        BufferedWriter outputWriter= new BufferedWriter(new FileWriter(output));

        for (int ar: EGY_En_Align_Dic.keySet())
        {
            String ar_word= Indexing.get_DIA_Word(ar);

            for (int en: EGY_En_Align_Dic.get(ar).keySet())
            {
                String en_word= Indexing.get_En_Word(en);

                for (int pos: EGY_En_Align_Dic.get(ar).get(en))
                {
                    String POS= Indexing.get_POS(pos);
                    outputWriter.write(ar_word + "\t" +POS+"\t"+en_word+"\n");
                }
            }
        }

        outputWriter.flush();
        outputWriter.close();
    }


    public static void write_paralle_dic_msa(HashMap<Integer, HashMap<Integer, HashSet<Integer>>> MSA_En_Align_Dic,
                                             String output) throws IOException
    {
        BufferedWriter outputWriter= new BufferedWriter(new FileWriter(output));

        for (int ar: MSA_En_Align_Dic.keySet())
        {
            String ar_word= Indexing.get_MSA_Word(ar);

            for (int en: MSA_En_Align_Dic.get(ar).keySet())
            {
                String en_word= Indexing.get_En_Word(en);
                for (int pos: MSA_En_Align_Dic.get(ar).get(en)) {

                    String POS = Indexing.get_POS(pos);
                    outputWriter.write(ar_word + "\t" + POS + "\t" + en_word + "\n");
                }

            }
        }

        outputWriter.flush();
        outputWriter.close();
    }

    public static HashMap<Integer, HashMap<Integer, HashSet<Integer>>> create_synonym_dic(String transDic_En_pos, String transDic_En_pos_syn) throws IOException
    {
        BufferedReader transDic_En_pos_reader= new BufferedReader(new FileReader(transDic_En_pos));
        BufferedReader transDic_En_pos_syn_reader= new BufferedReader(new FileReader(transDic_En_pos_syn));

        HashMap<Integer, HashMap<Integer, HashSet<Integer>>> synonymDic= new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();

        String transDic_En_pos_line2Read="";
        String transDic_En_pos_syn_line2Read="";

        int counter=0;
        while ((transDic_En_pos_line2Read= transDic_En_pos_reader.readLine())!=null)
        {
            counter++;
            transDic_En_pos_syn_line2Read= transDic_En_pos_syn_reader.readLine().trim();
            //System.out.println(counter+": "+transDic_En_pos_line2Read);
            //System.out.println(counter+": "+transDic_En_pos_syn_line2Read);

            String pos= transDic_En_pos_line2Read.split("\t")[0];
            String en= transDic_En_pos_line2Read.split("\t")[1];


            int en_index= Indexing.get_En_Index(en);
            int pos_index= Indexing.get_POS_Index(pos);

            HashSet<Integer> synonyms= new HashSet<Integer>();

            for (String syn: transDic_En_pos_syn_line2Read.split(";;"))
            {
                Integer index = 0;

                syn= syn.replace("_"," ");
                if (!Indexing.En_indexDic.containsKey(syn))
                    Indexing.update_En_Index_Dic(syn);

                index = Indexing.get_En_Index(syn);

                synonyms.add(index);
            }

            if (!synonymDic.containsKey(en_index)) {
                HashMap<Integer, HashSet<Integer>> temp= new HashMap<Integer, HashSet<Integer>>();
                temp.put(pos_index, synonyms);
                synonymDic.put(en_index, temp);
            }
            else if (!synonymDic.get(en_index).containsKey(pos_index))
                synonymDic.get(en_index).put(pos_index, synonyms);
        }

        return synonymDic;


    }


    public static ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>> loadMSAAlignmentDic (String pathToTheFile) throws IOException
    {

        final HashMap<Integer, HashMap<Integer, HashSet<Integer>>>  En_Ar_Align_Dic = new  HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
        final HashMap<Integer, HashMap<Integer, HashSet<Integer>>> Ar_En_Align_Dic = new  HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();


        BufferedReader reader= new BufferedReader(new FileReader(pathToTheFile));
        String line2read= "";
        int counter=0;
        while ((line2read= reader.readLine())!= null)
        {
            counter++;

            if (counter%1000==0)
                System.out.println(counter);

            String[] splitLine= line2read.trim().split("\t");
            if (splitLine.length==3) {
                String MSAWord = splitLine[0];
                String posTag = splitLine[1];
                String EnglishWord = splitLine[2].replaceAll("_"," ");

                MSAWord= Preprocessing.remove_last_vowel(MSAWord);

                Integer MSAIndex ;
                Integer EnIndex ;
                final Integer POSIndex;

                if (!Indexing.MSA_indexDic.containsKey(MSAWord))
                    Indexing.update_MSA_Index_Dic(MSAWord);
                if (!Indexing.En_indexDic.containsKey(EnglishWord))
                    Indexing.update_En_Index_Dic(EnglishWord);
                if (!Indexing.POS_indexDic.containsKey(posTag))
                    Indexing.update_POS_Index_Dic(posTag);


                MSAIndex = Indexing.get_MSA_Index(MSAWord);
                EnIndex = Indexing.get_En_Index(EnglishWord);
                POSIndex = Indexing.get_POS_Index(posTag);

                // Constructing En_Ar_Alignment Dictionary
                if (!En_Ar_Align_Dic.containsKey(EnIndex)) {
                    HashMap<Integer, HashSet<Integer>> tempHashMap = new HashMap<Integer, HashSet<Integer>>();

                    tempHashMap.put(MSAIndex, new HashSet<Integer>() {{add(POSIndex);}});

                    En_Ar_Align_Dic.put(EnIndex, tempHashMap);

                } else {
                    if (!En_Ar_Align_Dic.get(EnIndex).containsKey(MSAIndex)) {
                        En_Ar_Align_Dic.get(EnIndex).put(MSAIndex, new HashSet<Integer>(){{add(POSIndex);}});

                    } else if (!En_Ar_Align_Dic.get(EnIndex).get(MSAIndex).contains(POSIndex)) {
                        En_Ar_Align_Dic.get(EnIndex).get(MSAIndex).add(POSIndex);
                    }
                }

                //Update Ar_En_Align_Dic
                if (!Ar_En_Align_Dic.containsKey(MSAIndex)) {
                    HashMap<Integer, HashSet<Integer>> tempHashMap = new HashMap<Integer, HashSet<Integer>>();
                    tempHashMap.put(EnIndex, new HashSet<Integer>(){{add(POSIndex);}});

                    Ar_En_Align_Dic.put(MSAIndex, tempHashMap);

                } else {
                    if (!Ar_En_Align_Dic.get(MSAIndex).containsKey(EnIndex)) {
                        Ar_En_Align_Dic.get(MSAIndex).put(EnIndex, new HashSet<Integer>(){{add(POSIndex);}});
                    } else if (!Ar_En_Align_Dic.get(MSAIndex).get(EnIndex).contains(POSIndex)) {
                        Ar_En_Align_Dic.get(MSAIndex).get(EnIndex).add(POSIndex);
                    }

                }
            }

        }

        return new ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>>(){{add(En_Ar_Align_Dic); add(Ar_En_Align_Dic);}};
    }


    public static ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>> loadEGYAlignmentDic (String pathToTheFile) throws IOException
    {

        final HashMap<Integer, HashMap<Integer, HashSet<Integer>>>  En_Ar_Align_Dic =
                new  HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
        final HashMap<Integer, HashMap<Integer, HashSet<Integer>>> Ar_En_Align_Dic =
                new  HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();


        BufferedReader reader= new BufferedReader(new FileReader(pathToTheFile));
        String line2read= "";
        int counter=0;
        while ((line2read= reader.readLine())!= null)
        {
            counter++;

            if (counter%1000==0)
                System.out.println(counter);

            String[] splitLine= line2read.trim().split("\t");

            if (splitLine.length==3) {
                String EGYWord = splitLine[0];
                String posTag = splitLine[1];
                String EnglishWord = splitLine[2].replaceAll("_"," ");

                EGYWord= Preprocessing.remove_last_vowel(EGYWord);

                Integer EGYIndex ;
                Integer EnIndex ;
                final Integer POSIndex ;

                if (!Indexing.DIA_indexDic.containsKey(EGYWord))
                    Indexing.update_DIA_Index_Dic(EGYWord);
                if (!Indexing.En_indexDic.containsKey(EnglishWord))
                    Indexing.update_En_Index_Dic(EnglishWord);
                if (!Indexing.POS_indexDic.containsKey(posTag))
                    Indexing.update_POS_Index_Dic(posTag);


                EGYIndex = Indexing.get_DIA_Index(EGYWord);
                EnIndex = Indexing.get_En_Index(EnglishWord);
                POSIndex = Indexing.get_POS_Index(posTag);

                // Constructing En_Ar_Alignment Dictionary
                if (!En_Ar_Align_Dic.containsKey(EnIndex)) {
                    HashMap<Integer, HashSet<Integer>> tempHashMap = new HashMap<Integer, HashSet<Integer>>();

                    tempHashMap.put(EGYIndex, new HashSet<Integer>(){{add(POSIndex);}});

                    En_Ar_Align_Dic.put(EnIndex, tempHashMap);

                } else {
                    if (!En_Ar_Align_Dic.get(EnIndex).containsKey(EGYIndex)) {
                        En_Ar_Align_Dic.get(EnIndex).put(EGYIndex, new HashSet<Integer>(){{add(POSIndex);}});

                    } else if (!En_Ar_Align_Dic.get(EnIndex).get(EGYIndex).contains(POSIndex)) {
                        En_Ar_Align_Dic.get(EnIndex).get(EGYIndex).add(POSIndex);
                    }
                }

                //Update Ar_En_Align_Dic
                if (!Ar_En_Align_Dic.containsKey(EGYIndex)) {
                    HashMap<Integer, HashSet<Integer>> tempHashMap = new HashMap<Integer, HashSet<Integer>>();
                    tempHashMap.put(EnIndex, new HashSet<Integer>(){{add(POSIndex);}});

                    Ar_En_Align_Dic.put(EGYIndex, tempHashMap);

                } else {
                    if (!Ar_En_Align_Dic.get(EGYIndex).containsKey(EnIndex)) {
                        Ar_En_Align_Dic.get(EGYIndex).put(EnIndex, new HashSet<Integer>(){{add(POSIndex);}});
                    } else if (!Ar_En_Align_Dic.get(EGYIndex).get(EnIndex).contains(POSIndex)) {
                        Ar_En_Align_Dic.get(EGYIndex).get(EnIndex).add(POSIndex);
                    }

                }
            }

        }

        return new ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>>(){{add(En_Ar_Align_Dic); add(Ar_En_Align_Dic);}};
    }

}
