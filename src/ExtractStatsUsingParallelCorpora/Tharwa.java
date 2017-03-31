package ExtractStatsUsingParallelCorpora;

import java.io.*;
import java.util.*;

import utilities.*;

/**
 * Created by monadiab on 1/8/16.
 */
public class Tharwa {

    //tharwa information
    static ArrayList<Integer> tharwa_DIA = new ArrayList<Integer>();
    static ArrayList<String> tharwa_DIA_MSA= new ArrayList<String>();
    static ArrayList<String> tharwa_DIA_MSA_EN= new ArrayList<String>();
    static int tharwaSize=0;

    public static void main(String[] args) throws IOException{
        String tharwa= args[0];
        ConvertTharwaToDic_EGYKey(tharwa,false,true);
    }


    public static HashMap<Integer, ArrayList<MSAEnTuple>> ConvertTharwaToDic_EGYKey(String tharwaPath, boolean AlefYaNormalized, boolean diacritized) throws IOException
    {
        System.out.println("***************************");
        System.out.println("Convert Tharwa To Dic Started (EGY is the key)...\n");

        File tharwaDir= new File(tharwaPath);
        String outputDir= tharwaDir.getParent();

        BufferedWriter tharwaWriter=new BufferedWriter(new FileWriter(outputDir+"/tharwa_EGYKey"));
        BufferedReader tharwaReader = new BufferedReader(new FileReader(tharwaPath));
        HashMap<Integer, ArrayList<MSAEnTuple>> tharwaDic=new HashMap<Integer, ArrayList<MSAEnTuple>>();

        String DIA_LEMMA = "";
        String MSA_Lemma = "";
        String MSA_equiv = "";
        String CODA= "";
        String Eng_gloss="";
        Integer id= 0;
        String MSA_POS_LDC="";
        String DIA_POS_LDC="";

        String LineToRead= tharwaReader.readLine();
        while ((LineToRead =tharwaReader.readLine()) != null)
        {
            String[] splitedLine = LineToRead.split("\t");

            //based on Tharwa column in the latest version of Tharwa
            id= Integer.parseInt(splitedLine[0]);
            DIA_LEMMA = splitedLine[6]; // EGY_word column
            MSA_Lemma= splitedLine[4];  //MSA-Lemma column
            MSA_equiv= splitedLine[3];  //MSA-equivalent column
            CODA= splitedLine[2];      //CODA column
            Eng_gloss= splitedLine[7]; // English-Equivalent column
            MSA_POS_LDC= splitedLine[10]; //MSA_POS_LDC column
            DIA_POS_LDC= splitedLine[8]; //EGY_POS_LDC column

            //organising English glosses
            String [] temp_ENG_glosses= Eng_gloss.replace("##",";;").split(";;");
            Vector<String> ENG_glosses= new Vector<String>();
            for (String En_token:temp_ENG_glosses)
            {
                if (En_token.contains("/"))
                {
                    if (!En_token.contains(" "))
                    {
                        if (!ENG_glosses.contains(En_token.split("/")[0]))
                            ENG_glosses.add(En_token.split("/")[0]);
                        if (!ENG_glosses.contains(En_token.split("/")[1]))
                            ENG_glosses.add(En_token.split("/")[1]);
                    }
                    else
                    {
                        String[] spaceSeparatedTokens= En_token.split(" ");
                        String[] slashSeparatedToken_1= spaceSeparatedTokens[0].split("/");
                        String[] slashSeparatedToken_2= spaceSeparatedTokens[1].split("/");

                        for (int i=0;i< slashSeparatedToken_1.length;i++)
                        {
                            for (int j=0;j< slashSeparatedToken_2.length;j++)
                            {
                                if (!ENG_glosses.contains(slashSeparatedToken_1[i]+" "+slashSeparatedToken_2[j]))
                                    ENG_glosses.add(slashSeparatedToken_1[i]+" "+slashSeparatedToken_2[j]);
                            }
                        }
                    }
                }
                else
                {
                    if (!ENG_glosses.contains(En_token))
                        ENG_glosses.add(En_token);

                }
            }

            DIA_LEMMA= Preprocessing.remove_last_vowel(DIA_LEMMA);
            MSA_Lemma= Preprocessing.remove_last_vowel(MSA_Lemma);
            MSA_equiv= Preprocessing.remove_last_vowel(MSA_equiv);
            CODA= Preprocessing.remove_last_vowel(CODA);


            if (AlefYaNormalized==true)
            {
                if (diacritized==false)
                {
                    DIA_LEMMA = (Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(DIA_LEMMA)));
                    MSA_Lemma = (Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(MSA_Lemma)));
                    MSA_equiv = (Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(MSA_equiv)));
                    CODA = (Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(CODA)));
                }
                else {
                    DIA_LEMMA = (Preprocessing.normalizeAlefYa(DIA_LEMMA));
                    MSA_Lemma = (Preprocessing.normalizeAlefYa(MSA_Lemma));
                    MSA_equiv = (Preprocessing.normalizeAlefYa(MSA_equiv));
                    CODA = (Preprocessing.normalizeAlefYa(CODA));
                }
            }
            else
            {
                if (diacritized== false)
                {
                    DIA_LEMMA= (Preprocessing.undiacratize(DIA_LEMMA));
                    MSA_Lemma= (Preprocessing.undiacratize(MSA_Lemma));
                    MSA_equiv= (Preprocessing.undiacratize(MSA_equiv));
                    CODA= (Preprocessing.undiacratize(CODA));
                }
                else
                {
                    DIA_LEMMA= (DIA_LEMMA);
                    MSA_Lemma= (MSA_Lemma);
                    CODA= (CODA);
                }
            }
            // adding CODA to the CODA_freq dictionary separatly
            Indexing.add_CODA_freq_dic(CODA, id);

            // seprating Eng glosses: each entry might have different Eng equivalents seprated by ";"
            for (int i=0;i<ENG_glosses.size();i++)
            {
                if(!ENG_glosses.get(i).equals("UNK") && ENG_glosses.get(i).length()>0)
                {
                    // wrtining into a text file

                    tharwaWriter.write(id.toString()+"\t"+MSA_POS_LDC+'\t'+ MSA_Lemma+'\t'+ENG_glosses.get(i)+'\n');

                    if (!MSA_equiv.equals(MSA_Lemma))
                        tharwaWriter.write(id.toString()+"\t"+MSA_POS_LDC+'\t'+ MSA_equiv+'\t'+ENG_glosses.get(i)+'\n');


                    // creating the thatwaDic using indexed dictionaries
                    Integer DIA_index=0;
                    Integer MSA_index = 0;
                    Integer En_index= 0;
                    Integer MSA_POS_LDC_index=0;
                    Integer DIA_POS_LDC_index=0;

                    //Using CODA word as DA word
                    if(!Indexing.DIA_indexDic.containsKey(CODA))
                        Indexing.update_DIA_Index_Dic(CODA);
                    if (!Indexing.MSA_indexDic.containsKey(MSA_Lemma))
                        Indexing.update_MSA_Index_Dic(MSA_Lemma);
                    if (! Indexing.En_indexDic.containsKey(ENG_glosses.get(i)))
                        Indexing.update_En_Index_Dic(ENG_glosses.get(i));
                    if (!MSA_POS_LDC.equals("") && !Indexing.POS_indexDic.containsKey(MSA_POS_LDC))
                        Indexing.update_POS_Index_Dic(MSA_POS_LDC);
                    if (!DIA_POS_LDC.equals("") && !Indexing.POS_indexDic.containsKey(DIA_POS_LDC))
                        Indexing.update_POS_Index_Dic(DIA_POS_LDC);

                    DIA_index = Indexing.get_DIA_Index(CODA);
                    MSA_index = Indexing.get_MSA_Index(MSA_Lemma);
                    En_index = Indexing.get_En_Index(ENG_glosses.get(i));
                    MSA_POS_LDC_index= Indexing.get_POS_Index(MSA_POS_LDC);
                    DIA_POS_LDC_index= Indexing.get_POS_Index(DIA_POS_LDC);

                    ArrayList<Integer> ids= new ArrayList<Integer>();
                    ids.add(id);

                    //tharwaWriter.write(Indexing.get_En_Word(En_index)+'\t'+ Indexing.get_DIA_Word(DIA_index)+'\t'+Indexing.get_MSA_Word(MSA_index)+'\n');


                    MSAEnTuple touple= new MSAEnTuple(MSA_index,En_index,ids, MSA_POS_LDC_index, DIA_POS_LDC_index);

                    if (!tharwaDic.containsKey(DIA_index))
                    {
                        ArrayList<MSAEnTuple> tempVector= new ArrayList<MSAEnTuple>();
                        tempVector.add(touple);
                        tharwaDic.put(DIA_index, tempVector);
                        tharwaSize++;

                        tharwa_DIA.add(DIA_index);
                        tharwa_DIA_MSA.add(DIA_index+"\t"+MSA_index);
                        tharwa_DIA_MSA_EN.add(DIA_index+"\t"+MSA_index+"\t"+En_index);
                    }
                    else if (tharwaDic.containsKey(DIA_index))
                    {
                        Boolean isTheSame=false;
                        for (int k=0;k<tharwaDic.get(DIA_index).size();k++)
                        {
                            if (tharwaDic.get(DIA_index).get(k).equals(touple))
                            {
                                tharwaDic.get(DIA_index).get(k).setIds(id);
                                isTheSame=true;
                                break;
                            }
                            else
                                tharwa_DIA_MSA.add(DIA_index+"\t"+MSA_index);

                        }
                        if (isTheSame.equals(false))
                        {
                            tharwaDic.get(DIA_index).add(touple);
                            tharwaSize++;
                            tharwa_DIA_MSA_EN.add(DIA_index+"\t"+MSA_index+"\t"+En_index);
                        }
                    }

                }
            }


            if(id %10000==0)
                System.out.println(id);
        }
        tharwaWriter.flush();
        tharwaWriter.close();
        tharwaReader.close();

        System.out.println("ConvertTharwaToDic_EGYKey finished");
        System.out.println("Numbe of ExtractStats.Tharwa Entries "+ tharwaDic.size());
        System.out.println("***************************");
        return tharwaDic;
    }


    public static HashMap<Integer, TharwaRow> ConvertTharwaToDic_IDKey(String tharwaPath,
                                                                       String EGYFilePath,
                                                                       String outputDir,
                                                                       String prefix,
                                                                       boolean AlefYaNormalized,
                                                                       boolean diacritized,
                                                                       boolean useEGYMatchedTharwa,
                                                                       boolean usePreComputedGoldTharwa,
                                                                       String path_to_preComputedGoldTharwa) throws IOException
    {
        System.out.println("***************************");
        System.out.println("Convert Tharwa To Dic Started (ID is the key)...\n");


        //get list of EGY words in the monolingual data
        HashSet<String> monolingualEGYWords= new HashSet<String>();
        HashSet<Integer> goldTharwaIDs= new HashSet<Integer>();
        HashSet<String> tharwaEGYWords= new HashSet<String>();

        if (useEGYMatchedTharwa==true)
            monolingualEGYWords= getEGYWords(EGYFilePath);
        else if (usePreComputedGoldTharwa==true)
            goldTharwaIDs= getListOfGoldTharwaEntries(path_to_preComputedGoldTharwa);


        //////////////
        // Select 10% of Tharwa rows randomly --> total number of rows: 29330, 10% of rows: 2933
        //int numberOfTharwaRows_4_test= 29329;
        //int totalNumberofTharwaRows= 29329;
        //int rowCounter=0;
        //Set<Integer> randRows =generateRandIDs(numberOfTharwaRows_4_test,totalNumberofTharwaRows);

        //File tharwaDir= new File(tharwaPath);
        //String outputDir= tharwaDir.getParent();

        BufferedWriter tharwaWriter=new BufferedWriter(new FileWriter(outputDir+"/"+prefix+"_tharwa_IDKey"));
        BufferedWriter tharwaEGYWordsWriter=new BufferedWriter(new FileWriter(outputDir+"/"+prefix+"_tharwa_IDKey_EGY_words"));

        BufferedReader tharwaReader = new BufferedReader(new FileReader(tharwaPath));
        HashMap<Integer, TharwaRow> tharwaDic=new HashMap<Integer, TharwaRow>();

        String EGY_word = "";
        String MSA_Lemma = "";
        String MSA_equiv="";
        String CODA= "";
        String Eng_gloss="";
        Integer id= 0;
        String MSA_POS_LDC="";
        String EGY_POS_LDC="";
        String EGY_lemma="";

        String LineToRead= tharwaReader.readLine();
        while ((LineToRead =tharwaReader.readLine()) != null)
        {
            //rowCounter++;
            //if (randRows.contains(rowCounter)) {

                String[] splitedLine = LineToRead.split("\t");

                //based on Tharwa column in the latest version of Tharwa
                id = Integer.parseInt(splitedLine[0]);
                CODA = splitedLine[2];      //CODA column
                MSA_equiv = splitedLine[3];  //MSA-equivalent column
                MSA_Lemma = splitedLine[4];  //MSA-Lemma column
                EGY_word = splitedLine[6]; // EGY_word column
                Eng_gloss = splitedLine[7]; // English-Equivalent column
                MSA_POS_LDC = splitedLine[10]; //MSA_POS_LDC column
                EGY_POS_LDC = splitedLine[8]; //EGY_POS_LDC column
                EGY_lemma= splitedLine[11]; //EGY_lemma column

                //organising English glosses
                String[] temp_ENG_glosses = Eng_gloss.replace("##", ";;").split(";;");
                ArrayList<String> ENG_glosses = new ArrayList<String>();
                for (String En_token : temp_ENG_glosses) {
                    if (En_token.contains("/")) {
                        if (!En_token.contains(" ")) {
                            if (!ENG_glosses.contains(En_token.split("/")[0]))
                                ENG_glosses.add(En_token.split("/")[0]);
                            if (!ENG_glosses.contains(En_token.split("/")[1]))
                                ENG_glosses.add(En_token.split("/")[1]);
                        } else {
                            String[] spaceSeparatedTokens = En_token.split(" ");
                            String[] slashSeparatedToken_1 = spaceSeparatedTokens[0].split("/");
                            String[] slashSeparatedToken_2 = spaceSeparatedTokens[1].split("/");

                            for (int i = 0; i < slashSeparatedToken_1.length; i++) {
                                for (int j = 0; j < slashSeparatedToken_2.length; j++) {
                                    if (!ENG_glosses.contains(slashSeparatedToken_1[i] + " " + slashSeparatedToken_2[j]))
                                        ENG_glosses.add(slashSeparatedToken_1[i] + " " + slashSeparatedToken_2[j]);
                                }
                            }
                        }
                    } else {
                        if (!ENG_glosses.contains(En_token))
                            ENG_glosses.add(En_token);

                    }
                }

                //organizing EGY variants
                HashSet<String> EGY_words= new HashSet<String>();

                if (AlefYaNormalized == true) {
                    if (diacritized == false) {
                        MSA_Lemma = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(MSA_Lemma)));
                        MSA_equiv = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(MSA_equiv)));

                        CODA = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(CODA)));
                        EGY_lemma = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(EGY_lemma)));

                        for (String egy: EGY_word.split(";;"))
                            EGY_words.add(Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(egy))));

                    } else {

                        MSA_Lemma = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(MSA_Lemma));
                        MSA_equiv = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(MSA_equiv));

                        CODA = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(CODA));
                        EGY_lemma = Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(EGY_lemma));

                        for (String egy: EGY_word.split(";;"))
                            EGY_words.add(Preprocessing.remove_last_vowel(Preprocessing.normalizeAlefYa(egy)));
                    }
                } else {
                    if (diacritized == false) {
                        MSA_Lemma = Preprocessing.remove_last_vowel(Preprocessing.undiacratize(MSA_Lemma));
                        MSA_equiv = Preprocessing.remove_last_vowel(Preprocessing.undiacratize(MSA_equiv));

                        CODA = Preprocessing.remove_last_vowel(Preprocessing.undiacratize(CODA));
                        EGY_lemma = Preprocessing.remove_last_vowel(Preprocessing.undiacratize(EGY_lemma));

                        for (String egy: EGY_word.split(";;"))
                            EGY_words.add(Preprocessing.remove_last_vowel(Preprocessing.undiacratize(egy)));
                    }
                    else
                    {
                        for (String egy: EGY_word.split(";;"))
                            EGY_words.add(Preprocessing.remove_last_vowel(egy));
                        MSA_Lemma= Preprocessing.remove_last_vowel(MSA_Lemma);
                        MSA_equiv= Preprocessing.remove_last_vowel(MSA_equiv);
                        CODA= Preprocessing.remove_last_vowel(CODA);
                        EGY_lemma= Preprocessing.remove_last_vowel(EGY_lemma);

                    }
                }

                //add CODA, EGY_lemma and MSA words to the list of EGY variants
                EGY_words.add(CODA);
                EGY_words.add(MSA_Lemma);
                EGY_words.add(MSA_equiv);
                if (!EGY_lemma.equals("_"))
                    EGY_words.add(EGY_lemma);

                tharwaEGYWords.addAll(EGY_words);

                // adding CODA to the CODA_freq dictionary separatly
                Indexing.add_CODA_freq_dic(CODA, id);


                // creating the thatwaDic using indexed dictionaries
                HashSet<Integer> En_equiv_indices = new HashSet<Integer>();
                for (String En : ENG_glosses) {
                    Integer En_index = 0;
                    if (!Indexing.En_indexDic.containsKey(En))
                        Indexing.update_En_Index_Dic(En);

                    En_index = Indexing.get_En_Index(En);
                    En_equiv_indices.add(En_index);
                }

                HashSet<Integer> EGY_indices = new HashSet<Integer>();
                for (String egy : EGY_words) {
                    Integer EGY_index = 0;
                    if (!Indexing.DIA_indexDic.containsKey(egy))
                        Indexing.update_DIA_Index_Dic(egy);

                    EGY_index = Indexing.get_DIA_Index(egy);
                    EGY_indices.add(EGY_index);
                }


                if (useEGYMatchedTharwa== true)
                {
                    //just EGY matched Tharwa

                    //want to match EGY-POS in the monolingual data
                    HashSet<String> EGY_words_pos=new HashSet<String>();

                    for (String EGY: EGY_words)
                        EGY_words_pos.add(EGY+"@@pos@@"+EGY_POS_LDC);

                    //EGY_indices.retainAll(monolingualEGYWords);
                    EGY_words_pos.retainAll(monolingualEGYWords);

                    if (EGY_words_pos.size() >0 )
                    {
                        //we have at least one EGY word common with monolingual data
                        // wrtining into a text file
                        tharwaWriter.write(id.toString() + "\t" + EGY_words.toString() + '\t' + MSA_Lemma+","+MSA_equiv + '\t' + ENG_glosses.toString() + '\n');


                        Integer MSA_lemma_index = 0;
                        Integer MSA_equiv_index=0;
                        Integer MSA_POS_LDC_index = 0;
                        Integer DIA_POS_LDC_index = 0;

                        if (!Indexing.MSA_indexDic.containsKey(MSA_Lemma))
                            Indexing.update_MSA_Index_Dic(MSA_Lemma);
                        if (!Indexing.MSA_indexDic.containsKey(MSA_equiv))
                            Indexing.update_MSA_Index_Dic(MSA_equiv);
                        if (!MSA_POS_LDC.equals("") && !Indexing.POS_indexDic.containsKey(MSA_POS_LDC))
                            Indexing.update_POS_Index_Dic(MSA_POS_LDC);
                        if (!EGY_POS_LDC.equals("") && !Indexing.POS_indexDic.containsKey(EGY_POS_LDC))
                            Indexing.update_POS_Index_Dic(EGY_POS_LDC);

                        MSA_lemma_index = Indexing.get_MSA_Index(MSA_Lemma);
                        MSA_equiv_index= Indexing.get_MSA_Index(MSA_equiv);
                        MSA_POS_LDC_index = Indexing.get_POS_Index(MSA_POS_LDC);
                        DIA_POS_LDC_index = Indexing.get_POS_Index(EGY_POS_LDC);

                        HashSet<Integer> MSA_indices= new HashSet<Integer>();
                        MSA_indices.add(MSA_lemma_index);
                        MSA_indices.add(MSA_equiv_index);

                        TharwaRow tharwaRow = new TharwaRow(EGY_indices, MSA_indices, En_equiv_indices, DIA_POS_LDC_index, MSA_POS_LDC_index);

                        if (!tharwaDic.containsKey(id)) {
                            tharwaDic.put(id, tharwaRow);
                            tharwaSize++;
                        } else if (tharwaDic.containsKey(id))
                            System.out.println("!!!ERROR!!!Trying to add the same ID to TharwaDic!");
                    }

                }else if (usePreComputedGoldTharwa==true)
                {
                    //just tharwa rows which are in the gold tharwa

                    if (goldTharwaIDs.contains(id))
                    {
                        //this id is in the list of gold tharwa ids
                        tharwaWriter.write(id.toString() + "\t" + EGY_words.toString() + '\t' + MSA_Lemma+","+MSA_equiv + '\t' + ENG_glosses.toString() + '\n');


                        Integer MSA_lemma_index = 0;
                        Integer MSA_equiv_index=0;
                        Integer MSA_POS_LDC_index = 0;
                        Integer DIA_POS_LDC_index = 0;

                        if (!Indexing.MSA_indexDic.containsKey(MSA_Lemma))
                            Indexing.update_MSA_Index_Dic(MSA_Lemma);
                        if (!Indexing.MSA_indexDic.containsKey(MSA_equiv))
                            Indexing.update_MSA_Index_Dic(MSA_equiv);
                        if (!MSA_POS_LDC.equals("") && !Indexing.POS_indexDic.containsKey(MSA_POS_LDC))
                            Indexing.update_POS_Index_Dic(MSA_POS_LDC);
                        if (!EGY_POS_LDC.equals("") && !Indexing.POS_indexDic.containsKey(EGY_POS_LDC))
                            Indexing.update_POS_Index_Dic(EGY_POS_LDC);

                        MSA_lemma_index = Indexing.get_MSA_Index(MSA_Lemma);
                        MSA_equiv_index= Indexing.get_MSA_Index(MSA_equiv);
                        MSA_POS_LDC_index = Indexing.get_POS_Index(MSA_POS_LDC);
                        DIA_POS_LDC_index = Indexing.get_POS_Index(EGY_POS_LDC);

                        HashSet<Integer> MSA_indices= new HashSet<Integer>();
                        MSA_indices.add(MSA_lemma_index);
                        MSA_indices.add(MSA_equiv_index);

                        TharwaRow tharwaRow = new TharwaRow(EGY_indices, MSA_indices, En_equiv_indices, DIA_POS_LDC_index, MSA_POS_LDC_index);

                        if (!tharwaDic.containsKey(id)) {
                            tharwaDic.put(id, tharwaRow);
                            tharwaSize++;
                        } else if (tharwaDic.containsKey(id))
                            System.out.println("!!!ERROR!!!Trying to add the same ID to TharwaDic!");
                    }
                }else
                {
                   //whole Tharwa
                    //we have at least one EGY word common with monolingual data
                    // wrtining into a text file
                    tharwaWriter.write(id.toString() + "\t" + EGY_words.toString() + '\t' + MSA_Lemma+","+MSA_equiv + '\t' + ENG_glosses.toString() + '\n');


                    Integer MSA_lemma_index = 0;
                    Integer MSA_equiv_index=0;
                    Integer MSA_POS_LDC_index = 0;
                    Integer DIA_POS_LDC_index = 0;

                    if (!Indexing.MSA_indexDic.containsKey(MSA_Lemma))
                        Indexing.update_MSA_Index_Dic(MSA_Lemma);
                    if (!Indexing.MSA_indexDic.containsKey(MSA_equiv))
                        Indexing.update_MSA_Index_Dic(MSA_equiv);
                    if (!MSA_POS_LDC.equals("") && !Indexing.POS_indexDic.containsKey(MSA_POS_LDC))
                        Indexing.update_POS_Index_Dic(MSA_POS_LDC);
                    if (!EGY_POS_LDC.equals("") && !Indexing.POS_indexDic.containsKey(EGY_POS_LDC))
                        Indexing.update_POS_Index_Dic(EGY_POS_LDC);

                    MSA_lemma_index = Indexing.get_MSA_Index(MSA_Lemma);
                    MSA_equiv_index= Indexing.get_MSA_Index(MSA_equiv);
                    MSA_POS_LDC_index = Indexing.get_POS_Index(MSA_POS_LDC);
                    DIA_POS_LDC_index = Indexing.get_POS_Index(EGY_POS_LDC);

                    HashSet<Integer> MSA_indices= new HashSet<Integer>();
                    MSA_indices.add(MSA_lemma_index);
                    MSA_indices.add(MSA_equiv_index);

                    TharwaRow tharwaRow = new TharwaRow(EGY_indices, MSA_indices, En_equiv_indices, DIA_POS_LDC_index, MSA_POS_LDC_index);

                    if (!tharwaDic.containsKey(id)) {
                        tharwaDic.put(id, tharwaRow);
                        tharwaSize++;
                    } else if (tharwaDic.containsKey(id))
                        System.out.println("!!!ERROR!!!Trying to add the same ID to TharwaDic!");
                }
            //}

            if(id %100==0)
                System.out.println(id);
        }

        for (String tharwaEGY: tharwaEGYWords)
            tharwaEGYWordsWriter.write(tharwaEGY.trim()+"@@pos@@"+EGY_POS_LDC+"\n");

        tharwaWriter.flush();
        tharwaWriter.close();
        tharwaReader.close();
        tharwaEGYWordsWriter.flush();
        tharwaEGYWordsWriter.close();

        System.out.println("ConvertTharwaToDic_IDKey finished");
        System.out.println("Numbe of Tharwa Entries "+ tharwaDic.size());
        System.out.println("***************************");
        return tharwaDic;
    }


    public static Set<Integer> generateRandIDs(int numbersNeeded, int max)
    {
        Random rng = new Random(); // Ideally just create one instance globally
        // Note: use LinkedHashSet to maintain insertion order
        Set<Integer> generated = new LinkedHashSet<Integer>();
        while (generated.size() < numbersNeeded)
        {
            Integer next = rng.nextInt(max) + 1;
            // As we're adding to a set, this will automatically do a containment check
            generated.add(next);
        }

        return generated;
    }


    public static HashSet<String> getEGYWords (String EGYFilePath) throws IOException
    {
        System.out.println("Getting list of words in the EGY data...");
        HashSet<String> EGYWords= new HashSet<String>();
        BufferedReader Ar_data_reader = new BufferedReader(new FileReader(EGYFilePath));
        String Ar_line_to_read = "";

        int counter=0;
        while ((Ar_line_to_read = Ar_data_reader.readLine()) != null)
        {
            counter++;
            String[] words = Ar_line_to_read.split(" ");
            for (String word:words)
            {

                if (word.contains("@@pos@@")) {
                    String ArabicLemma = word.split("@@pos@@")[0].trim();
                    String POS = word.split("@@pos@@")[1].trim();

                    ArabicLemma= Preprocessing.remove_last_vowel(ArabicLemma);
                    String clean_word= ArabicLemma+"@@pos@@"+POS;
                    EGYWords.add(clean_word.trim());
                }
                else
                    EGYWords.add(word.trim());

                /*
                Integer DIA_index;
                if (!Indexing.DIA_indexDic.containsKey(word))
                    Indexing.update_DIA_Index_Dic(word);

                DIA_index = Indexing.get_DIA_Index(word);
                */
            }
        }

        System.out.println("Number of EGY types in data: "+ EGYWords.size());
        return EGYWords;

    }


    public static HashSet<Integer> getListOfGoldTharwaEntries (String goldTharwaPath) throws IOException
    {
        BufferedReader tharwaReader= new BufferedReader(new FileReader(goldTharwaPath));
        String line2Read= "";
        HashSet<Integer> listOfGoldTharwaIDs= new HashSet<Integer>();

        while ((line2Read= tharwaReader.readLine())!=null)
        {
            line2Read= line2Read.trim();
            int id= Integer.parseInt(line2Read.split("\t")[0].trim());

            listOfGoldTharwaIDs.add(id);
        }

        return listOfGoldTharwaIDs;
    }


}
