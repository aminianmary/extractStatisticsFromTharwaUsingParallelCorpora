package ExtractStatsUsingParallelCorpora;
/**
 * User: Maryam Aminian
 * Date: 1/24/14
 * Time: 10:41 PM
 * This java class is created to extract matching statistics between Tharwa and existing Parallel corpora
 */
import utilities.Indexing;
import utilities.MSAEnTuple;
import utilities.TharwaRow;

import java.io.*;
import java.util.*;

public class ExtractStats {

    static HashMap<Integer, HashMap<Integer, ArrayList<Object>>> DA_sim_En_diff_MSA_sim_w_POS_found_rows = new HashMap<Integer, HashMap<Integer, ArrayList<Object>>>();
    static HashMap<Integer, HashMap<Integer, ArrayList<Set<Integer>>>> DA_sim_En_diff_MSA_sim_found_rows = new HashMap<Integer, HashMap<Integer, ArrayList<Set<Integer>>>>();

    //Stats
    static int DA_sim_En_sim_MSA_sim = 0;
    static int DA_sim_En_sim_MSA_diff = 0;
    static int DA_sim_En_diff_MSA_sim = 0;
    static int DA_sim_En_diff_MSA_diff = 0;

    static int DA_diff_En_sim_MSA_sim = 0;
    static int DA_diff_En_sim_MSA_diff = 0;
    static int DA_diff_En_diff_MSA_sim = 0;
    //static int DA_diff_En_diff_MSA_diff=0;

    static int DA_sim_En_sim_MSA_sim_w_POS = 0;
    static int DA_sim_En_sim_MSA_diff_w_POS = 0;
    static int DA_sim_En_diff_MSA_sim_w_POS = 0;
    static int DA_sim_En_diff_MSA_diff_w_POS = 0;

    static int DA_diff_En_sim_MSA_sim_w_POS = 0;
    static int DA_diff_En_sim_MSA_diff_w_POS = 0;
    static int DA_diff_En_diff_MSA_sim_w_POS = 0;
    //static int DA_diff_En_diff_MSA_diff_w_POS=0;

    static int transDicEntries_Dropped_Due_to_diff_POS = 0;


    public static void main(String[] args) throws IOException {
        /*
        args[0] = DIA_En_Alignment_Path
        args[1] = MSA_En_Alignment_Path
        args[2] = tharwaPath
        args[3] = DIA_En_EngDataPath
        args[4] = DIA_En_ArDataPath
        args[5] = MSA_En_EngDataPath
        args[6] = MSA_En_ArDataPath
        args[7] = Alef-Ya normalized
        args[8] = lemmatized (boolean)
        args[9] = diacritized (boolean)
        args[10]= consider transDic pos match
        args[11]= consider tharwa pos match
        args[12]= consider synonyms for En match
        args[13]= load alignment dic from file (false= create them from data)
        args[14]= consider expanded parallel data
        args[15]= use EGY matched Tharwa (true: just EGY matched Tharwa::false: the whole Tharwa)
        args[16]= use pre-computed gold tharwa (if this argument will be TRUE, args[15] MUST be FALSE)
        args[17]= expand EGY variants
        args[18]= expand tharwa EGY variants (false: expand transDic EGY variants)
        args[19]= expand using word clusters (true: word clusters, false: EGY synonyms)
        args[20]= number of clusters to expand EGY
        args[21]= path to the pre-computed gold tharwa
        args[22]= path to the cluster file (word cluster file path [OR] EGY_synonyms file path)
        args[23]= output Directory
         */

        if (args.length < 11)
            System.out.println("MISSED ARGUMENT");

        String DIA_En_Alignment_Path = args[0];
        String MSA_En_Alignment_Path = args[1];
        String tharwaPath = args[2];
        String outputDir = args[23];

        ////////////////

        String DIA_En_EngDataPath = args[3];
        String DIA_En_ArDataPath = args[4];

        String MSA_En_EngDataPath = args[5];
        String MSA_En_ArDataPath = args[6];

        String preComputedGoldTharwaPath= args[21];
        String clusterFilePath= args[22];
        String number_of_clusters= args[20];

        boolean isNormalized = Boolean.parseBoolean(args[7]);
        boolean isLemmatized = Boolean.parseBoolean(args[8]);
        boolean isDiacratized = Boolean.parseBoolean(args[9]);
        boolean considerTransDicPOSMatch = Boolean.parseBoolean(args[10]);
        boolean considerTharwaDicPOSMatch = Boolean.parseBoolean(args[11]);
        boolean considerSynonyms4EnMatch = Boolean.parseBoolean(args[12]);
        boolean loadAlignmentDics= Boolean.parseBoolean(args[13]);
        boolean loadExpandedDics= Boolean.parseBoolean(args[14]);
        boolean useEGYMatchedTharwa= Boolean.parseBoolean(args[15]);
        boolean usePreComputedGoldTharwa= Boolean.parseBoolean(args[16]);
        boolean expandEGY= Boolean.parseBoolean(args[17]);
        boolean expandTharwaEGY= Boolean.parseBoolean(args[18]);
        boolean expandTharwaEGY_using_word_clusters= Boolean.parseBoolean(args[19]);


        System.out.println("diacritized: " + isDiacratized);

        String normalized = "0";
        String lemmatized = "tok";
        String diacratized = "undiac";
        String consider_transDic_pos = "0";
        String consider_tharwa_pos = "0";
        String consider_synonyms = "0";
        String expanded_pt="0";
        String gold_tharwa="0";
        String expand_EGY="no_EGY_expan";
        String expans_EGY_with_word_clusters="with_synonyms";


        if (isNormalized == true)
            normalized = "AlefYaNorm";

        if (isLemmatized == true)
            lemmatized = "lem";

        if (isDiacratized == true)
            diacratized = "diac";

        if (considerTransDicPOSMatch == true)
            consider_transDic_pos = "transPOS";

        if (considerTharwaDicPOSMatch == true)
            consider_tharwa_pos = "tharwaPOS";

        if (considerSynonyms4EnMatch == true)
            consider_synonyms = "en_syn";

        if (loadExpandedDics==true)
            expanded_pt="expandedPT";

        if (useEGYMatchedTharwa==true && usePreComputedGoldTharwa==false)
             gold_tharwa="EGYTharwa";
        else if (useEGYMatchedTharwa==false && usePreComputedGoldTharwa==true)
            gold_tharwa="preComputedGold";

        if (expandEGY==true) {
            if (expandTharwaEGY==true)
                expand_EGY="tharwa_EGY_expan";
            else
                expand_EGY="trans_EGY_expan";
        }

        if (expandTharwaEGY_using_word_clusters==true)
            expans_EGY_with_word_clusters="with_clusters";



        String prefix = normalized + "_" + lemmatized + "_" + diacratized + "_" + consider_transDic_pos + "_" +
                consider_tharwa_pos + "_" + consider_synonyms+"_"+expanded_pt+"_"+ gold_tharwa+"_"+number_of_clusters+"Clusters"+
                "_"+expand_EGY+"_"+expans_EGY_with_word_clusters;

        //Creates TharwaDict from ExtractStats.Tharwa
        //HashMap<Integer, ArrayList<MSAEnTuple>> tharwaDic_EGYKey = Tharwa.ConvertTharwaToDic_EGYKey(tharwaPath, isNormalized, isDiacratized);
        //HashMap<Integer, TharwaRow> tharwaDic_IDKey = Tharwa.ConvertTharwaToDic_IDKey(tharwaPath,DIA_En_ArDataPath,
        //        outputDir,prefix,isNormalized, isDiacratized, useEGYMatchedTharwa, usePreComputedGoldTharwa,preComputedGoldTharwaPath);



        //Creates Alignmenent data dictionary for MSA-En and Egy-En separately
        ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>> MSA_AlignmentdDics=
                new ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>>();
        ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>> DA_AlignmentdDics=
                new ArrayList<HashMap<Integer, HashMap<Integer, HashSet<Integer>>>>();

        if (loadAlignmentDics== false)
        {
            MSA_AlignmentdDics = ParallelCorpora.buildMSA_EnAllignDataDic(MSA_En_ArDataPath, MSA_En_EngDataPath, MSA_En_Alignment_Path, outputDir, prefix);
            DA_AlignmentdDics = ParallelCorpora.buildDA_EnAlignDic(DIA_En_ArDataPath, DIA_En_EngDataPath, DIA_En_Alignment_Path, outputDir, prefix);

        }else if (loadAlignmentDics==true)
        {
            if (loadExpandedDics==false) {
                MSA_AlignmentdDics = ParallelCorpora.loadMSAAlignmentDic(outputDir + "/parallel_dic_MSA");
                DA_AlignmentdDics = ParallelCorpora.loadEGYAlignmentDic(outputDir + "/parallel_dic_EGY");
            }else
            {
                MSA_AlignmentdDics = ParallelCorpora.loadMSAAlignmentDic(outputDir + "/parallel_dic_MSA.expanded");
                DA_AlignmentdDics = ParallelCorpora.loadEGYAlignmentDic(outputDir + "/parallel_dic_EGY.expanded");
            }

        }

        /*
        HashMap<Integer, HashMap<Integer, HashSet<Integer>>> synonymDic= new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();

        System.out.println("considerSynonyms4EnMatch: "+ considerSynonyms4EnMatch);

        if (considerSynonyms4EnMatch==true)
            synonymDic = ParallelCorpora.create_synonym_dic(outputDir + "/transDic_pos_en", outputDir + "/transDic_pos_en.mapped.syn");

        HashMap<Integer, HashMap<Integer, HashSet<Integer>>> En_MSA_AlignmentDic =
                new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
        HashMap<Integer, HashMap<Integer, HashSet<Integer>>> MSA_En_AlignmentDic =
                new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();

        HashMap<Integer, HashMap<Integer, HashSet<Integer>>> En_DA_AlignmentDic =
                new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();
        HashMap<Integer, HashMap<Integer, HashSet<Integer>>> DA_En_AlignmentDic =
                new HashMap<Integer, HashMap<Integer, HashSet<Integer>>>();


        En_MSA_AlignmentDic = MSA_AlignmentdDics.get(0);
        MSA_En_AlignmentDic = MSA_AlignmentdDics.get(1);

        En_DA_AlignmentDic = DA_AlignmentdDics.get(0);
        DA_En_AlignmentDic = DA_AlignmentdDics.get(1);


        //TransDictCreator transDic= new TransDictCreator(outputDir+"/parallel_dic_MSA", outputDir+"/parallel_dic_EGY", outputDir+"/transDict");


        //extractStats(MSA_AlignmentdDics, DA_AlignmentdDics, tharwaDic_EGYKey, outputDir+"/"+prefix, includePOS);

       evaluateEGYMatch(MSA_En_AlignmentDic, En_DA_AlignmentDic, tharwaDic_IDKey, outputDir, considerTransDicPOSMatch, considerTharwaDicPOSMatch, expandEGY, expandTharwaEGY, expandTharwaEGY_using_word_clusters , prefix, clusterFilePath);

       evaluateEnMatch(MSA_En_AlignmentDic, DA_En_AlignmentDic, tharwaDic_IDKey, synonymDic, outputDir, considerTransDicPOSMatch, considerTharwaDicPOSMatch, considerSynonyms4EnMatch, prefix);


        */
        /*
        System.out.println("DA_sim_En_sim_MSA_sim "+DA_sim_En_sim_MSA_sim_w_POS);
        System.out.println("DA_sim_En_sim_MSA_diff "+DA_sim_En_sim_MSA_diff_w_POS);
        System.out.println("DA_sim_En_diff_MSA_sim "+DA_sim_En_diff_MSA_sim_w_POS);
        System.out.println("DA_sim_En_diff_MSA_diff "+DA_sim_En_diff_MSA_diff_w_POS);

        System.out.println("DA_diff_En_sim_MSA_sim "+DA_diff_En_sim_MSA_sim_w_POS);
        System.out.println("DA_siff_En_sim_MSA_diff "+DA_diff_En_sim_MSA_diff_w_POS);
        System.out.println("DA_diff_En_diff_MSA_sim "+DA_diff_En_diff_MSA_sim_w_POS);

        System.out.println("DA_sim_En_sim_MSA_sim_w_POS"+DA_sim_En_sim_MSA_sim_w_POS);
        System.out.println("DA_sim_En_sim_MSA_diff_w_POS "+DA_sim_En_sim_MSA_diff_w_POS);
        System.out.println("DA_sim_En_diff_MSA_sim_w_POS"+DA_sim_En_diff_MSA_sim_w_POS);
        System.out.println("DA_sim_En_diff_MSA_diff_w_POS "+DA_sim_En_diff_MSA_diff_w_POS);

        System.out.println("DA_diff_En_sim_MSA_sim_w_POS "+DA_diff_En_sim_MSA_sim_w_POS);
        System.out.println("DA_siff_En_sim_MSA_diff_w_POS "+DA_diff_En_sim_MSA_diff_w_POS);
        System.out.println("DA_diff_En_diff_MSA_sim_w_POS "+DA_diff_En_diff_MSA_sim_w_POS);
        */

        System.out.println("ExtractStats.Tharwa Size  " + Tharwa.tharwaSize);

        Indexing.write_index_dic(Indexing.En_indexDic, outputDir + "/En_index_dic");
        Indexing.write_index_dic(Indexing.MSA_indexDic, outputDir + "/MSA_index_dic");
        Indexing.write_index_dic(Indexing.DIA_indexDic, outputDir + "/DIA_index_dic");
        Indexing.write_index_dic(Indexing.POS_indexDic, outputDir + "/POS_index_dic");
    }


    /**
     * This function extracts matching statistics from ExtractStats.Tharwa and Parallel Corpora
     *
     * @param MSA_AlignmentDics
     * @param DA_AlignmentDics
     * @param tharwaDic
     * @param prefix
     * @param includePOSTag
     * @throws IOException
     */
    public static void extractStats(Object[] MSA_AlignmentDics, Object[] DA_AlignmentDics,
                                    HashMap<Integer, ArrayList<MSAEnTuple>> tharwaDic, String prefix, boolean includePOSTag) throws IOException {

        System.out.println("Extract Stats From Alignment Dics Started...");
        HashMap<Integer, HashMap<Integer, Integer>> En_MSA_AlignmentDic = new HashMap<Integer, HashMap<Integer, Integer>>();
        HashMap<Integer, HashMap<Integer, Integer>> En_DA_AlignmentDic = new HashMap<Integer, HashMap<Integer, Integer>>();

        HashMap<Integer, HashMap<Integer, Integer>> MSA_En_AlignmentDic = new HashMap<Integer, HashMap<Integer, Integer>>();
        HashMap<Integer, HashMap<Integer, Integer>> DA_En_AlignmentDic = new HashMap<Integer, HashMap<Integer, Integer>>();


        En_MSA_AlignmentDic = (HashMap<Integer, HashMap<Integer, Integer>>) MSA_AlignmentDics[0];
        MSA_En_AlignmentDic = (HashMap<Integer, HashMap<Integer, Integer>>) MSA_AlignmentDics[1];

        En_DA_AlignmentDic = (HashMap<Integer, HashMap<Integer, Integer>>) DA_AlignmentDics[0];
        DA_En_AlignmentDic = (HashMap<Integer, HashMap<Integer, Integer>>) DA_AlignmentDics[1];


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //output files containing extracted triplets
        //DA sim
        BufferedWriter DA_sim_En_sim_MSA_sim_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_sim-MSA_sim", true));
        DA_sim_En_sim_MSA_sim_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_sim_MSA_diff_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_sim-MSA_diff", true));
        DA_sim_En_sim_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_sim_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_diff-MSA_sim", true));
        DA_sim_En_diff_MSA_sim_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_sim_1Col_Format_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_diff-MSA_sim_1Col_format", true));
        DA_sim_En_diff_MSA_sim_1Col_Format_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_diff_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_diff-MSA_diff", true));
        DA_sim_En_diff_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        //DA diff
        BufferedWriter DA_diff_En_sim_MSA_sim_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_diff-En_sim-MSA_sim", true));
        DA_diff_En_sim_MSA_sim_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_sim_MSA_diff_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_diff-En_sim-MSA_diff", true));
        DA_diff_En_sim_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_diff_MSA_sim_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_diff-En_diff-MSA_sim", true));
        DA_diff_En_diff_MSA_sim_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_diff_MSA_diff_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_diff-En_diff-MSA_diff", true));
        DA_diff_En_diff_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        ///////////////////////////// INCLUDE POS TAG in CREATING TRANSDICT ////////////////////////////////////////////////

        //DA sim
        BufferedWriter DA_sim_En_sim_MSA_sim_w_POS_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_sim-MSA_sim_w_POS", true));
        DA_sim_En_sim_MSA_sim_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_sim_MSA_diff_w_POS_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_sim-MSA_diff_w_POS", true));
        DA_sim_En_sim_MSA_diff_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_sim_w_POS_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_diff-MSA_sim_w_POS", true));
        DA_sim_En_diff_MSA_sim_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_diff-MSA_sim_w_POS_1Col_format", true));
        DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_sim_En_diff_MSA_diff_w_POS_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_sim-En_diff-MSA_diff_w_POS", true));
        DA_sim_En_diff_MSA_diff_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        //DA diff
        BufferedWriter DA_diff_En_sim_MSA_sim_w_POS_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_diff-En_sim-MSA_sim_w_POS", true));
        DA_diff_En_sim_MSA_sim_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_sim_MSA_diff_w_POS_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_diff-En_sim-MSA_diff_w_POS", true));
        DA_diff_En_sim_MSA_diff_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");

        BufferedWriter DA_diff_En_diff_MSA_sim_w_POS_Writer = new BufferedWriter(new FileWriter(prefix + "_DA_diff-En_diff-MSA_sim_w_POS", true));
        DA_diff_En_diff_MSA_sim_w_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");
        //BufferedWriter DA_diff_En_diff_MSA_diff_w_POS_Writer= new BufferedWriter(new FileWriter(prefix+"_DA_diff-En_diff-MSA_diff_w_POS",true));
        //DA_diff_En_diff_MSA_diff_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");


        BufferedWriter transDicEntries_Dropped_Due_to_diff_POS_Writer = new BufferedWriter(new FileWriter(prefix + "_transDicEntries_Dropped_Due_to_diff_POS", true));
        transDicEntries_Dropped_Due_to_diff_POS_Writer.write("ids\tCODA\tEn Gloss\tMSA\n");
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        int counter = 0;
        //Iterates on all ExtractStats.Tharwa entries to extract <<PARTIAL MATCHES>>
        for (int TharwaEGY : tharwaDic.keySet()) {
            //System.out.println("ExtractStats.Tharwa EGY: "+get_DIA_Word(TharwaEGY));
            counter++;

            if (counter % 100 == 0)
                System.out.println("Number of Processed Entries: " + counter);


            //iterates on all MSA-En assigned to an EGY lemma
            for (MSAEnTuple MSAEnT : tharwaDic.get(TharwaEGY)) {
                int TharwaEn = MSAEnT.getEn();
                int TharwaMSA = MSAEnT.getMSA();
                int TharwaMSAPOS = MSAEnT.getMSA_POS_LDC();
                int TharwaDIAPOS = MSAEnT.getEGY_POS_LDC();
                Set<Integer> TharwaIds = new HashSet<Integer>(MSAEnT.getIds());

                //System.out.println("ExtractStats.Tharwa MSA: " + get_MSA_Word(TharwaMSA));
                //System.out.println("ExtractStats.Tharwa En: "+get_En_Word(TharwaEn));

                //Checks DA_En_AlignmentDic
                //Found EGY word in Transdict--> DA_sim
                if (DA_En_AlignmentDic.containsKey(TharwaEGY)) {

                    //System.out.println("EGY found in EGY-En data");
                    //fulfills the similarity condition on English
                    if (DA_En_AlignmentDic.get(TharwaEGY).containsKey(TharwaEn) && En_MSA_AlignmentDic.containsKey(TharwaEn)) {
                        //System.out.println("Found common En between EGY-En, MSA-En and ExtractStats.Tharwa");
                        //DA_sim, En_sim
                        if (En_MSA_AlignmentDic.get(TharwaEn).containsKey(TharwaMSA)) {
                            //include POS tag means that MSA_POS, DA_POS should match with the POS tag of DIA column in Tharwa
                            if (includePOSTag == true) {
                                int DA_POS = DA_En_AlignmentDic.get(TharwaEGY).get(TharwaEn);
                                int MSA_POS = En_MSA_AlignmentDic.get(TharwaEn).get(TharwaMSA);

                                //System.out.println("DA POS: " +get_POS(DA_POS));
                                //System.out.println("MSA POS: "+ get_POS(MSA_POS));

                                if (DA_POS == MSA_POS && DA_POS == TharwaDIAPOS) {
                                    //DA_sim, En_sim, MSA_sim w POS
                                    DA_sim_En_sim_MSA_sim_w_POS++;
                                    DA_sim_En_sim_MSA_sim_w_POS_Writer.write(MSAEnT.getIds() + "\t" +
                                            Indexing.get_DIA_Word(TharwaEGY) + "\t" + Indexing.get_En_Word(TharwaEn) + "\t" + Indexing.get_MSA_Word(TharwaMSA) + "\n");

                                } else {
                                    //if DA_POS is not equal to MSA_POS, then this triplet is not included in TransDict at all, hence does not count on the stats
                                    transDicEntries_Dropped_Due_to_diff_POS++;
                                    transDicEntries_Dropped_Due_to_diff_POS_Writer.write(MSAEnT.getIds() + "\t" +
                                            Indexing.get_DIA_Word(TharwaEGY) + "\t" + Indexing.get_En_Word(TharwaEn) + "\t" + Indexing.get_MSA_Word(TharwaMSA) + "\n");

                                }

                            } else {
                                //DA_sim, En_sim, MSA_sim
                                DA_sim_En_sim_MSA_sim++;
                                DA_sim_En_sim_MSA_sim_Writer.write(MSAEnT.getIds() + "\t" +
                                        Indexing.get_DIA_Word(TharwaEGY) + "\t" + Indexing.get_En_Word(TharwaEn) + "\t" + Indexing.get_MSA_Word(TharwaMSA) + "\n");

                            }

                        }
                        //MSA is not similar to ExtractStats.Tharwa
                        else {

                            //System.out.println("MSA is not found in ExtractStats.Tharwa");
                            if (includePOSTag == true) {
                                for (int transDicMSA : En_MSA_AlignmentDic.get(TharwaEn).keySet()) {
                                    int DA_POS = DA_En_AlignmentDic.get(TharwaEGY).get(TharwaEn);
                                    int MSA_POS = En_MSA_AlignmentDic.get(TharwaEn).get(transDicMSA);

                                    //System.out.println("DA POS: " +get_POS(DA_POS));
                                    //System.out.println("MSA POS: "+get_POS(MSA_POS));

                                    if (DA_POS == MSA_POS) {
                                        //qualifies for being an entry in TransDict
                                        DA_sim_En_sim_MSA_diff_w_POS++;
                                        DA_sim_En_sim_MSA_diff_w_POS_Writer.write(MSAEnT.getIds() + "\t" +
                                                Indexing.get_DIA_Word(TharwaEGY) + "\t" + Indexing.get_En_Word(TharwaEn) + "\t" + Indexing.get_MSA_Word(transDicMSA) + "\n");
                                        DA_sim_En_sim_MSA_diff_w_POS_Writer.write("\n");
                                    }
                                }
                            } else {
                                //DA_sim, En_sim, MSA_diff
                                DA_sim_En_sim_MSA_diff++;
                                for (int transDicMSA : En_MSA_AlignmentDic.get(TharwaEn).keySet()) {
                                    DA_sim_En_sim_MSA_diff_Writer.write(MSAEnT.getIds() + "\t" +
                                            Indexing.get_DIA_Word(TharwaEGY) + "\t" + Indexing.get_En_Word(TharwaEn) + "\t" + Indexing.get_MSA_Word(transDicMSA) + "\n");
                                }
                                DA_sim_En_sim_MSA_diff_Writer.write("\n");


                            }

                        }
                    } else {
                        //System.out.println("Common Eng is not found");
                        //DA_sim, Eng_diff
                        if (MSA_En_AlignmentDic.containsKey(TharwaMSA)) {
                            // System.out.println("Found common MSA");
                            Vector<Integer> EnAlignedToDA = new Vector<Integer>(DA_En_AlignmentDic.get(TharwaEGY).keySet());
                            Vector<Integer> EnAlignedToMSA = new Vector<Integer>(MSA_En_AlignmentDic.get(TharwaMSA).keySet());

                            EnAlignedToDA.retainAll(EnAlignedToMSA);

                            //There exists at least one Eng lemma commonly aligned to both MSA and DA word.
                            if (EnAlignedToDA.size() > 0) {
                                for (int transDicEn : EnAlignedToDA) {
                                    if (includePOSTag == true) {
                                        int DA_POS = DA_En_AlignmentDic.get(TharwaEGY).get(transDicEn);
                                        int MSA_POS = MSA_En_AlignmentDic.get(TharwaMSA).get(transDicEn);

                                        if (DA_POS == MSA_POS) {
                                            DA_sim_En_diff_MSA_sim_w_POS++;
                                            DA_sim_En_diff_MSA_sim_w_POS_Writer.write(MSAEnT.getIds() + "\t" +
                                                    Indexing.get_DIA_Word(TharwaEGY) + "\t" + Indexing.get_En_Word(transDicEn) + "\t" + Indexing.get_MSA_Word(TharwaMSA) + "\n");
                                            DA_sim_En_diff_MSA_sim_w_POS_Writer.write("\n");

                                            //To avoid replicating rows
                                            if (!DA_sim_En_diff_MSA_sim_w_POS_found_rows.containsKey(TharwaEGY)) {
                                                Set<Integer> idArrayList = new HashSet<Integer>();
                                                idArrayList.addAll(TharwaIds);

                                                ArrayList<Integer> EnArrayList = new ArrayList<Integer>();
                                                EnArrayList.add(transDicEn);

                                                ArrayList<Integer> POSArrayList = new ArrayList<Integer>();
                                                POSArrayList.add(MSA_POS);

                                                ArrayList<Object> tempArrayList = new ArrayList<Object>();

                                                tempArrayList.add(EnArrayList);
                                                tempArrayList.add(idArrayList);
                                                tempArrayList.add(POSArrayList);

                                                HashMap<Integer, ArrayList<Object>> MSAHashMap = new HashMap<Integer, ArrayList<Object>>();
                                                MSAHashMap.put(TharwaMSA, tempArrayList);

                                                DA_sim_En_diff_MSA_sim_w_POS_found_rows.put(TharwaEGY, MSAHashMap);

                                            } else {
                                                if (!DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).containsKey(TharwaMSA)) {
                                                    Set<Integer> idArrayList = new HashSet<Integer>();
                                                    idArrayList.addAll(TharwaIds);

                                                    ArrayList<Integer> EnArrayList = new ArrayList<Integer>();
                                                    EnArrayList.add(transDicEn);

                                                    ArrayList<Integer> POSArrayList = new ArrayList<Integer>();
                                                    POSArrayList.add(MSA_POS);

                                                    ArrayList<Object> tempArrayList = new ArrayList<Object>();
                                                    tempArrayList.add(EnArrayList);
                                                    tempArrayList.add(idArrayList);
                                                    tempArrayList.add(POSArrayList);


                                                    DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).put(TharwaMSA, tempArrayList);
                                                } else {
                                                    if (!((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(0)).contains(transDicEn)) {

                                                        ((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(0)).add(transDicEn);
                                                        ((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(2)).add(MSA_POS);
                                                    } else {

                                                        System.out.println("Current IDs: " + DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(1));
                                                        System.out.println("New IDs: " + TharwaIds);
                                                        Set<Integer> Ids2Add = TharwaIds;
                                                        Ids2Add.removeAll((Set<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(1));

                                                        System.out.println("Set of Ids to add: " + Ids2Add);

                                                        if (Ids2Add.size() > 0)
                                                            ((Set<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(TharwaEGY).get(TharwaMSA).get(1)).addAll(Ids2Add);
                                                    }

                                                }
                                            }
                                        }

                                    } else {
                                        DA_sim_En_diff_MSA_sim++;
                                        DA_sim_En_diff_MSA_sim_Writer.write(MSAEnT.getIds() + "\t" +
                                                Indexing.get_DIA_Word(TharwaEGY) + "\t" + Indexing.get_En_Word(transDicEn) + "\t" + Indexing.get_MSA_Word(TharwaMSA) + "\n");
                                        DA_sim_En_diff_MSA_sim_Writer.write("\n");


                                        //To avoid replicating rows
                                        if (!DA_sim_En_diff_MSA_sim_found_rows.containsKey(TharwaEGY)) {
                                            Set<Integer> idArrayList = new HashSet<Integer>();
                                            idArrayList.addAll(TharwaIds);

                                            Set<Integer> EnArrayList = new HashSet<Integer>();
                                            EnArrayList.add(transDicEn);

                                            ArrayList<Set<Integer>> tempArrayList = new ArrayList<Set<Integer>>();
                                            tempArrayList.add(EnArrayList);
                                            tempArrayList.add(idArrayList);

                                            HashMap<Integer, ArrayList<Set<Integer>>> MSAHashMap = new HashMap<Integer, ArrayList<Set<Integer>>>();
                                            MSAHashMap.put(TharwaMSA, tempArrayList);

                                            DA_sim_En_diff_MSA_sim_found_rows.put(TharwaEGY, MSAHashMap);

                                        } else {
                                            if (!DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).containsKey(TharwaMSA)) {
                                                Set<Integer> idArrayList = new HashSet<Integer>();
                                                idArrayList.addAll(TharwaIds);

                                                Set<Integer> EnArrayList = new HashSet<Integer>();
                                                EnArrayList.add(transDicEn);

                                                ArrayList<Set<Integer>> tempArrayList = new ArrayList<Set<Integer>>();
                                                tempArrayList.add(EnArrayList);
                                                tempArrayList.add(idArrayList);

                                                DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).put(TharwaMSA, tempArrayList);
                                            } else {
                                                if (!DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).get(TharwaMSA).get(0).contains(transDicEn))
                                                    DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).get(TharwaMSA).get(0).add(transDicEn);
                                                else {
                                                    Set<Integer> Ids2Add = TharwaIds;
                                                    Ids2Add.removeAll(DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).get(TharwaMSA).get(1));


                                                    if (Ids2Add.size() > 0)
                                                        DA_sim_En_diff_MSA_sim_found_rows.get(TharwaEGY).get(TharwaMSA).get(1).addAll(Ids2Add);
                                                }


                                            }

                                        }
                                    }

                                }
                            }
                        } else {
                            //System.out.println("Common MSA is not found");
                            //DA_sim, Eng_diff, MSA_diff

                            for (int transDicEn : DA_En_AlignmentDic.get(TharwaEGY).keySet()) {
                                if (En_MSA_AlignmentDic.containsKey(transDicEn)) {
                                    for (int transDicMSA : En_MSA_AlignmentDic.get(transDicEn).keySet()) {
                                        if (includePOSTag == true) {
                                            int DA_POS = DA_En_AlignmentDic.get(TharwaEGY).get(transDicEn);
                                            int MSA_POS = En_MSA_AlignmentDic.get(transDicEn).get(transDicMSA);

                                            if (DA_POS == MSA_POS) {
                                                DA_sim_En_diff_MSA_diff_w_POS++;
                                                DA_sim_En_diff_MSA_diff_w_POS_Writer.write(MSAEnT.getIds() + "\t" +
                                                        Indexing.get_DIA_Word(TharwaEGY) + "\t" + Indexing.get_En_Word(transDicEn) + "\t" + Indexing.get_MSA_Word(transDicMSA) + "\n");
                                                DA_sim_En_diff_MSA_diff_w_POS_Writer.write("\n");
                                            }

                                        } else {
                                            DA_sim_En_diff_MSA_diff++;
                                            DA_sim_En_diff_MSA_diff_Writer.write(MSAEnT.getIds() + "\t" +
                                                    Indexing.get_DIA_Word(TharwaEGY) + "\t" + Indexing.get_En_Word(transDicEn) + "\t" + Indexing.get_MSA_Word(transDicMSA) + "\n");
                                            DA_sim_En_diff_MSA_diff_Writer.write("\n");
                                        }
                                    }


                                }
                            }

                        }
                    }

                }


                ////Extract Triples from TransDic which have not DA entry matched to TharwaDic
                //System.out.println("Writing DA diff");

                //check if MSA matches to TransDict
                if (MSA_En_AlignmentDic.containsKey(TharwaMSA)) {
                    //System.out.println("Common MSA");
                    //DA_diff_MSA_sim

                    if (MSA_En_AlignmentDic.get(TharwaMSA).containsKey(TharwaEn) && En_DA_AlignmentDic.containsKey(TharwaEn)) {
                        // System.out.println("Common En");
                        //DA_diff_En_sim_MSA_sim
                        for (int transDicDA : En_DA_AlignmentDic.get(TharwaEn).keySet()) {
                            if (includePOSTag) {
                                int DA_POS = En_DA_AlignmentDic.get(TharwaEn).get(transDicDA);
                                int MSA_POS = MSA_En_AlignmentDic.get(TharwaMSA).get(TharwaEn);

                                if (DA_POS == MSA_POS) {
                                    DA_diff_En_sim_MSA_sim_w_POS++;
                                    DA_diff_En_sim_MSA_sim_w_POS_Writer.write(MSAEnT.getIds() + "\t"
                                            + Indexing.get_DIA_Word(transDicDA) + "\t" + Indexing.get_En_Word(TharwaEn) + "\t" + Indexing.get_MSA_Word(TharwaMSA) + "\n\n");
                                }

                            } else {
                                DA_diff_En_sim_MSA_sim++;
                                DA_diff_En_sim_MSA_sim_Writer.write(MSAEnT.getIds() + "\t" +
                                        Indexing.get_DIA_Word(transDicDA) + "\t" + Indexing.get_En_Word(TharwaEn) + "\t" + Indexing.get_MSA_Word(TharwaMSA) + "\n\n");
                            }
                        }
                    } else {
                        //System.out.println("Common En is not found");
                        //DA_diff_MSA_sim_En_diff
                        for (int transDicEn : MSA_En_AlignmentDic.get(TharwaMSA).keySet()) {
                            if (En_DA_AlignmentDic.containsKey(transDicEn)) {
                                for (int transDicDA : En_DA_AlignmentDic.get(transDicEn).keySet()) {
                                    if (includePOSTag == true) {
                                        int DA_POS = En_DA_AlignmentDic.get(transDicEn).get(transDicDA);
                                        int MSA_POS = MSA_En_AlignmentDic.get(TharwaMSA).get(transDicEn);

                                        if (DA_POS == MSA_POS) {
                                            DA_diff_En_diff_MSA_sim_w_POS++;
                                            DA_diff_En_diff_MSA_sim_w_POS_Writer.write(MSAEnT.getIds() + "\t" +
                                                    Indexing.get_DIA_Word(transDicDA) + "\t" + Indexing.get_En_Word(transDicEn) + "\t" + Indexing.get_MSA_Word(TharwaMSA) + "\n\n");
                                        }

                                    } else {
                                        DA_diff_En_diff_MSA_sim++;
                                        DA_diff_En_diff_MSA_sim_Writer.write(MSAEnT.getIds() + "\t" +
                                                Indexing.get_DIA_Word(transDicDA) + "\t" + Indexing.get_En_Word(transDicEn) + "\t" + Indexing.get_MSA_Word(TharwaMSA) + "\n\n");
                                    }
                                }

                            }
                        }

                    }

                } else {

                    // System.out.println("Common MSA is not found");
                    //DA_diff_MSA_diff

                    if (En_DA_AlignmentDic.containsKey(TharwaEn) && En_MSA_AlignmentDic.containsKey(TharwaEn)) {
                        //DA_diff_MSA_diff_En_sim
                        for (int transDicMSA : En_MSA_AlignmentDic.get(TharwaEn).keySet()) {
                            for (int transDicDA : En_DA_AlignmentDic.get(TharwaEn).keySet()) {
                                if (includePOSTag == true) {
                                    int DA_POS = En_DA_AlignmentDic.get(TharwaEn).get(transDicDA);
                                    int MSA_POS = En_MSA_AlignmentDic.get(TharwaEn).get(transDicMSA);

                                    if (DA_POS == MSA_POS) {
                                        DA_diff_En_sim_MSA_diff_w_POS++;
                                        DA_diff_En_sim_MSA_diff_w_POS_Writer.write(MSAEnT.getIds() + "\t" +
                                                Indexing.get_DIA_Word(transDicDA) + "\t" + Indexing.get_En_Word(TharwaEn) + "\t" + Indexing.get_MSA_Word(transDicMSA) + "\n\n");
                                    }

                                } else {
                                    DA_diff_En_sim_MSA_diff++;
                                    DA_diff_En_sim_MSA_diff_Writer.write(MSAEnT.getIds() + "\t" +
                                            Indexing.get_DIA_Word(transDicDA) + "\t" + Indexing.get_En_Word(TharwaEn) + "\t" + Indexing.get_MSA_Word(transDicMSA) + "\n\n");
                                }
                            }
                        }
                    } else {
                        //DA_diff_MSA_diff_En_diff
                        //This output does not seem to be helpful and there is not any feasible way yo get it

                    }

                }

            }
        }

        //write down the 1-Col format with POS
        for (int egy : DA_sim_En_diff_MSA_sim_w_POS_found_rows.keySet()) {
            for (int msa : DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).keySet()) {
                DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.write(Indexing.get_DIA_Word(egy) + "\t" + Indexing.get_MSA_Word(msa) + "\t");
                for (int i = 0; i < ((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).get(msa).get(0)).size(); i++) {
                    DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.write(Indexing.get_En_Word(((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).get(msa).get(0)).get(i)) + "/" +
                            Indexing.get_POS(((ArrayList<Integer>) DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).get(msa).get(2)).get(i)) + ",");
                }
                DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.write("\t" + DA_sim_En_diff_MSA_sim_w_POS_found_rows.get(egy).get(msa).get(1) + "\n");
            }
        }

        //write down the 1-Col format
        for (int egy : DA_sim_En_diff_MSA_sim_found_rows.keySet()) {
            for (int msa : DA_sim_En_diff_MSA_sim_found_rows.get(egy).keySet()) {
                DA_sim_En_diff_MSA_sim_1Col_Format_Writer.write(Indexing.get_DIA_Word(egy) + "\t" + Indexing.get_MSA_Word(msa) + "\t");
                for (int en : DA_sim_En_diff_MSA_sim_found_rows.get(egy).get(msa).get(0)) {
                    DA_sim_En_diff_MSA_sim_1Col_Format_Writer.write(Indexing.get_En_Word(en) + ",");
                }
                DA_sim_En_diff_MSA_sim_1Col_Format_Writer.write("\t" + DA_sim_En_diff_MSA_sim_found_rows.get(egy).get(msa).get(1) + "\n");
            }
        }

        DA_sim_En_sim_MSA_sim_Writer.flush();
        DA_sim_En_sim_MSA_sim_Writer.close();
        DA_sim_En_sim_MSA_sim_Writer.close();

        DA_sim_En_diff_MSA_diff_Writer.flush();
        DA_sim_En_diff_MSA_diff_Writer.close();

        DA_sim_En_sim_MSA_diff_Writer.flush();
        DA_sim_En_sim_MSA_diff_Writer.close();

        DA_sim_En_diff_MSA_sim_Writer.flush();
        DA_sim_En_diff_MSA_sim_Writer.close();

        /////////////////////////////////////
        DA_diff_En_sim_MSA_sim_Writer.flush();
        DA_diff_En_sim_MSA_sim_Writer.close();

        DA_diff_En_diff_MSA_diff_Writer.flush();
        DA_diff_En_diff_MSA_diff_Writer.close();

        DA_diff_En_sim_MSA_diff_Writer.flush();
        DA_diff_En_sim_MSA_diff_Writer.close();

        DA_diff_En_diff_MSA_sim_Writer.flush();
        DA_diff_En_diff_MSA_sim_Writer.close();

        /////////////////////////////////////
        DA_sim_En_sim_MSA_sim_w_POS_Writer.flush();
        DA_sim_En_sim_MSA_sim_w_POS_Writer.close();

        DA_sim_En_diff_MSA_diff_w_POS_Writer.flush();
        DA_sim_En_diff_MSA_diff_w_POS_Writer.close();

        DA_sim_En_sim_MSA_diff_w_POS_Writer.flush();
        DA_sim_En_sim_MSA_diff_w_POS_Writer.close();

        DA_sim_En_diff_MSA_sim_w_POS_Writer.flush();
        DA_sim_En_diff_MSA_sim_w_POS_Writer.close();

        /////////////////////////////////////
        DA_diff_En_sim_MSA_sim_w_POS_Writer.flush();
        DA_diff_En_sim_MSA_sim_w_POS_Writer.close();

        //DA_diff_En_diff_MSA_diff_w_POS_Writer.flush();
        //DA_diff_En_diff_MSA_diff_w_POS_Writer.close();

        DA_diff_En_sim_MSA_diff_w_POS_Writer.flush();
        DA_diff_En_sim_MSA_diff_w_POS_Writer.close();

        DA_diff_En_diff_MSA_sim_w_POS_Writer.flush();
        DA_diff_En_diff_MSA_sim_w_POS_Writer.close();

        DA_sim_En_diff_MSA_sim_1Col_Format_Writer.flush();
        DA_sim_En_diff_MSA_sim_1Col_Format_Writer.close();

        DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.flush();
        DA_sim_En_diff_MSA_sim_w_POS_1Col_Format_Writer.close();

        transDicEntries_Dropped_Due_to_diff_POS_Writer.flush();
        transDicEntries_Dropped_Due_to_diff_POS_Writer.close();

        System.out.println("Extract Stats Finished!\n");
    }


    public static Integer evaluateEGYMatch(HashMap<Integer, HashMap<Integer, HashSet<Integer>>> MSA_En_AlignmentDic,
                                           HashMap<Integer, HashMap<Integer, HashSet<Integer>>> En_DA_AlignmentDic,
                                           HashMap<Integer, TharwaRow> tharwaDic, String outputDir,
                                           boolean consider_transDic_pos_match,
                                           boolean consider_thrawa_pos_match,
                                           boolean expand_EGY,
                                           boolean expand_tharwa_EGY,
                                           boolean expand_tharwa_EGY_with_word_clusters,
                                           String prefix,
                                           String clusterFilePath) throws IOException {

        BufferedWriter MSA_En_not_found_in_transDic = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir + "/" + prefix + "_MSA_En_not_found_in_transDic"), "UTF-8"));
        BufferedWriter MSA_En_found_in_transDic_pos_not_matched = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir + "/" + prefix + "_MSA_En_found_in_transDic_tharwa_pos_not_matched"), "UTF-8"));
        BufferedWriter MSA_En_found_in_transDic_pos_matched_EGY_not_matched = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir + "/" + prefix + "_MSA_En_found_in_transDic_tharwa_pos_matched_EGY_not_matched"), "UTF-8"));

        BufferedWriter MSA_not_found_in_MSA_En_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir + "/" + prefix + "_MSA_not_found_in_MSA_En"), "UTF-8"));
        BufferedWriter En_not_found_in_MSA_En_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir + "/" + prefix + "_En_not_found_in_MSA_En"), "UTF-8"));
        BufferedWriter En_not_found_in_En_DA_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir + "/" + prefix + "_En_not_found_in_En_DA"), "UTF-8"));
        BufferedWriter MSA_En_found_transDic_pos_not_matched_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir + "/" + prefix + "_MSA_En_found_transDic_pos_not_matched"), "UTF-8"));


        BufferedWriter matchedEntriesWriter = new BufferedWriter(new FileWriter(outputDir + "/" + prefix + "_EGY_match"));
        System.out.println("Evaluation EGY Match Started...");


        HashMap<Integer, Integer> word_cluster_map= new HashMap<Integer, Integer>();
        HashMap<Integer, HashSet<Integer>> cluster_words_map= new HashMap<Integer, HashSet<Integer>>();
        HashMap<Integer, HashSet<Integer>> EGY_synonym_map= new HashMap<Integer, HashSet<Integer>>();

        if (expand_EGY==true)
        {

            if (expand_tharwa_EGY_with_word_clusters==true) {
                Object[] clusterMaps = WordClusters.getEGYClusters(clusterFilePath);
                word_cluster_map = (HashMap<Integer, Integer>) clusterMaps[0];
                cluster_words_map = (HashMap<Integer, HashSet<Integer>>) clusterMaps[1];
            }else
            {
                double similarity_threshold= 0.5;
                EGY_synonym_map= WordClusters.getEGYSynonyms(clusterFilePath,0.5);
            }
        }

        HashMap<Integer, Integer> EnEquivs_made_match = new HashMap<Integer, Integer>();
        int num_matched_rows_EGY = 0;

        for (int tharwaID : tharwaDic.keySet()) {

            //matchedEntriesWriter.write(tharwaID + "\t");
            System.out.println("Tharwa Row " + tharwaID);

            HashMap<Integer, HashSet<Integer>> EGY_set_4_all_tharwa_EnEquivs = new HashMap<Integer, HashSet<Integer>>();
            HashSet<Integer> EGY_set_overal= new HashSet<Integer>();
            HashMap<Integer, ArrayList<Boolean>> match_status_4_all_tharwa_EnEquivs = new HashMap<Integer, ArrayList<Boolean>>();


            HashSet<Integer> tharwaEGYWords = new HashSet<Integer>(tharwaDic.get(tharwaID).getEGY_words());
            HashSet<Integer> tharwaMSAWords = tharwaDic.get(tharwaID).getMSA_words();
            HashSet<Integer> tharwaEnEquivs = tharwaDic.get(tharwaID).getEn_equivs();
            int tharwaEGY_POS_LDC = tharwaDic.get(tharwaID).getEGY_POS_LDC();
            int tharwaMSA_POS_LDC = tharwaDic.get(tharwaID).getMSA_POS_LDC();

            int total_size_of_EGY_match_set = 0;
            boolean MSA_found_in_MSA_En = false;

            for (int tharwaEn : tharwaEnEquivs) {
                System.out.println("finding EGY match set for En equiv: " + Indexing.get_En_Word(tharwaEn));
                Object[] obj = findEGYMath_singleEN(MSA_En_AlignmentDic, En_DA_AlignmentDic, tharwaMSAWords, tharwaEn, tharwaMSA_POS_LDC, consider_transDic_pos_match, consider_thrawa_pos_match);

                HashSet<Integer> EGY_set = (HashSet<Integer>) obj[0];
                EGY_set_overal.addAll(EGY_set);


                final boolean found_in_transDict = (Boolean) obj[1];
                final boolean tharwa_pos_matched_with_transDic_pos = (Boolean) obj[2];
                MSA_found_in_MSA_En = (Boolean) obj[3];
                final boolean En_found_in_MSA_En = (Boolean) obj[4];
                final boolean En_found_in_En_DA = (Boolean) obj[5];
                final boolean MSA_En_found_transDict_POS_matched = (Boolean) obj[6];


                EGY_set_4_all_tharwa_EnEquivs.put(tharwaEn, EGY_set);
                match_status_4_all_tharwa_EnEquivs.put(tharwaEn, new ArrayList<Boolean>() {{
                    add(found_in_transDict);
                    add(tharwa_pos_matched_with_transDic_pos);
                    add(En_found_in_MSA_En);
                    add(En_found_in_En_DA);
                    add(MSA_En_found_transDict_POS_matched);
                }});

                total_size_of_EGY_match_set += EGY_set.size();

                System.out.println("EGY match set for the above En equiv: \n");
                print_set_of_indices(EGY_set, "EGY");
            }

            //evaluation step
            boolean matchFound = false;
            int num_EnEquivs_made_match = 0;

            System.out.println("Now finding match with Tharwa EGY");
            for (int tharaEnEqiv : EGY_set_4_all_tharwa_EnEquivs.keySet()) {
                for (int tharwaEGYWord: tharwaEGYWords)
                {
                    if (EGY_set_4_all_tharwa_EnEquivs.get(tharaEnEqiv).contains(tharwaEGYWord)) {
                        //find a match
                        System.out.println("EGY set found for En Equiv: " + Indexing.get_En_Word(tharaEnEqiv) + " contains TharwaEGY --> Found a match!");

                        //matchFound = true;
                        //break;
                    }
                }
                num_EnEquivs_made_match++;
            }



            if (expand_EGY==true)
            {
                if (expand_tharwa_EGY==true)
                {
                    //expand Tharwa EGY
                    if (expand_tharwa_EGY_with_word_clusters==true) {
                        HashSet<Integer> tharwaEGYWords_expanded = WordClusters.expand_EGY_set_with_clusters(tharwaEGYWords, word_cluster_map, cluster_words_map);
                        tharwaEGYWords_expanded.addAll(tharwaEGYWords);

                        System.out.println("Reference list of EGY words (EGY variants + CODA + MSA):" + tharwaEGYWords_expanded.toString());
                        System.out.println("EGY set genearted by transDic:" + EGY_set_overal.toString());

                        EGY_set_overal.retainAll(tharwaEGYWords_expanded);

                        System.out.println("COMMON EGY WORDS: " + EGY_set_overal.toString());
                        if (EGY_set_overal.size() > 0) {
                            num_matched_rows_EGY++;
                            matchFound = true;
                        }
                    }else if (expand_tharwa_EGY_with_word_clusters==false)
                    {
                        HashSet<Integer> tharwaEGYWords_expanded = WordClusters.expand_EGY_set_with_synonyms(tharwaEGYWords,EGY_synonym_map);
                        tharwaEGYWords_expanded.addAll(tharwaEGYWords);

                        System.out.println("Reference list of EGY words (EGY variants + CODA + MSA):" + tharwaEGYWords_expanded.toString());
                        System.out.println("EGY set genearted by transDic:" + EGY_set_overal.toString());

                        EGY_set_overal.retainAll(tharwaEGYWords_expanded);

                        System.out.println("COMMON EGY WORDS: " + EGY_set_overal.toString());
                        if (EGY_set_overal.size() > 0) {
                            num_matched_rows_EGY++;
                            matchFound = true;
                        }
                    }

                }
                else
                {
                    //expand transDic EGY
                    HashSet<Integer> EGY_set_overal_expanded= WordClusters.expand_EGY_set_with_clusters(EGY_set_overal, word_cluster_map, cluster_words_map);
                    EGY_set_overal_expanded.addAll(EGY_set_overal);

                    System.out.println("Reference list of EGY words (EGY variants + CODA + MSA):" + tharwaEGYWords.toString());
                    System.out.println("EGY set genearted by transDic:" + EGY_set_overal_expanded.toString());

                    EGY_set_overal_expanded.retainAll(tharwaEGYWords);


                    System.out.println("COMMON EGY WORDS: " + EGY_set_overal_expanded.toString());
                    if (EGY_set_overal_expanded.size() > 0) {
                        num_matched_rows_EGY++;
                        matchFound= true;
                    }
                }
            }
            else
            {
                //no EGY expansion
                System.out.println("Reference list of EGY words (EGY variants + CODA + MSA):" + tharwaEGYWords.toString());
                System.out.println("EGY set genearted by transDic:" + EGY_set_overal.toString());

                EGY_set_overal.retainAll(tharwaEGYWords);


                System.out.println("COMMON EGY WORDS: " + EGY_set_overal.toString());
                if (EGY_set_overal.size() > 0) {
                    num_matched_rows_EGY++;
                    matchFound= true;
                }
            }



            if (matchFound == true) {
                matchedEntriesWriter.write(tharwaID + "\n");
                //num_matched_rows_EGY++;
                System.out.println("EGY Match Found for Tharwa Row: " + tharwaID);
            } else {
                //match not found
                System.out.println("EGY Match NOT Found for Tharwa Row: " + tharwaID);
                if (total_size_of_EGY_match_set == 0) {

                    if (MSA_found_in_MSA_En == false)
                        MSA_not_found_in_MSA_En_writer.write(tharwaID + "\n");


                    boolean found_in_transDic_overal = false;
                    boolean pos_matched_overal = false;
                    boolean En_found_in_MSA_En_overal = false;
                    boolean En_found_in_En_DA_overal = false;
                    boolean MSA_En_found_transDic_pos_matched_overal = false;

                    for (int tharaEnEqiv : match_status_4_all_tharwa_EnEquivs.keySet()) {

                        System.out.println("Tharwa ID " + tharwaID + " En equiv: " + tharaEnEqiv + " found_in_transDic: " + match_status_4_all_tharwa_EnEquivs.get(tharaEnEqiv).get(0) +
                                " pos_matche: " + match_status_4_all_tharwa_EnEquivs.get(tharaEnEqiv).get(1));


                        found_in_transDic_overal = Boolean.logicalOr(found_in_transDic_overal, match_status_4_all_tharwa_EnEquivs.get(tharaEnEqiv).get(0));
                        pos_matched_overal = Boolean.logicalOr(pos_matched_overal, match_status_4_all_tharwa_EnEquivs.get(tharaEnEqiv).get(1));

                        En_found_in_MSA_En_overal = Boolean.logicalOr(En_found_in_MSA_En_overal, match_status_4_all_tharwa_EnEquivs.get(tharaEnEqiv).get(2));
                        En_found_in_En_DA_overal = Boolean.logicalOr(En_found_in_En_DA_overal, match_status_4_all_tharwa_EnEquivs.get(tharaEnEqiv).get(3));
                        MSA_En_found_transDic_pos_matched_overal = Boolean.logicalOr(MSA_En_found_transDic_pos_matched_overal, match_status_4_all_tharwa_EnEquivs.get(tharaEnEqiv).get(4));

                    }

                    if (En_found_in_MSA_En_overal == false)
                        En_not_found_in_MSA_En_writer.write(tharwaID + "\n");
                    if (En_found_in_En_DA_overal == false)
                        En_not_found_in_En_DA_writer.write(tharwaID + "\n");
                    if (MSA_En_found_transDic_pos_matched_overal == false)
                        MSA_En_found_transDic_pos_not_matched_writer.write(tharwaID + "\n");


                    if (found_in_transDic_overal == false)
                        MSA_En_not_found_in_transDic.write(tharwaID + "\n");
                    if (found_in_transDic_overal == true && pos_matched_overal == false)
                        MSA_En_found_in_transDic_pos_not_matched.write(tharwaID + "\n");
                    if ((found_in_transDic_overal == true && pos_matched_overal == true) || (found_in_transDic_overal == false && pos_matched_overal == true))
                        System.out.println("ERROR! LOOK at line 884 of your code!");

                    System.out.println("Final Decision: Found_in_TransDic: " + found_in_transDic_overal + "\n POS_matched: " + pos_matched_overal);

                } else {
                    MSA_En_found_in_transDic_pos_matched_EGY_not_matched.write(tharwaID + "\n");
                }
            }


            System.out.println("num_EnEquivs_made_match for this EGY: " + num_EnEquivs_made_match);
            EnEquivs_made_match.put(tharwaID, num_EnEquivs_made_match / (tharwaEnEquivs.size()));
        }

        System.out.println("Number of rows with matched EGY " + num_matched_rows_EGY);

        matchedEntriesWriter.flush();
        matchedEntriesWriter.close();

        MSA_En_not_found_in_transDic.flush();
        MSA_En_not_found_in_transDic.close();

        MSA_En_found_in_transDic_pos_not_matched.flush();
        MSA_En_found_in_transDic_pos_not_matched.close();

        MSA_En_found_in_transDic_pos_matched_EGY_not_matched.flush();
        MSA_En_found_in_transDic_pos_matched_EGY_not_matched.close();

        MSA_not_found_in_MSA_En_writer.flush();
        MSA_not_found_in_MSA_En_writer.close();

        En_not_found_in_MSA_En_writer.flush();
        En_not_found_in_MSA_En_writer.close();

        En_not_found_in_En_DA_writer.flush();
        En_not_found_in_En_DA_writer.close();

        MSA_En_found_transDic_pos_not_matched_writer.flush();
        MSA_En_found_transDic_pos_not_matched_writer.close();

        return num_matched_rows_EGY;
    }


    public static Object[] findEGYMath_singleEN(HashMap<Integer, HashMap<Integer, HashSet<Integer>>> MSA_En_AlignmentDic,
                                                HashMap<Integer, HashMap<Integer, HashSet<Integer>>> En_DA_AlignmentDic,
                                                HashSet<Integer> tharwaMSAWords, int tharwaEN, int tharwaMSA_POS,
                                                boolean consider_transDic_pos_match, boolean consider_tharwa_pos_match)
    {
        System.out.println("Finding EGY match set for \nEn: " + Indexing.get_En_Word(tharwaEN) + "\nMSA: ");
        print_set_of_indices(tharwaMSAWords, "MSA");
        System.out.println("\n MSA_POS: " + Indexing.get_POS(tharwaMSA_POS));


        boolean MSA_found_in_MSA_En = false;
        boolean En_found_in_MSA_En = false;
        boolean En_found_in_En_DA = false;
        boolean MSA_En_found_transDic_POS_matched = false;

        boolean found_in_transDict = false;
        boolean tharwa_pos_matched_with_transDic_pos = false;

        HashSet<Integer> EGY_set = new HashSet<Integer>();


        for (int tharwaMSA: tharwaMSAWords)
        {
            if (MSA_En_AlignmentDic.containsKey(tharwaMSA)) {
                MSA_found_in_MSA_En = true;

                if (MSA_En_AlignmentDic.get(tharwaMSA).containsKey(tharwaEN)) {
                    En_found_in_MSA_En = true;
                    System.out.println("Found MSA-En in MSA_En_AlignmentDic");

                    //MSA-En is found
                    HashSet<Integer> trandsDic_MSA_pos_tags= new HashSet<Integer>(MSA_En_AlignmentDic.get(tharwaMSA).get(tharwaEN));

                    System.out.println("TransDic_MSA_POS: " );print_set_of_indices(trandsDic_MSA_pos_tags,"POS");

                    if (En_DA_AlignmentDic.containsKey(tharwaEN)) {

                        En_found_in_En_DA = true;

                        System.out.println("Found En-EGY in En_DA_AlignmentDic");

                        for (int DA : En_DA_AlignmentDic.get(tharwaEN).keySet()) {
                            HashSet<Integer> transDic_DA_pos_tags = new HashSet<Integer>(En_DA_AlignmentDic.get(tharwaEN).get(DA));

                            HashSet<Integer> MSA_DA_common_pos_tags= new HashSet<Integer>(transDic_DA_pos_tags);
                            MSA_DA_common_pos_tags.retainAll(trandsDic_MSA_pos_tags);


                            System.out.println("DA word: " + Indexing.get_DIA_Word(DA) + " with POS ");
                            print_set_of_indices(transDic_DA_pos_tags, "POS");

                            if (consider_transDic_pos_match == true) {

                                for (int MSA_DA_common_pos: MSA_DA_common_pos_tags)
                                {
                                    MSA_En_found_transDic_POS_matched = true;
                                    found_in_transDict = true;
                                    System.out.println("DA_POS == MSA_POS --> qualified for TransDic triplet!");

                                    //qualifies for being a transDic tuple but should still check for tharwa POS match
                                    if (consider_tharwa_pos_match == true) {

                                        if (MSA_DA_common_pos == tharwaMSA_POS) {
                                            tharwa_pos_matched_with_transDic_pos = true;
                                            EGY_set.add(DA);
                                            System.out.println("MSA_POS==tharwaMSA_POS --> qualified for being in the EGY match set!");
                                        } else
                                            tharwa_pos_matched_with_transDic_pos = false;
                                    } else {
                                        //no need to consider pos for tharwa match
                                        EGY_set.add(DA);
                                        System.out.println("qualified for being in the EGY match set!");
                                    }
                                }

                            }
                            else
                            {
                                //no need to consider pos for transDic match
                                found_in_transDict = true;
                                System.out.println("qualified for TransDic triplet!");

                                //qualifies for being a transDic tuple but should still check for tharwa POS match
                                if (consider_tharwa_pos_match == true) {

                                    if (MSA_DA_common_pos_tags.contains(tharwaMSA_POS)) {
                                        tharwa_pos_matched_with_transDic_pos = true;
                                        EGY_set.add(DA);
                                        System.out.println("MSA_POS==tharwaMSA_POS --> qualified for being in the EGY match set!");
                                    }

                                } else {
                                    //no need to consider pos for tharwa match
                                    EGY_set.add(DA);
                                    System.out.println("qualified for being in the EGY match set!");
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                System.out.println("!! Did not Find MSA-En in MSA_En_AlignmentDic");
            }
        }


        return new Object[]{EGY_set, found_in_transDict, tharwa_pos_matched_with_transDic_pos, MSA_found_in_MSA_En, En_found_in_MSA_En, En_found_in_En_DA, MSA_En_found_transDic_POS_matched};
    }


    public static HashMap<Integer, HashSet<Integer>> evaluateEnMatch(HashMap<Integer, HashMap<Integer, HashSet<Integer>>> MSA_En_AlignmentDic,
                                                                     HashMap<Integer, HashMap<Integer, HashSet<Integer>>> DA_En_AlignmentDic,
                                                                     HashMap<Integer, TharwaRow> tharwaDic,
                                                                     HashMap<Integer, HashMap<Integer, HashSet<Integer>>> synonymDic,
                                                                     String outputDir,
                                                                     boolean consider_transDic_pos_match,
                                                                     boolean consider_thrawa_pos_match,
                                                                     boolean consider_synonyms,
                                                                     String prefix) throws IOException {

        BufferedWriter matchedEntriesWriter = new BufferedWriter(new FileWriter(outputDir + "/" + prefix + "_En_matched"));
        System.out.println("Evaluation En Match Started...");

        HashMap<Integer, HashSet<Integer>> En_set_4_tharwa_rows = new HashMap<Integer, HashSet<Integer>>();

        int num_matched_rows_full = 0;
        int num_matched_rows_one = 0;


        for (int tharwaID : tharwaDic.keySet()) {
            //matchedEntriesWriter.write(tharwaID+"\t");

            System.out.println("Tharwa ID: " + tharwaID);

            HashSet<Integer> tharwaEGYWords = tharwaDic.get(tharwaID).getEGY_words();
            HashSet<Integer> tharwaMSAWords = tharwaDic.get(tharwaID).getMSA_words();
            HashSet<Integer> tharwaEnEquivs = tharwaDic.get(tharwaID).getEn_equivs();
            int tharwaEGY_POS_LDC = tharwaDic.get(tharwaID).getEGY_POS_LDC();
            int tharwaMSA_POS_LDC = tharwaDic.get(tharwaID).getMSA_POS_LDC();

            //for (int tharwa_en:tharwaEnEquivs)
            //matchedEntriesWriter.write(Indexing.get_En_Word(tharwa_en)+",");
            //matchedEntriesWriter.write(":\t[");

            HashSet<Integer> En_set = new HashSet<Integer>();

            //evaluation step
            for (int tharwaMSA: tharwaMSAWords)
            {
                for (int tharwaEGY:tharwaEGYWords)
                {
                    if (DA_En_AlignmentDic.containsKey(tharwaEGY) && MSA_En_AlignmentDic.containsKey(tharwaMSA))
                    {
                        System.out.println("Found tharwaEGY in DA_En_Alignment and tharwaMSA in MSA_En_Alignment");


                        HashMap<Integer, HashSet<Integer>> En_aligned_to_DA = DA_En_AlignmentDic.get(tharwaEGY);
                        HashMap<Integer, HashSet<Integer>> En_aligned_to_MSA = MSA_En_AlignmentDic.get(tharwaMSA);

                        Set<Integer> En_aligned_to_DA_keySet = En_aligned_to_DA.keySet();
                        Set<Integer> En_aligned_to_MSA_keySet = En_aligned_to_MSA.keySet();

                        Set<Integer> common_En_between_DA_MSA= new HashSet<Integer>(En_aligned_to_DA_keySet);
                        common_En_between_DA_MSA.retainAll(En_aligned_to_MSA_keySet);

                        System.out.println("En aligned to tharwaEGY:");
                        print_set_of_indices(En_aligned_to_DA_keySet, "En");

                        System.out.println("En aligned to tharwaMSA:");
                        print_set_of_indices(En_aligned_to_MSA_keySet, "En");

                        System.out.println("Common En: \n");
                        print_set_of_indices(En_aligned_to_DA_keySet, "En");


                        for (int En_DA : common_En_between_DA_MSA) {
                            System.out.println("En_DA: " + Indexing.get_En_Word(En_DA));

                            HashSet<Integer> DA_pos_tags = En_aligned_to_DA.get(En_DA);
                            HashSet<Integer> MSA_pos_tags = En_aligned_to_MSA.get(En_DA);

                            HashSet<Integer> common_pos_tags= new HashSet<Integer>(DA_pos_tags);
                            common_pos_tags.retainAll(MSA_pos_tags);


                            System.out.println("DA_pos_tags: "); print_set_of_indices(DA_pos_tags,"POS");
                            System.out.println("MSA_pos_tags: "); print_set_of_indices(MSA_pos_tags, "POS");

                            if (consider_transDic_pos_match == true) {

                                if (consider_thrawa_pos_match == true)
                                {
                                    for (int common_pos:common_pos_tags )
                                    {
                                        //qualified for being a transDic tuple first!
                                        System.out.println("Qualified for being a TransDic triplet!");

                                        if (common_pos == tharwaEGY_POS_LDC) {
                                            System.out.println("DA_pos == tharwaEGY_POS");
                                            System.out.println("Qualified for being in the En match set!");

                                            //finding synonyms of En_DA and adding them to En_match_set

                                            if (consider_synonyms == true) {
                                                if (synonymDic.containsKey(En_DA)) {
                                                    if (synonymDic.get(En_DA).containsKey(tharwaEGY_POS_LDC))
                                                        En_set.addAll(synonymDic.get(En_DA).get(tharwaEGY_POS_LDC));
                                                    else
                                                        System.out.println("synonym dic does not contain pos: " + Indexing.get_POS(tharwaEGY_POS_LDC) + " for En word: " + Indexing.get_En_Word(En_DA));
                                                } else
                                                    System.out.println("synonym dic does not contain En word " + Indexing.get_En_Word(En_DA));

                                                En_set.add(En_DA);
                                            } else {
                                                //no need to add synonyms
                                                En_set.add(En_DA);
                                            }

                                        }
                                    }

                                }else
                                {
                                    for (int common_pos:common_pos_tags)
                                    {
                                        //no need to consider tharwa pos match
                                        System.out.println("Qualified for being in the En match set!");

                                        //finding synonyms of En_DA and adding them to En_match_set

                                        if (consider_synonyms == true) {
                                            if (synonymDic.containsKey(En_DA)) {
                                                if (synonymDic.get(En_DA).containsKey(tharwaEGY_POS_LDC))
                                                    En_set.addAll(synonymDic.get(En_DA).get(tharwaEGY_POS_LDC));
                                                else
                                                    System.out.println("synonym dic does not contain pos: " + Indexing.get_POS(tharwaEGY_POS_LDC) + " for En word: " + Indexing.get_En_Word(En_DA));
                                            } else
                                                System.out.println("synonym dic does not contain En word " + Indexing.get_En_Word(En_DA));

                                            En_set.add(En_DA);
                                        } else {
                                            //no need to add synonyms
                                            En_set.add(En_DA);
                                        }
                                    }
                                }
                            } else
                            {
                                //no need to consider transDic pos match
                                System.out.println("Qualified for being a TransDic triplet!");
                                if (consider_thrawa_pos_match == true)
                                {
                                    if (common_pos_tags.contains(tharwaEGY_POS_LDC)) {
                                        System.out.println("DA_pos == tharwaEGY_POS");
                                        System.out.println("Qualified for being in the En match set!");

                                        //finding synonyms of En_DA and adding them to En_match_set

                                        if (consider_synonyms == true) {
                                            if (synonymDic.containsKey(En_DA)) {
                                                if (synonymDic.get(En_DA).containsKey(tharwaEGY_POS_LDC))
                                                    En_set.addAll(synonymDic.get(En_DA).get(tharwaEGY_POS_LDC));
                                                else
                                                    System.out.println("synonym dic does not contain pos: " + Indexing.get_POS(tharwaEGY_POS_LDC) + " for En word: " + Indexing.get_En_Word(En_DA));
                                            } else
                                                System.out.println("synonym dic does not contain En word " + Indexing.get_En_Word(En_DA));

                                            En_set.add(En_DA);
                                        } else {
                                            //no need to add synonyms
                                            En_set.add(En_DA);
                                        }

                                    }
                                } else {
                                    //no need to consider tharwa pos match
                                    System.out.println("Qualified for being in the En match set!");

                                    //finding synonyms of En_DA and adding them to En_match_set

                                    if (consider_synonyms == true) {
                                        if (synonymDic.containsKey(En_DA)) {
                                            if (synonymDic.get(En_DA).containsKey(tharwaEGY_POS_LDC))
                                                En_set.addAll(synonymDic.get(En_DA).get(tharwaEGY_POS_LDC));
                                            else
                                                System.out.println("synonym dic does not contain pos: " + Indexing.get_POS(tharwaEGY_POS_LDC) + " for En word: " + Indexing.get_En_Word(En_DA));
                                        } else
                                            System.out.println("synonym dic does not contain En word " + Indexing.get_En_Word(En_DA));

                                        En_set.add(En_DA);
                                    } else {
                                        //no need to add synonyms
                                        En_set.add(En_DA);
                                    }
                                }
                            }

                        }
                    }
                }
            }


            //for (int matched_en:En_set)
            //matchedEntriesWriter.write(Indexing.get_En_Word(matched_en)+",");
            //matchedEntriesWriter.write("]\n");

            En_set_4_tharwa_rows.put(tharwaID, En_set);
            //built En set, now find matches!
            En_set.retainAll(tharwaEnEquivs);

            System.out.println("Common En between the match set and Tharwa En equiv: \n");
            print_set_of_indices(En_set, "En");

            if (En_set.size() > tharwaEnEquivs.size())
                System.out.println("There is something wrong here in finding En match for Row " + tharwaID);
            else if (En_set.size() != 0 && (En_set.size() <= tharwaEnEquivs.size())) {
                num_matched_rows_one++;
                matchedEntriesWriter.write(tharwaID + "\n");
            } else if (En_set.size() == tharwaEnEquivs.size())
                num_matched_rows_full++;
        }

        System.out.println("Number of partially matched rows: " + num_matched_rows_one);
        System.out.println("Number of fully matched rows: " + num_matched_rows_full);

        matchedEntriesWriter.flush();
        matchedEntriesWriter.close();

        return En_set_4_tharwa_rows;
    }


    public static void print_set_of_indices(Set<Integer> set, String type) {
        if (type.equals("En")) {
            for (int En : set)
                System.out.print(Indexing.get_En_Word(En) + ",");
            System.out.print("\n");
        }

        else if (type.equals("EGY")) {
            for (int egy : set)
                System.out.print(Indexing.get_DIA_Word(egy) + ",");
            System.out.print("\n");
        }

        else if (type.equals("MSA")) {
            for (int msa : set)
                System.out.print(Indexing.get_MSA_Word(msa) + ",");
            System.out.print("\n");
        }
        else if (type.equals("POS")) {
            for (int pos : set) {
                System.out.print(Indexing.get_POS(pos) + ",");
            }
            System.out.print("\n");
        }

    }

}
